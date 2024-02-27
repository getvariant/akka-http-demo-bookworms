package com.variant.demo.bookworms

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import com.typesafe.scalalogging.LazyLogging
import com.variant.demo.bookworms.api.{Books, Copies, Promo, Root, Users}
import com.variant.demo.bookworms.variant.Variant._

import scala.jdk.OptionConverters._
import scala.concurrent.{ExecutionContext, Future}

class Routes(implicit ec: ExecutionContext) extends LazyLogging {

  import Routes._

  private val rootRoutes = pathEndOrSingleSlash {
    get {
      // GET / - Health page
      complete(Root.get())
    }
  }

  private val booksRoutes = pathPrefix("books") {
    concat(
      pathEnd {
        get {
          implicit ctx => action {
            targetForState() match {
              case Some(stateRequest) =>
                // All went well and we have a state request
                Books.get.transform(commitOrFail(stateRequest))
              case None =>
                // We didn't get a state request.Variant server may be down, or the feature flag we anticipated
                // may be offline.
                Books.get
            }
          }
        }
      },
      path(Segment) { bookId =>
        get {
          implicit ctx => action {
            targetForState() match {
              case Some(stateRequest) =>
                // All went well and we have a state request
                val withReputation = isExperienceLive(stateRequest,"ReputationFF","WithReputation")
                Books.get(bookId.toInt, withReputation).transform(commitOrFail(stateRequest))
              case None =>
                // We didn't get a state request. Variant server may be down, or the feature flag we anticipated
                // may be offline. Defaulting to NoReputation)
                Books.get(bookId.toInt)
            }
          }
        }
      }
    )
  }

  private val copiesRoutes = pathPrefix("copies") {
      concat(
        pathEndOrSingleSlash {
          // Buy a copy.
          put {
            entity(as[String]) {
              body =>
                onSuccess(withBodyAs[Copy](body)(Copies.update)){
                  resp =>
                    UserRegistry.isInactive = false
                    complete(resp)
                }
            }
          }
        },
        path(Segment) { copyId =>
          put {
            // Put hold on a book copy.
            implicit ctx => action {
              targetForState() match {
                case Some(stateRequest) =>
                  // All went well and we have a state request.
                  val withReputation = isExperienceLive(stateRequest, "ReputationFF", "WithReputation")
                  Copies.hold(copyId.toInt, withReputation).transform(commitOrFail(stateRequest))
                case None =>
                  // We didn't get a state request. Most likely cause is that Variant server is down.
                  // Defaulting to the control experience with no SVE events logged."
                  Copies.hold(copyId.toInt, withReputation = false);
              }
            }
          }
        }
      )
  }

  private val userRoutes = pathPrefix("user") {
    concat(
      pathEnd {
        concat(
          get {
            complete(Users.getUser)
          },
        )
      },
      path(Segment) { user =>
        put {
          complete(Users.setUser(user))
        }
      }
    )
  }

  private val promoRoutes = pathPrefix("promo") {
    concat(
      pathEndOrSingleSlash {
        // Current promo message, if any
        get {
          implicit ctx => action {
            val message: String = (for {
              stateRequest <- targetForState()
              liveExperience <- stateRequest.getLiveExperience("FreeShippingExp").toScala
            } yield {
              // We have live experience in the right experiment.
              Option(liveExperience.getParameters.get("threshold")) match {
                case Some(threshold) => s"Free shipping on orders over $$${threshold}"
                case None => ""
              }

            })
              .getOrElse("")

            val resp =   HttpResponse(
              StatusCodes.OK,
              entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, message)
            )
            Future.successful(resp)
          }
        }
      }
    )
  }

  def routes: Route = {
      rootRoutes ~ booksRoutes ~ copiesRoutes ~ userRoutes ~ promoRoutes
  }
}

object Routes extends LazyLogging {

  import akka.http.scaladsl.model.StatusCodes._
  import io.circe._
  import io.circe.parser._

  import scala.reflect.runtime.universe._

  private def action(bloc: => Future[HttpResponse])(implicit ctx: RequestContext): Future[RouteResult] = {
    try {
      ctx.complete(bloc)
    } catch {
      case t: Throwable =>
        logger.error("Unhandled exception:", t)
        ctx.complete(
          HttpResponse(
            StatusCodes.BadRequest,
            entity = s"Something's rotten in the state of Denmark:\n${t.getMessage}"))
    }
  }

  private def withBodyAs[T](body: String)(f: T => Future[HttpResponse])
                             (implicit decoder: Decoder[T], tt: TypeTag[T]): Future[HttpResponse] = {

    parse(body) match {
      case Left(ex) =>
        Future.successful(
          HttpResponse(
            BadRequest,
            entity = s"Exception while parsing JSON in request: ${ex.getMessage()}")
        )
      case Right(json) =>
        json.as[T] match {
          case Left(ex) =>
            val msg = s"Unable to unmarshal request body to class ${tt.tpe.getClass.getName} "
            Future.successful(HttpResponse(BadRequest, entity = msg))
          case Right(t) =>
            f(t)
        }
    }
  }
}
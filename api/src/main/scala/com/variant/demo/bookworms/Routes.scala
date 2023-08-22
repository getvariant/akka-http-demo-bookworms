package com.variant.demo.bookworms

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import com.typesafe.scalalogging.LazyLogging
import com.variant.demo.bookworms.api.{Books, Copies, Root}
import com.variant.demo.bookworms.variant.Variant

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import scala.jdk.OptionConverters._

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
        concat(
          get {
            onSuccess(Books.get)(resp => complete(resp))
          },
        )
      },
      path(Segment) { bookId =>
        get {
          implicit ctx => action {
            Variant.targetForState("BookDetails") match {
              case Some(stateRequest) =>
                // All went well and we have a state request
                val exp = stateRequest.getLiveExperience("ReputationFF").get()
                (exp.getName match {
                  case "Qualified" => Books.get(bookId.toInt)
                  case "Disqualified" => Books.getWithReputation(bookId.toInt)
                })
                  // Commit or fail state request
                  .transform(Variant.responseTransformer(stateRequest))
              case None =>
                // We didn't get a state request. Variant server may be down, or the feature flag we anticipated
                // may be offline. Defaulting to disqualification, i.e. control experience")
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
          put {
            entity(as[String]) {
              body =>
                onSuccess(withBodyAs[Copy](body)(Copies.update))(resp => complete(resp))
            }
          }
        }
        ,
        path(Segment) { copyId =>
          put {
            // Put hold on a book copy.
            // Instrument a Variant experiment.
            implicit ctx => action {
              Variant.targetForState("Checkout") match {
                case Some(stateRequest) =>
                  // All went well and we have a state request.
                  val exp = stateRequest.getLiveExperience("Suggestions").get;
                  logger.debug(s"Targeted for experience $exp")
                  (exp.getName match {
                    case "NoSuggestions" => Copies.hold(copyId.toInt)
                    case "WithSuggestions" => Copies.holdWithSuggestions(copyId.toInt)
                  })
                    // Commit or fail state request
                    .transform(Variant.responseTransformer(stateRequest))
                case None =>
                  // We didn't get a state request. Variant server may be down, or the experiment we anticipated
                  // may be offline. Defaulting to control")
                  Copies.hold(copyId.toInt)
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

  def routes: Route = {
      rootRoutes ~ booksRoutes ~ copiesRoutes ~ userRoutes
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
            entity = s"Something's rotten in the Kingdom of Denmark:\n${t.getMessage}"))
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
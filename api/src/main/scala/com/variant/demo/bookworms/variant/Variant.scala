package com.variant.demo.bookworms.variant

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.headers.Referer
import akka.http.scaladsl.server.RequestContext
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import com.variant.client.{Connection, ServerConnectException, StateRequest, VariantClient, Session}
import com.variant.demo.bookworms.UserRegistry

import java.util.Optional
import scala.util.{Failure, Success, Try}
import scala.jdk.OptionConverters._

/**
 * A few general purpose helper functions.
 */
object Variant extends LazyLogging {

  private val config = ConfigFactory.load()
  private val uri = config.getString("bookworms.variant.uri")
  private val client: VariantClient = VariantClient.build {
    builder => builder.withSessionIdTrackerClass(classOf[SessionIdTrackerAkka])
  }
  private var _connection: Option[Connection] = None

  private def connection(): Option[Connection] = {
    if (_connection.isEmpty) _connection =
      try {
        logger.info("(Re)connecting to Variant schema [" + uri + "]")
        val result = client.connectTo(uri);
        logger.info("Connected to Variant URI [" + uri + "]")
        Some(result)
      }
      catch {
        case sce: ServerConnectException =>
          logger.error(sce.getMessage)
          None
        case t: Throwable =>
          logger.error("Failed to connect to Variant URI [" + uri + "]", t)
          None
      }
    _connection
  }

  def thisVariantSession(implicit ctx: RequestContext): Option[Session] = {
    connection().flatMap(_.getSession(ctx.request).toScala)
  }

  /** Infer the Variant state from the referring page. */
  private def variantState(implicit ctx: RequestContext): String = {
    // Mapping from referer page prefix to Variant state name
    val refererMapping = List(
      "/books/" -> "BookDetails",
      "/checkout/" -> "Checkout",
      "/" -> "Home",
    )
    Referer.parseFromValueString(ctx.request.getHeader("Referer").get().value()) match {
      case Right(refererHeader) =>
        val refererUri = refererHeader.uri.path.toString
          refererMapping.find(e => refererUri.startsWith(e._1)).map(_._2)
            .getOrElse(throw new Exception(s"Unable to map referer uri $refererUri"))
      case Left(_) => throw new Exception("No Referer Header")
    }
  }
  def targetForState()(implicit ctx: RequestContext): Option[StateRequest] = {
    import scala.jdk.OptionConverters._
    try {
      for {
        ssn <- connection().map(
          conn => this.synchronized {
            conn.getOrCreateSession(ctx.request, Optional.of(UserRegistry.currentUser))
          }
        )
        myState <- ssn.getSchema.getState(variantState).toScala
      }
      yield {
        ssn.getAttributes.put("isInactive", UserRegistry.isInactive.toString)
        ssn.targetForState(myState)
      }
    } catch {
      case _: ServerConnectException => None
    }
  }

  def isExperienceLive(stateRequest: StateRequest, variationName: String, experienceName: String): Boolean = {
    stateRequest.getLiveExperience(variationName).stream().anyMatch(exp => exp.getName == experienceName)
  }

  def commitOrFail(req: StateRequest): Try[HttpResponse] => Try[HttpResponse] = {
    case Success(goodHttpResponse) =>
      val wrapper = Array(goodHttpResponse)
      req.commit(wrapper)
      Try(wrapper(0))
    case Failure(ex) =>
      req.fail()
      Failure(ex)
  }
}
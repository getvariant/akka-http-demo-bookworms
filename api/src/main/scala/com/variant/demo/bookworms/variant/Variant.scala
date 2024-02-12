package com.variant.demo.bookworms.variant

import akka.http.scaladsl.model.HttpResponse
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

  def currVariantSession(implicit ctx: RequestContext): Option[Session] = {
    connection().flatMap(_.getSession(ctx.request, Optional.of(UserRegistry.currentUser)).toScala)
  }

  def targetForState(name: String)(implicit ctx: RequestContext): Option[StateRequest] = {
    import scala.jdk.OptionConverters._
    try {
      for {
        ssn <- connection().map(_.getOrCreateSession(ctx.request, Optional.of(UserRegistry.currentUser)))
        myState <- ssn.getSchema.getState(name).toScala
      }
      yield {
        ssn.getAttributes.put("user", UserRegistry.currentUser)
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
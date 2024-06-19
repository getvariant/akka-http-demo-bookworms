package com.variant.demo.bookworms

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.headers.Referer
import akka.http.scaladsl.server.RequestContext
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import com.variant.client.stdlib.SessionIdTrackerAkka
import com.variant.client._
import com.variant.share.schema.State

import java.util.Optional
import scala.jdk.CollectionConverters._
import scala.jdk.OptionConverters._
import scala.util.{Failure, Success, Try}

/**
 * General purpose helper functions.
 */
object Variant extends LazyLogging {

  private val config = ConfigFactory.load()
  private val uri = config.getString("bookworms.variant.uri")
  private val client: VariantClient = VariantClient.build {
    builder => builder.withSessionIdTrackerClass(classOf[SessionIdTrackerAkka])
  }
  private var _connection: Option[Connection] = None

  // The connection to Variant server.
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

  /** Get existing session tracked by the session ID cookie */
  def thisVariantSession(implicit ctx: RequestContext): Option[Session] = {
    connection().flatMap(_.getSession(ctx.request).toScala)
  }

  /** Infer the Variant state from the referring page. */
  private def inferState(ssn: Session)(implicit ctx: RequestContext): Option[State] = {
    val refererUri: String = Referer.parseFromValueString(ctx.request.getHeader("Referer").get().value()) match {
      case Right(refererHeader) => refererHeader.uri.path.toString
      case Left(_) => throw new Exception("No Referer Header")
    }
    ssn.getSchema.getStates.asScala.find(state => refererUri.matches(state.getParameters.get("path")))
  }

  /** Target current session for a given state */
  def targetForState()(implicit ctx: RequestContext): Option[StateRequest] = {
    try {
      for {
        ssn <- connection().map(
          // Avoid concurrent state creation if does not exist. This is only an issue on the very first page,
          // where we don't yet have a variant session.
          conn => this.synchronized {
            conn.getOrCreateSession(ctx.request, Optional.of(UserRegistry.currentUser))
          }
        )
        myState <- inferState(ssn)
      }
      yield {
        ssn.setAttribute("isInactive", UserRegistry.isInactive.toString)
        ssn.targetForState(myState)
      }
    } catch {
      case _: ServerConnectException => None
    }
  }

  /** Is a given experience live in a given state request */
  def isExperienceLive(stateRequest: StateRequest, variationName: String, experienceName: String): Boolean = {
    stateRequest.getLiveExperience(variationName).stream().anyMatch(exp => exp.getName == experienceName)
  }

  def commitOrFail(req: StateRequest): Try[HttpResponse] => Try[HttpResponse] = {
    case Success(goodHttpResponse) =>
      val newHttpResp = req.commit(goodHttpResponse).asInstanceOf[HttpResponse]
      Try(newHttpResp)
    case Failure(ex) =>
      req.fail()
      Failure(ex)
  }
}
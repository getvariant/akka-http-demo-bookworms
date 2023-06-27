package urisman.bookworms.variant

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.RequestContext
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import com.variant.client.{Connection, Session, StateRequest, VariantClient, VariantException}

object Variant extends LazyLogging {

  private val config = ConfigFactory.load()
  private val uri = config.getString("bookworms.variant.uri")
  private val client: VariantClient = VariantClient
    .builder()
    .withSessionIdTrackerClass(classOf[SessionIdTrackerAkka])
    .withTargetingTrackerClass(classOf[TargetingTrackerAkka])
    .build()
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
        case t: Throwable =>
          logger.error("Failed to connect to Variant URI [" + uri + "]", t)
          None
      }
    _connection
  }
  def targetForState(name: String)(implicit ctx: RequestContext): Option[StateRequest] = {
    import scala.jdk.OptionConverters._
    for {
      ssn <- connection().map(_.getOrCreateSession(ctx.request))
      myState <- ssn.getSchema.getState(name).toScala
    }
    yield ssn.targetForState(myState)
  }
}

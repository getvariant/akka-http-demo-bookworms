package urisman.bookworms.variant

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import com.variant.client.{Connection, Session, VariantClient, VariantException}

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
  def getSession(request: Any): Option[Session] = {
    connection().map(_.getOrCreateSession(request))
  }
}

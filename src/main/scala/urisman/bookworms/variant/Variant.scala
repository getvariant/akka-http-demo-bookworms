package urisman.bookworms.variant

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.RequestContext
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import com.variant.client.{Connection, Session, StateRequest, VariantClient, VariantException}

import scala.util.{Failure, Success, Try}

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
    yield {
      println(s"**************** ${ssn.getId}")
      ssn.targetForState(myState)
    }
  }

  def responseTransformer(req: StateRequest): Try[HttpResponse] => Try[HttpResponse] = {
    case Success(goodHttpResponse) =>
      val wrapper = Array(goodHttpResponse)
      req.commit(wrapper)
      Try(wrapper(0))
    case Failure(ex) =>
      req.fail()
      Failure(ex)
  }
}
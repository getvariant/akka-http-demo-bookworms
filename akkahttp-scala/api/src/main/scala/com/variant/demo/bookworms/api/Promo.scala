package com.variant.demo.bookworms.api

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.RequestContext
import com.variant.demo.bookworms.Variant._

import scala.concurrent.Future
import scala.jdk.OptionConverters._

object Promo extends Endpoint {
  def getPromoMessage(implicit req: RequestContext): Future[HttpResponse] = {
    (for {
        ssn <- thisVariantSession
        req <- ssn.getStateRequest.toScala
        exp <- req.getLiveExperience("FreeShippingExp").toScala
      } yield {
        Option(exp.getParameters.get("threshold")) match {
          case Some(threshold) =>
            // A variant
            s"Free shipping on orders over $$${threshold}"
          case None =>
            // Control
            ""
        }
      }) match {
      case Some (message) =>
        // We have a usable message
        Future.successful (respondOk (message))
      case None =>
        // We either couldn't find variant session because the session ID cookie hasn't made it over
        // to the server yet, or no live experience in the FreeShippingExp experiment because the session
        // is disqualified for it. This indicates to the client to retry this call.
        Future.successful(respondOk(""))
    }
  }

}

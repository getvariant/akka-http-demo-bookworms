package com.variant.demo.bookworms.api

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.RequestContext
import com.variant.demo.bookworms.variant.Variant._

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
          // We have the session and a non-control experience
          case Some(threshold) => s"Free shipping on orders over $$${threshold}"
          // Control Experience
          case None => ""
        }
      }) match {
      case Some (message) =>
        // We have a usable message
        Future.successful (respondOk (message))
      case None =>
        // We either couldn't find variant session because the session ID cookie hasn't made it over
        // to the server yet, or no live experience in the FreeShippingExp experiment because the session
        // is disqualified for it. This indicates to the client to retry this call.
        Future.successful(respondNoContent())
    }
  }

}

package com.variant.demo.bookworms.api

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.RequestContext
import com.variant.demo.bookworms.UserRegistry
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
            // We have the session and a non-control experience. Still have to check
            // the user's status, in case the user just bought and is reloading the
            // home page. In this case the qualifier may not have had a chance to re-run yet.
            // This is a hack; the proper solution to concurrent server calls on the same
            // state is to target from JavaScript first before loading the rest.
            /*if (UserRegistry.isInactive) */s"Free shipping on orders over $$${threshold}"
            //else ""
          case None =>
            // Control Experience
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

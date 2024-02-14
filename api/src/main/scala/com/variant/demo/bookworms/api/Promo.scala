package com.variant.demo.bookworms.api

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.RequestContext
import com.variant.demo.bookworms.variant.Variant._

import scala.concurrent.Future
import scala.jdk.OptionConverters._

object Promo extends Endpoint {
  def getPromoMessage(implicit req: RequestContext): Future[HttpResponse] = {
    //println(req)
    // This should not be called from a page that is not instrumented by the FreeShippingExp experiment.
    // There's a race condition between the two AJAX calls made from all pages: the main content and this call,
    // which is only concerned with the promo msg.
    // and thus the live experience already be in session.
    Thread.sleep(1000)
    val promoMessage =
      for {
        ssn <- thisVariantSession
        req <- ssn.getStateRequest.toScala
        exp <- req.getLiveExperience("FreeShippingExp").toScala
        threshold <- Option(exp.getParameters.get("threshold"))
      } yield {
        s"Free shipping on orders over $$${threshold}"
      }
    Future.successful(respondOk(promoMessage.getOrElse("Nothing")))
  }

}

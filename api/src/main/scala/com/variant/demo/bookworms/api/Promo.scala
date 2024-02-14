package com.variant.demo.bookworms.api

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.RequestContext
import com.variant.demo.bookworms.variant.Variant._

import scala.concurrent.Future
import scala.jdk.OptionConverters._

object Promo extends Endpoint {
  def getPromoMessage(implicit req: RequestContext): Future[HttpResponse] = {
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

package com.variant.demo.bookworms.api

import akka.http.scaladsl.model.HttpResponse
import com.variant.demo.bookworms._

import java.text.NumberFormat
import java.util.Locale
import scala.concurrent.{ExecutionContext, Future}
import scala.math.BigDecimal.RoundingMode
import scala.util.Random

object Copies extends Endpoint {

  private def receiptFor(copy: Copy): Receipt = {
    val price = copy.price.setScale(2)
    val tax = copy.price * BigDecimal(0.097).setScale(2, RoundingMode.HALF_EVEN)
    val shipping = BigDecimal(
      // Simulate shipping as a stable random fraction of the price.
      new Random(copy.price.longValue).nextFloat() * price.doubleValue
    ).setScale(2, RoundingMode.HALF_EVEN)
    val total = price + tax + shipping
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    Receipt(format.format(price), format.format(tax), format.format(shipping), format.format(total))
  }

  /**
   * Hold a book and return receipt.
   * In real life we'd update the book record to make it unavailable for a limited period
   * of time to avoid shopping cart collisions. An async process would be needed to clear
   * holds on books that didn't end up being purchased.
   */
  def hold(copyId: Int, withSuggestions: Boolean, withReputation: Boolean)(implicit ec: ExecutionContext): Future[HttpResponse] = {
    val suggestionsF: Future[Seq[Book]] =
      if (withSuggestions) Postgres.getBooks
      else Future.successful(Seq.empty)

    for {
      copyOpt <- Postgres.getCopy(copyId)
      suggestions <- suggestionsF
    } yield {
      copyOpt match {
        case Some(copy) =>
          var receipt = receiptFor(copy)
          if (withReputation) {
            receipt = receipt.copy(withReputation = true)
          }
          if (withSuggestions) {
            receipt = receipt.copy(suggestions = Random.shuffle(suggestions).take(3))
          }
          respondOk(receipt)
        case None => respondBadRequest(s"No copy with ID $copyId")
      }
    }
  }

  def update(copy: Copy)(implicit ec: ExecutionContext): Future[HttpResponse] =
    Postgres.updateCopy(copy)
      .map {
        case true => respondNoContent()
        case false => respondBadRequest(s"No copy with ID ${copy.id}")
      }

}

package com.variant.demo.bookworms.api

import akka.http.scaladsl.model.HttpResponse
import com.variant.demo.bookworms.Postgres
import com.variant.demo.bookworms._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

object Books extends Endpoint {

  /** Get summaries on all books */
  def get(implicit ec: ExecutionContext): Future[HttpResponse] =
    Postgres.getBooks.map(books => respondOk(books))

  /** Get a book's details */
  def get(bookId: Int)(implicit ec: ExecutionContext): Future[HttpResponse] =
    Postgres.getBookDetails(bookId).map(respondOk)

  /** Simulate the new book details code path that includes the seller's reputation */
  def getWithReputation(bookId: Int)(implicit ec: ExecutionContext): Future[HttpResponse] = {
    Postgres.getBookDetails(bookId)
      .map {
        _.map {
          bookDetails => BookDetailsWithReputation(bookDetails, Random.nextInt(4));
        }
      }
      .map(respondOk(_))
  }
}

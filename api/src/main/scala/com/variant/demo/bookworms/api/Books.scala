package com.variant.demo.bookworms.api

import akka.http.scaladsl.model.HttpResponse
import com.variant.demo.bookworms.Postgres
import com.variant.demo.bookworms._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

object Books extends Endpoint {

  /** Get summaries on all books */
  def get(implicit ec: ExecutionContext): Future[HttpResponse] =
    Postgres.getBooks.map(respondOk(_))

  /** Get a book's details */
  def get(bookId: Int, withReputation: Boolean = false)(implicit ec: ExecutionContext): Future[HttpResponse] =
    if (withReputation)
      Postgres.getBookDetails(bookId)
        .map { _.map { bookDetails => BookDetailsWithReputation(bookDetails) } }
        .map(respondOk(_))
    else
      Postgres.getBookDetails(bookId).map(respondOk(_))
}

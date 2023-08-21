package com.variant.demo.bookworms.api

import akka.http.scaladsl.model.HttpResponse
import com.variant.demo.bookworms.Postgres

import scala.concurrent.{ExecutionContext, Future}

object Books extends Endpoint {

  /** Get summaries on all books */
  def get(implicit ec: ExecutionContext): Future[HttpResponse] =
    Postgres.getBooks.map(books => respondOk(books))

  /** Get a book's details */
  def get(bookId: Int)(implicit ec: ExecutionContext): Future[HttpResponse] =
    Postgres.getBookDetails(bookId).map(copies => respondOk(copies))

}

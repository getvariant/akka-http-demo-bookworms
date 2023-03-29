package urisman.bookworms.api

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import io.circe.syntax._
import urisman.bookworms.db.BookwormsDatabase

import scala.concurrent.{ExecutionContext, Future}

object Books extends Endpoint {

  /** Get summaries on all books */
  def get(implicit ec: ExecutionContext): Future[HttpResponse] =
    BookwormsDatabase.getBooks.map(books => respondOk(books))

  /** Get a book's details */
  def get(bookId: Int)(implicit ec: ExecutionContext): Future[HttpResponse] =
    BookwormsDatabase.getBookDetails(bookId).map(copies => respondOk(copies))

}

package com.variant.demo

import io.circe.Decoder.Result
import io.circe._
import io.circe.generic.semiauto._

import java.sql.Date

package object bookworms {

  //// JSON marshalling
  implicit val e1: Codec[java.sql.Date] = new Codec[java.sql.Date]() {
    // Encoder
    override def apply(sqlDate: java.sql.Date): Json =
      Encoder.encodeString(sqlDate.toString)
    // Decoder
    override def apply(c: HCursor): Result[Date] = {
      Decoder.decodeString.map(str => java.sql.Date.valueOf(str)).apply(c)
    }
  }

  case class Author(id: Int, first: String, last: String)
  object Author {
    implicit val codec: Codec[Author] = deriveCodec[Author]
  }

  case class Book(id: Int, isbn: String, title: String, pubDate: java.sql.Date, coverImageUri: String, copies: Int, authors: Seq[Author])
  object Book {
    implicit val codec: Codec[Book] = deriveCodec[Book]
  }

  case class Copy(id: Int, bookId: Int, condition: String, price: BigDecimal, location: String, seller: String, reputation: Int, available: Boolean)
  object Copy {
    implicit val codec: Codec[Copy] = deriveCodec
  }

  case class BookDetails(book: Book, availableCopies: Seq[Copy])
  object BookDetails {
    implicit val coded: Codec[BookDetails] = deriveCodec
  }

  case class BookDetailsWithReputation(book: Book, availableCopies: Seq[Copy], withReputation: Boolean = true)
  object BookDetailsWithReputation {
    def apply(bookDetails: BookDetails): BookDetailsWithReputation = {
      BookDetailsWithReputation(bookDetails.book, bookDetails.availableCopies)
    }
    implicit val coded: Codec[BookDetailsWithReputation] = deriveCodec
  }

  case class Receipt(price: String, tax: String, shipping: String, total: String)

  object Receipt {
    implicit val coded: Codec[Receipt] = deriveCodec
  }

  case class ReceiptWithSuggestions(price: String, tax: String, shipping: String, total: String, suggestions: Seq[Book])

  object ReceiptWithSuggestions {
    implicit val coded: Codec[ReceiptWithSuggestions] = deriveCodec
    def fromReceipt(receipt: Receipt, suggestions: Seq[Book]): ReceiptWithSuggestions =
      new ReceiptWithSuggestions(receipt.price, receipt.tax, receipt.shipping, receipt.total, suggestions)

  }

  //// Exceptions
  case class JsonDecodeException(source: String, target: Class[_])
    extends Exception(s"""Unable to decode JSON string '$source' as class ${target.getName}""")
}

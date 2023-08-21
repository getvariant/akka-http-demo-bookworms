package com.variant.demo.bookworms.api

import akka.http.scaladsl.model.HttpResponse
import urisman.bookworms.Postgres

import scala.concurrent.{ExecutionContext, Future}

object Users extends Endpoint {
  private var currentUser: String = "random"
  def getUser: HttpResponse = respondOk(currentUser)
  def setUser(user: String) = {
    currentUser = user
    respondOk()
  }
}

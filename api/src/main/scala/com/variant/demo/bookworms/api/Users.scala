package com.variant.demo.bookworms.api

import akka.http.scaladsl.model.HttpResponse

object Users extends Endpoint {
  private var currentUser: String = "random"
  def getUser: HttpResponse = respondOk(currentUser)

  def setUser(user: String): HttpResponse = {
    currentUser = user
    respondOk()
  }
}

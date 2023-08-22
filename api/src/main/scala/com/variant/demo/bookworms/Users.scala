package com.variant.demo.bookworms

import akka.http.scaladsl.model.HttpResponse
import com.variant.demo.bookworms.api.Endpoint

object Users extends Endpoint {
  private var currentUser: String = "Everyman"
  def getUser: HttpResponse = respondOk(currentUser)

  def setUser(user: String): HttpResponse = {
    currentUser = user
    respondOk()
  }
}

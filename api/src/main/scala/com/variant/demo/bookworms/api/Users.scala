package com.variant.demo.bookworms.api

import akka.http.scaladsl.model.HttpResponse
import com.variant.demo.bookworms.UserRegistry

object Users extends Endpoint {
  def getUser: HttpResponse = respondOk(UserRegistry.currentUser)

  def setUser(user: String): HttpResponse = {
    UserRegistry.currentUser = user
    respondOk()
  }
}

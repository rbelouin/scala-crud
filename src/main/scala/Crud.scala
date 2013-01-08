package com.rbelouin

import unfiltered.request._
import unfiltered.response._

import unfiltered.netty._

class Crud[A](name: String) extends async.Plan with ServerErrorResponse {
  def intent = {
    case r @ Path(Seg(root :: Nil)) => r match {
      case GET(_) => ()
      case POST(_) => ()
      case DELETE(_) => ()
      case _ => r respond MethodNotAllowed
    }
    case r @ Path(Seg(root :: id :: Nil)) => r match {
      case GET(_) => ()
      case PUT(_) => ()
      case DELETE(_) => ()
      case _ => r respond MethodNotAllowed
    }
  }
}

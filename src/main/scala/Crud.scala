package com.rbelouin

import scalaz._
import Scalaz._

import unfiltered.request._
import unfiltered.response._

import unfiltered.netty._

class Crud[A](name: String) extends async.Plan with ServerErrorResponse {
  def intent = {
    case r @ Path(Seg(root :: Nil)) => r.some.filter(x => name == root).fold(
      _ match {
        case GET(_) => ()
        case POST(_) => ()
        case DELETE(_) => ()
        case _ => r respond MethodNotAllowed
      },
      Pass
    )
    case r @ Path(Seg(root :: id :: Nil)) => r.some.filter(x => name == root).fold(
      _ match {
        case GET(_) => ()
        case PUT(_) => ()
        case DELETE(_) => ()
        case _ => r respond MethodNotAllowed
      },
      Pass
    )
  }
}

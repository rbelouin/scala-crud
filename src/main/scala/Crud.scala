package com.rbelouin.scalacrud

import net.liftweb.json._

import scalaz._
import Scalaz._

import unfiltered.request._
import unfiltered.response._

import unfiltered.netty._

class Crud[A <: AnyRef](name: String)(implicit mf: Manifest[A]) extends async.Plan with ServerErrorResponse {
  implicit val formats = DefaultFormats
  var map = Map.empty[Int,A]

  def intent = {
    case r @ Path(Seg(root :: Nil)) => r.some.filter(x => name == root).fold(
      _ match {
        case GET(_) => r respond Ok ~> ResponseString(map.toString) // weird bug with Serialization.write, use toString fallback
        case POST(_) => JsonParser.parseOpt(Body.string(r)).flatMap(_.extractOpt[A]).fold(
          a => {
            map = map + ((map.keys.some.filter(_.size > 0).getOrElse(List(0)).max + 1, a))
            r respond Created
          },
          r respond BadRequest
        )
        case DELETE(_) => {
          map = Map.empty[Int,A]
          r respond NoContent
        }
        case _ => r respond MethodNotAllowed
      },
      Pass
    )
    case r @ Path(Seg(root :: id :: Nil)) => r.some.filter(x => name == root).fold(
      _ match {
        case GET(_) => id.parseInt.toOption.flatMap(map.get(_)).fold(
          a => r respond Ok ~> ResponseString(Serialization.write(a)),
          r respond NotFound
        )
        case PUT(_) => id.parseInt.toOption.filter(map.get(_).isDefined).fold(
          i => JsonParser.parseOpt(Body.string(r)).flatMap(_.extractOpt[A]).fold(
            a => {
              map = map + ((i, a))
              r respond Ok
            },
            r respond BadRequest
          ),
          r respond NotFound
        )
        case DELETE(_) => id.parseInt.toOption.filter(map.get(_).isDefined).fold(
          i => {
            map = map - i
            r respond Ok
          },
          r respond NotFound
        )
        case _ => r respond MethodNotAllowed
      },
      Pass
    )
  }
}

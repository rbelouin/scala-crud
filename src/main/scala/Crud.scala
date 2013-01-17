package com.rbelouin.scalacrud

import net.liftweb.json._

import scalaz._
import Scalaz._

import unfiltered.request._
import unfiltered.response._

import unfiltered.netty._

class Crud[A <: AnyRef](name: String)(implicit mf: Manifest[A]) extends async.Plan with ServerErrorResponse {
  this: DAOComponent[A] =>

  implicit val formats = DefaultFormats

  def intent = {
    case r @ Path(Seg(root :: Nil)) => r.some.filter(x => name == root).fold(
      _ match {
        case GET(_) => r respond Ok ~> ResponseString(Serialization.write(dao.getAll))
        case POST(_) => JsonParser.parseOpt(Body.string(r)).flatMap(_.extractOpt[A]).fold(
          a => {
            dao.create(a)
            r respond Created
          },
          r respond BadRequest
        )
        case DELETE(_) => {
          dao.removeAll
          r respond NoContent
        }
        case _ => r respond MethodNotAllowed
      },
      Pass
    )
    case r @ Path(Seg(root :: id :: Nil)) => r.some.filter(x => name == root).fold(
      _ match {
        case GET(_) => dao.get(id).fold(
          a => r respond Ok ~> ResponseString(Serialization.write(a)),
          r respond NotFound
        )
        case PUT(_) => (for {
          jvalue  <- JsonParser.parseOpt(Body.string(r)).toSuccess(BadRequest)
          a       <- jvalue.extractOpt[A].toSuccess(BadRequest)
          x       <- dao.update(id, a).toSuccess(NotFound)
        } yield ()).fold(r respond _, x => r respond Ok)
        case DELETE(_) => dao.remove(id).fold(
          x => r respond Ok,
          r respond NotFound
        )
        case _ => r respond MethodNotAllowed
      },
      Pass
    )
  }
}

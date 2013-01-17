package com.rbelouin.scalacrud

import scalaz._
import scalaz.Scalaz._

trait MutableMapDAOComponentImpl[A] extends DAOComponent[A] {
  case class MutableMapDAO[A] extends DAO[A] {
    val map = scala.collection.mutable.Map.empty[String,A]

    override def get(id: String) = map.get(id)
    override def update(id: String, a: A) = map.get(id).map(x => {
      map += ((id,a))
      a
    })
    override def remove(id: String) = map.get(id).map(x => map -= id)
    
    override def getAll = scala.collection.immutable.Map.empty[String,A] ++ map
    override def removeAll = map.empty
    override def create(a: A) = {
      val id = map.keys.flatMap(x => x.parseInt.toOption).toList.some.filter(_.size > 0).map(_.max + 1).getOrElse(0)
      map += ((id.toString, a))
      id.toString
    }
  }
}

package com.rbelouin.scalacrud

trait DAOComponent[A] {
  val dao: DAO[A]
  trait DAO[A] {
    def get(id: String): Option[A]
    def update(id: String, a: A): Option[A]
    def remove(id: String): Option[Unit]

    def create(a: A): String
    def getAll: Map[String,A]
    def removeAll: Unit
  }
}

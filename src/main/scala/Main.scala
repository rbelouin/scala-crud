package com.rbelouin.scalacrud

object Main {
  case class Person(firstName: String, lastName: String, age: Int)

  object PersonCRUD extends Crud[Person]("persons") with RiakDAOComponentImpl[Person] {
    val dao = RiakDAO[Person]("localhost", 8098, "persons")
  }

  def main(args: Array[String]) {
    unfiltered.netty.Http(8080)
    .handler(PersonCRUD)
    .run()
  }
}

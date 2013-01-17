package com.rbelouin.scalacrud

object Main {
  case class Person(firstName: String, lastName: String, age: Int)

  object PersonCRUD extends Crud[Person]("persons") with MutableMapDAOComponentImpl[Person] {
    val dao = MutableMapDAO[Person]
  }

  def main(args: Array[String]) {
    unfiltered.netty.Http(8080)
    .handler(PersonCRUD)
    .run()
  }
}

package com.rbelouin.scalacrud

object Main {
  case class Person(firstName: String, lastName: String, age: Int)
  case class Thing(name: String)

  def main(args: Array[String]) {
    unfiltered.netty.Http(8080)
    .handler(new Crud[Person]("persons"))
    .handler(new Crud[Thing]("things"))
    .run()
  }
}

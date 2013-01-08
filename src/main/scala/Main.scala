package com.rbelouin

object Main {
  def main(args: Array[String]) {
    unfiltered.netty.Http(8080).run()
  }
}

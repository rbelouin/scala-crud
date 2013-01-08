organization := "com.rbelouin"

name := "scala-crud"

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
   "net.databinder" %% "unfiltered-netty-server" % "0.6.5",
   "net.liftweb" %% "lift-json" % "2.5-M3",
   "org.scalaz" %% "scalaz-core" % "7.0.0-M7"
)

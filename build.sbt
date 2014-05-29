organization := "com.pongr"

name := "spracebook"

scalaVersion := "2.10.3"

resolvers ++= Seq(
  "Spray" at "http://repo.spray.io/"
)

libraryDependencies ++= {
  val spray = "1.3.1"
  val akka = "2.3.3"
  Seq(
    "io.spray" % "spray-client" % spray,
    "io.spray" % "spray-json_2.10" % "1.2.6",
    "com.typesafe.akka" % "akka-actor_2.10" % akka,
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
    "org.specs2" %% "specs2" % "2.3.12" % "test"
  )
}

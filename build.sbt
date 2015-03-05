organization := "com.pongr"

name := "spracebook"

scalaVersion := "2.11.5"

resolvers ++= Seq(
  "Spray" at "http://repo.spray.io/",
  "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

)

libraryDependencies ++= {
  val spray = "1.3.2"
  val akka = "2.3.9"
  Seq(
    "io.spray" % "spray-client_2.11" % spray,
    "io.spray" % "spray-json_2.11" % "1.3.1",
    "com.typesafe.akka" % "akka-actor_2.11" % akka,
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
    "joda-time" % "joda-time" % "2.7",
    "org.joda" % "joda-convert" % "1.7"
  )
}

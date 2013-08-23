organization := "com.pongr"

name := "spracebook"

scalaVersion := "2.9.1"

resolvers ++= Seq(
  "Spray" at "http://repo.spray.io/"
)

libraryDependencies ++= {
  val spray = "1.0-M7"
  val akka = "2.0.5"
  Seq(
    "io.spray" % "spray-client" % spray,
    "io.spray" % "spray-json_2.9.2" % "1.2.3",
    "com.typesafe.akka" % "akka-actor" % akka,
    "org.clapper" %% "grizzled-slf4j" % "0.6.10",
    "org.specs2" %% "specs2" % "1.12.4" % "test"
  )
}

seq(sbtrelease.Release.releaseSettings: _*)

//http://www.scala-sbt.org/using_sonatype.html
//https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide
publishTo <<= version { v: String =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots/")
  else                             Some("releases" at nexus + "service/local/staging/deploy/maven2/")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

licenses := Seq("Apache-2.0" -> url("http://opensource.org/licenses/Apache-2.0"))

homepage := Some(url("http://github.com/pongr/spracebook"))

organizationName := "Pongr"

organizationHomepage := Some(url("http://pongr.com"))

description := "Scala signed requests"

pomExtra := (
  <scm>
    <url>git@github.com:pongr/spracebook.git</url>
    <connection>scm:git:git@github.com:pongr/spracebook.git</connection>
  </scm>
  <developers>
    <developer>
      <id>zcox</id>
      <name>Zach Cox</name>
      <url>https://github.com/zcox</url>
    </developer>
    <developer>
      <id>pcetsogtoo</id>
      <name>Byamba Tumurkhuu</name>
      <url>https://github.com/pcetsogtoo</url>
    </developer>
    <developer>
      <id>bayarmunkh</id>
      <name>Bayarmunkh Davaadorj</name>
      <url>https://github.com/bayarmunkh</url>
    </developer>
  </developers>
)

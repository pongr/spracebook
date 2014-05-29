package spracebook

import scala.concurrent.Future
import akka.actor._
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.util.Timeout
import akka.io.IO
import spray.client.pipelining._
import spray.io._
import spray.util._
import spray.http._
import spray.can.Http
import akka.pattern.ask
import HttpMethods._

object SprayCientFacebookApiTest {

  implicit val system = ActorSystem()
  implicit val timeout = Timeout(10 seconds)
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

  val token = "TODO"

  def main(args: Array[String]) {

    for {
      Http.HostConnectorInfo(connector, _) <- IO(Http) ? Http.HostConnectorSetup("graph.facebook.com", 443, true)
      val api = new SprayClientFacebookGraphApi(connector)
      friends <- api.getFriends(token)
      events <- api.getEvents(token)
    } yield {
      println("Result: " + friends)
      println("Result: " + events)
      system.shutdown
    }
  }

}

package spracebook

/* REPL commands for manual testing:
import akka.dispatch.Future
import akka.actor._
import akka.dispatch.Await
import akka.util.duration._
import akka.util.Timeout
import spray.can.client.HttpClient
import spray.client.HttpConduit
import spray.io._
import spray.util._
import spray.http._
import HttpMethods._
import HttpConduit._
import spracebook._
implicit val system = ActorSystem()
val ioBridge = IOExtension(system).ioBridge()
val httpClient = system.actorOf(Props(new HttpClient(ioBridge)))
val conduit = system.actorOf(
  props = Props(new HttpConduit(httpClient, "graph.facebook.com", 443, sslEnabled = true)),
  name = "http-conduit"
)
val fb = new SprayClientFacebookGraphApi(conduit)

// token expired due to password change
val token="CAADomw3tT28BAFTbh3emeRbbmmLoikZChhl7tRG3M3NxrDLtE9V00dNWflEMQBS7C9sOpaH4K864bOQ9wnE2oGJADdm48GvxmZBmBX3svr8ITDp6twXbl36Lw7tMXB95mrM0z9kCZBFw1JnN5gKDUZC9XRmZBqZBZCTAKdaroRQ8jPrY263SuPE"
val goodToken="CAADomw3tT28BAEk3ZC2LlbtN3FY45He07qqo08EtZBa9OullTTdkTgcIKjs7OX5sBD8dMHQUQYFZA4Y3ZCoCe2BolrbmQejMZBZCGzAlIGLP6WOtNrG54UThKOdB2xJVZCUGJKcNyAJabl0CemYY4h7fvfnjm03SYoEFNXZBpZAj246yxu8cbLMul"

fb.getUser(token)

val token = "TODO"

fb.extendToken("TODO", "TODO", "TODO")

fb.debugToken("TODO", "TODO")

fb.newPhotos(token, None)

Await.result(fb.getFriends(token), 1 minutes)

fb.createStory("cuppofjoe:photograph", "cup", "380730112032825", "https://fbcdn-sphotos-e-a.akamaihd.net/hphotos-ak-frc1/481514_10151657606201011_1061656805_n.jpg", "TODO")

fb.createComment("4286226694008", "Check it out! http://cuppofjoe.com", token)

*/

import akka.dispatch.Future
import akka.actor._
import akka.dispatch.Await
import akka.util.duration._
import akka.util.Timeout
import spray.can.client.HttpClient
import spray.client.HttpConduit
import spray.io._
import spray.util._
import spray.http._
import HttpMethods._
import HttpConduit._

object SprayCientFacebookApiTest {
  implicit val system = ActorSystem()
  val ioBridge = IOExtension(system).ioBridge()
  val httpClient = system.actorOf(Props(new HttpClient(ioBridge)))

  val facebookApiConduit = system.actorOf(
    props = Props(new HttpConduit(httpClient, "graph.facebook.com", 443, sslEnabled = true)),
    name = "facebook-api-conduit"
  )
  
  val token = "TODO"

  val fbApi = new SprayClientFacebookGraphApi(facebookApiConduit)

  def main(args: Array[String]) {
    val users = Await.result(fbApi.getLikes("487217224648173", token), 1 minutes)
    println("Result : " + users)
    system.shutdown
  }

}

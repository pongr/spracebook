package spracebook

import org.specs2.mutable._
import spray.json._
import DefaultJsonProtocol._
import scala.io._
import FacebookGraphApiJsonProtocol._

class FacebookGraphApiJsonProtocolSpec extends Specification {
  def slurp(file: String) = Source.fromFile("src/test/resources/" + file).getLines.mkString("\n")

  "The FacebookGraphApiJsonProtocol" should {
    "extract photo name" in {
      val raw = slurp("fbphotos1")
      val photos = raw.asJson.convertTo[Response[Photo]]
      photos.data.size must_== 1
      photos.data(0).name must beSome("I took this in 1963 http://t.co/uMlifaC1ce")
    }
  }
}

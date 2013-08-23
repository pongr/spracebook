package spracebook

import akka.dispatch.Future
import akka.actor.ActorRef
import spray.client.HttpConduit
import HttpConduit._
import spray.http._
import HttpMethods._
import spray.json._
import DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport._
import grizzled.slf4j.Logging
import FacebookGraphApiJsonProtocol._

class SprayClientFacebookGraphApi(conduit: ActorRef) extends FacebookGraphApi with Logging {

  val userFieldParams = "id,username,name,first_name,middle_name,last_name,email,link,gender,picture"
  
  //This works now, via manual testing
  def debugToken(appAccessToken: String, userAccessToken: String): Future[TokenData] = {
    val pipeline: HttpRequest => Future[TokenDataWrapper] = (
      addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> unmarshal[TokenDataWrapper]
    )
    val url = "/debug_token?input_token=%s&access_token=%s" format (userAccessToken, appAccessToken)
    pipeline(Get(url)).map(_.data)
    /*val f = pipeline(Get(url))
    f.onComplete(e => println(e))
    f.map(_.asJson.convertTo[TokenDataWrapper]).map(_.data)*/
  }

  //This works now, via manual testing
  def extendToken(appId: String, appSecret: String, accessToken: String): Future[TokenExtension] = {
    val pipeline: HttpRequest => Future[TokenExtension] = (
      sendReceive(conduit)
      ~> unmarshal[TokenExtension]
    )
    val url = "/oauth/access_token?grant_type=fb_exchange_token&client_id=%s&client_secret=%s&fb_exchange_token=%s" format (appId, appSecret, accessToken)
    pipeline(Get(url))
  }

  //This works now, via manual testing
  def newPhotos(accessToken: String, after: Option[String]): Future[Response[Photo]] = {
    val pipeline: HttpRequest => Future[Response[Photo]] = (
      addHeader("Authorization", "Bearer " + accessToken)
      ~> addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> unmarshal[Response[Photo]]
    )
    //TODO should really get multiple recent photos, then check IDs for previous processing, dates for recency, etc
    //there have been cases where we miss photos because this query only gets the 1 most recent photo
    val url = "/me/photos/uploaded?fields=id,name,images,place,tags&" + (after.map(a => "after="+a).getOrElse("limit=1"))
    pipeline(Get(url))
  }

  //This works now, via manual testing
  def getUser(accessToken: String): Future[User] = {
    val pipeline: HttpRequest => Future[User] = (
      addHeader("Authorization", "Bearer " + accessToken)
      ~> addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> unmarshal[User]
    )
    pipeline(Get("/me?fields=%s" format userFieldParams))
  }

  def getPage(pageId: String): Future[Page] = {
    val pipeline: HttpRequest => Future[Page] = (
      addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> unmarshal[Page]
    )
    pipeline(Get("/" + pageId))
  }

  def getTab(pageId: String, appId: String, token: String): Future[Response[Tab]] = {
    val pipeline: HttpRequest => Future[Response[Tab]] = (
      addHeader("Authorization", "Bearer " + token)
      ~> addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> unmarshal[Response[Tab]]
    )
    pipeline(Get("/%s/tabs/%s" format (pageId, appId)))
  }

  //This works now, via manual testing
  def createStory(action: String, objectName: String, objectId: String, imageUrl: String, message: Option[String], accessToken: String): Future[CreatedStory] = {
    val pipeline: HttpRequest => Future[CreatedStory] = (
      addHeader("Authorization", "Bearer " + accessToken)
      ~> addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> unmarshal[CreatedStory]
    )
    val data = List(
      objectName -> objectId,
      "image[0][url]" -> imageUrl,
      "image[0][user_generated]" -> "true"
    ) ++ (message.toList.map(m => "message" -> m))
    pipeline(Post("/me/" + action, FormData(data.toMap)))
  }

  def createComment(photoId: String, message: String, accessToken: String): Future[CreatedComment] = {
    val pipeline: HttpRequest => Future[CreatedComment] = (
      addHeader("Authorization", "Bearer " + accessToken)
      ~> addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> unmarshal[CreatedComment]
    )
    pipeline(Post("/%s/comments" format photoId, FormData(Map("message" -> message))))
  } 

  def getFriends(accessToken: String): Future[Seq[User]] = {
    val pipeline: HttpRequest => Future[FacebookFriends] = (
      addHeader("Authorization", "Bearer " + accessToken)
      ~> addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> unmarshal[FacebookFriends]
    )
    pipeline(Get("/me/friends?fields=%s" format userFieldParams)).map(_.data)
  }

  def getComments(objectId: String, accessToken: String): Future[Seq[Comment]] = {
    val pipeline: HttpRequest => Future[Response[Comment]] = (
      addHeader("Authorization", "Bearer " + accessToken)
      ~> addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> {s: HttpResponse => println("R : " + s); s}
      ~> unmarshal[Response[Comment]]
    )
    pipeline(Get("/%s/comments" format objectId)).map(_.data)    
  }

  def getLikes(objectId: String, accessToken: String): Future[Seq[User]] = {
    val pipeline: HttpRequest => Future[Response[User]] = (
      addHeader("Authorization", "Bearer " + accessToken)
      ~> addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> unmarshal[Response[User]]
    )
    pipeline(Get("/%s/likes" format objectId)).map(_.data)      
  }
}

package spracebook

import scala.concurrent.Future
import akka.actor.ActorRef
import spray.client.pipelining._
import spray.http._
import HttpMethods._
import spray.json._
import DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport._
import com.typesafe.scalalogging.slf4j.LazyLogging
import FacebookGraphApiJsonProtocol._ 
import akka.util.Timeout
import scala.concurrent.duration._

class SprayClientFacebookGraphApi(conduit: ActorRef) extends FacebookGraphApi with LazyLogging { 

  
  implicit val timeout = Timeout(10 seconds)
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global


  val userFieldParams = "id,username,name,first_name,middle_name,last_name,email,link,gender,picture"
  
  //This works now, via manual testing
  def debugToken(appAccessToken: String, userAccessToken: String): Future[TokenData] = {
    val pipeline: HttpRequest => Future[TokenDataWrapper] = (
      addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> mapErrors
      ~> unmarshal[TokenDataWrapper]
    )

    val url = "/debug_token?input_token=%s&access_token=%s" format (userAccessToken, appAccessToken)
    
    pipeline(Get(url)).map(_.data)
    /*val f = pipeline(Get(url))
    f.onComplete(e => println(e))
    f.map(_.asJson.convertTo[TokenDataWrapper]).map(_.data)*/
  }

  //This works now, via manual testing
  def extendToken(appId: String, appSecret: String, accessToken: String): Future[AccessToken] = {
    val pipeline: HttpRequest => Future[AccessToken] = (
      sendReceive(conduit)
      ~> unmarshal[AccessToken]
    )
    val url = "/oauth/access_token?grant_type=fb_exchange_token&client_id=%s&client_secret=%s&fb_exchange_token=%s" format (appId, appSecret, accessToken)
    pipeline(Get(url))
  }

  def getAccessToken(appId: String, appSecret: String, code: String, redirectUri: String): Future[AccessToken] = {
    //TODO this is basically the same as extendToken() request, just different query params, so extract some reusable function
    val pipeline: HttpRequest => Future[AccessToken] = (
      sendReceive(conduit)
      ~> mapErrors
      ~> unmarshal[AccessToken]
    )
    val url = "/oauth/access_token?client_id=%s&client_secret=%s&code=%s&redirect_uri=%s" format (appId, appSecret, code, redirectUri)
    pipeline(Get(url))
  }

  //This works now, via manual testing
  def newPhotos(accessToken: String, after: Option[String]): Future[Response[Photo]] = {
    val pipeline: HttpRequest => Future[Response[Photo]] = (
      addHeader("Authorization", "Bearer " + accessToken)
      ~> addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> mapErrors
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
      ~> mapErrors
      ~> unmarshal[User]
    )
    pipeline(Get("/me?fields=%s" format userFieldParams))
  }

  def getPage(pageId: String): Future[Page] = {
    val pipeline: HttpRequest => Future[Page] = (
      addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> mapErrors
      ~> unmarshal[Page]
    )
    pipeline(Get("/" + pageId))
  }

  def getTab(pageId: String, appId: String, token: String): Future[Response[Tab]] = {
    val pipeline: HttpRequest => Future[Response[Tab]] = (
      addHeader("Authorization", "Bearer " + token)
      ~> addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> mapErrors
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
      ~> mapErrors
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
      ~> mapErrors
      ~> unmarshal[CreatedComment]
    )
    pipeline(Post("/%s/comments" format photoId, FormData(Map("message" -> message))))
  } 

  def getFriends(accessToken: String): Future[Seq[User]] = {
    val pipeline: HttpRequest => Future[FacebookFriends] = (
      addHeader("Authorization", "Bearer " + accessToken)
      ~> addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> mapErrors
      ~> unmarshal[FacebookFriends]
    )
    pipeline(Get("/me/friends?fields=%s" format userFieldParams)).map(_.data)
  }

  def getComments(objectId: String, accessToken: String): Future[Seq[Comment]] = {
    val pipeline: HttpRequest => Future[Response[Comment]] = (
      addHeader("Authorization", "Bearer " + accessToken)
      ~> addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> mapErrors
      ~> unmarshal[Response[Comment]]
    )
    pipeline(Get("/%s/comments" format objectId)).map(_.data)    
  }

  def getLikes(objectId: String, accessToken: String): Future[Seq[User]] = {
    val pipeline: HttpRequest => Future[Response[User]] = (
      addHeader("Authorization", "Bearer " + accessToken)
      ~> addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> mapErrors
      ~> unmarshal[Response[User]]
    )
    pipeline(Get("/%s/likes" format objectId)).map(_.data)      
  }

  def getSharedPosts(objectId: String, accessToken: String): Future[Seq[Share]] = {
    val pipeline: HttpRequest => Future[Response[Share]] = (
      addHeader("Authorization", "Bearer " + accessToken)
      ~> addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> mapErrors
      ~> unmarshal[Response[Share]]
    )
    pipeline(Get("/%s/sharedposts" format objectId)).map(_.data)     
  }

  //Insight stuff
  def getApplicationOpenGraphActionCreate(appId: String, accessToken: String, since: Long, until: Long): Future[Seq[Insight]] = {
    val pipeline: HttpRequest => Future[Response[Insight]] = (
      addHeader("Authorization", "Bearer " + accessToken)
      ~> addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> mapErrors
      ~> unmarshal[Response[Insight]]
    )
    pipeline(Get("/%s/insights/application_opengraph_action_create?since=%s&until=%s" format (appId, since, until))).map(_.data)     
  }

  def getApplicationOpenGraphActionClick(appId: String, accessToken: String, since: Long, until: Long): Future[Seq[Insight]] = {
    val pipeline: HttpRequest => Future[Response[Insight]] = (
      addHeader("Authorization", "Bearer " + accessToken)
      ~> addHeader("Accept", "application/json")
       ~>sendReceive(conduit)
      ~> mapErrors
      ~> unmarshal[Response[Insight]]
    )
    pipeline(Get("/%s/insights/application_opengraph_story_click?since=%s&until=%s" format (appId, since, until))).map(_.data)     
  }

  def getApplicationOpenGraphActionImpressions(appId: String, accessToken: String, since: Long, until: Long): Future[Seq[Insight]] = {
    val pipeline: HttpRequest => Future[Response[Insight]] = (
      addHeader("Authorization", "Bearer " + accessToken)
      ~> addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> mapErrors
      ~> unmarshal[Response[Insight]]
    )
    pipeline(Get("/%s/insights/application_opengraph_story_impressions?since=%s&until=%s" format (appId, since, until))).map(_.data)     
  }

  val mapErrors = (response: HttpResponse) => {
    import Exceptions._
    if (response.status.isSuccess) response else {

      // https://developers.facebook.com/docs/reference/api/errors/
      response.entity.asString.asJson.convertTo[ErrorResponse].error match {
        case e if e.error_subcode == Some(458) => throw DeAuthorizedException(e.message, e.`type`, e.code, e.error_subcode)
        case e if e.error_subcode == Some(459) => throw NoSessionException(e.message, e.`type`, e.code, e.error_subcode)
        case e if e.error_subcode == Some(460) => throw PasswordChangedException(e.message, e.`type`, e.code, e.error_subcode)
        case e if e.error_subcode == Some(463) => throw AccessTokenExpiredException(e.message, e.`type`, e.code, e.error_subcode)
        case e if e.error_subcode == Some(464) => throw NoSessionException(e.message, e.`type`, e.code, e.error_subcode)
        case e if e.error_subcode == Some(467) => throw InvalidAccessTokenException(e.message, e.`type`, e.code, e.error_subcode)

        case e if e.code == 10 || e.code > 199 && e.code < 300 => throw new FacebookPermissionException(e.message, e.`type`, e.code, e.error_subcode)

        // TODO: extend to cover the most common exceptions
        case e         => throw FacebookException(e.message, e.`type`, e.code, e.error_subcode)
      }
    }
  }

}

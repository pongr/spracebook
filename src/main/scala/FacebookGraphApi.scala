package spracebook

import akka.dispatch.Future
import FacebookGraphApiJsonProtocol._

trait FacebookGraphApi {

  def debugToken(appAccessToken: String, userAccessToken: String): Future[TokenData]

  def extendToken(appId: String, appSecret: String, accessToken: String): Future[AccessToken]

  def getAccessToken(appId: String, appSecret: String, code: String, redirectUri: String): Future[AccessToken]

  def newPhotos(accessToken: String, after: Option[String]): Future[Response[Photo]]

  def getUser(accessToken: String): Future[User]

  def getPage(pageId: String): Future[Page]

  def getTab(pageId: String, appId: String, token: String): Future[Response[Tab]]

  def createStory(action: String, objectName: String, objectId: String, imageUrl: String, message: Option[String], accessToken: String): Future[CreatedStory]

  def createComment(photoId: String, message: String, accessToken: String): Future[CreatedComment]

  def getFriends(accessToken: String): Future[Seq[User]]

  def getComments(objectId: String, accessToken: String): Future[Seq[Comment]]

  def getLikes(objectId: String, accessToken: String): Future[Seq[User]]

  def getSharedPosts(objectId: String, accessToken: String): Future[Seq[Share]]

  //Facebook Insights
  def getApplicationOpenGraphActionCreate(appId: String, accessToken: String, since: Long, until: Long): Future[Seq[Insight]]

  def getApplicationOpenGraphActionClick(appId: String, accessToken: String, since: Long, until: Long): Future[Seq[Insight]]
  
  def getApplicationOpenGraphActionImpressions(appId: String, accessToken: String, since: Long, until: Long): Future[Seq[Insight]]  
}

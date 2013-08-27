package spracebook

import spray.json._

object FacebookGraphApiJsonProtocol extends DefaultJsonProtocol {
  
  case class TokenData(
    app_id: Long, 
    is_valid: Boolean, 
    application: String, 
    user_id: Long, 
    issued_at: Option[Long], 
    expires_at: Long, 
    scopes: Seq[String]
  )

  case class TokenDataWrapper(data: TokenData)

  case class AccessToken(
    access_token: String, 
    expires: Long
  )

  object AccessToken {
    //for unmarshalling response from Graph API: String => AccessToken
    import spray.httpx.unmarshalling.Unmarshaller
    import spray.http.MediaTypes._
    def decode(s: String): String = java.net.URLDecoder.decode(s, "UTF-8")
    implicit val AccessTokenUnmarshaller = Unmarshaller.delegate[String, AccessToken](`text/plain`) { string => 
      val map = string.split('&').map(_.split('=')).map(a => (a(0), decode(a(1)))).toMap
      AccessToken(map("access_token"), map("expires").toLong)
    }
  }

  case class Image(
    width: Int,
    height: Int,
    source: String
  )

  object Image {
    implicit val ordering: Ordering[Image] = new Ordering[Image] {
      def compare(x: Image, y: Image): Int = (y.width*y.height) compare (x.width*x.height)
    }
  }

  case class Photo(
    id: String,
    name: Option[String], //this is the photo caption
    images: Seq[Image]
  )

  case class Cursors(
    after: String,
    before: String
  )

  case class Paging(
    cursors: Option[Cursors],
    next: Option[String],
    previous: Option[String]
  )

  case class Response[T](
    data: Seq[T],
    paging: Option[Paging]
  )

  case class UserProfilePic (url: String, is_silhouette: Boolean)
  case class UserProfilePicContainer (data: UserProfilePic)

  case class User(
    id: String,
    username: Option[String],
    name: Option[String],
    first_name: Option[String],
    middle_name: Option[String],
    last_name: Option[String],
    email: Option[String],
    link: Option[String],
    gender: Option[String],
    picture: Option[UserProfilePicContainer]
  ) {
    // Ignores Facebook default photo
    def profilePic: Option[String] = picture.flatMap(p => if (p.data.is_silhouette) None else Some(p.data.url))
  }

  case class FacebookFriends (data: Seq[User])

  case class Page(id: String, name: String, link: String)

  case class Tab(id: String, name: String, link: String)

  //{"id":"100914593450999","photos":["100914610117664"]}
  case class CreatedStory(
    id: String,
    photos: Seq[String]
  )

  //{"id":"4286226694008_1953664"}
  case class CreatedComment(id: String)

  case class Comment(
    id: String,
    from: User,
    message: String,
    can_remove: Boolean,
    created_time: String,
    like_count: Int,
    user_likes: Boolean
  )

  implicit val tokenDataFormat = jsonFormat7(TokenData)
  implicit val tokenDataWrapperFormat = jsonFormat1(TokenDataWrapper)
  implicit val imageFormat = jsonFormat3(Image.apply)
  implicit val photoFormat = jsonFormat3(Photo)
  implicit val cursorsFormat = jsonFormat2(Cursors)
  implicit val pagingFormat = jsonFormat3(Paging)
  implicit def responseFormat[T : JsonFormat] = jsonFormat2(Response.apply[T])
  implicit val userProfilePic = jsonFormat2(UserProfilePic)
  implicit val userProfilePicContainer = jsonFormat1(UserProfilePicContainer)
  implicit val userFormat = jsonFormat10(User)
  implicit val pageFormat = jsonFormat3(Page)
  implicit val tabFormat = jsonFormat3(Tab)
  implicit val createdStory = jsonFormat2(CreatedStory)
  implicit val createdComment = jsonFormat1(CreatedComment)
  implicit val facebookFriends = jsonFormat1(FacebookFriends)
  implicit val commentFormat = jsonFormat7(Comment)
}

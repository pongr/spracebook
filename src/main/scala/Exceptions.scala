package spracebook

object Exceptions {

  object AccessTokenErrorType extends Enumeration {
    val Invalid, Expired, PasswordChange, DeAuthorized, LoggedOut = Value
  }

  case class FacebookException(message: String) extends RuntimeException(message)

  case class AccessTokenException(message: String, errorType: AccessTokenErrorType.Value) extends RuntimeException(message)

}

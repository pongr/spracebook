package spracebook

object Exceptions {

  object FacebookException {
    def apply(msg: String, etype: String, errorCode: Int, errorSubcode: Option[Int]) = new FacebookException {
      def message = msg
      def exceptionType = etype
      def code = errorCode
      def subcode = errorSubcode
    }
  }

  trait FacebookException extends RuntimeException {
    def message: String
    def exceptionType: String
    def code: Int
    def subcode: Option[Int]

    override def toString = {
      this.getClass.getName + "(" +
      "message: " + message + ", " +
      "exceptionType: " + exceptionType + ", " +
      "code: " + code + ", subcode: " + subcode + ")"
    }
  }

  trait FacebookAccessTokenException extends FacebookException

  case class AccessTokenExpiredException(message: String, exceptionType: String, code: Int, subcode: Option[Int]) extends FacebookAccessTokenException
  case class DeAuthorizedException(message: String, exceptionType: String, code: Int, subcode: Option[Int]) extends FacebookAccessTokenException
  case class PasswordChangedException(message: String, exceptionType: String, code: Int, subcode: Option[Int]) extends FacebookAccessTokenException
  case class NoSessionException(message: String, exceptionType: String, code: Int, subcode: Option[Int]) extends FacebookAccessTokenException
  case class InvalidAccessTokenException(message: String, exceptionType: String, code: Int, subcode: Option[Int]) extends FacebookAccessTokenException


}

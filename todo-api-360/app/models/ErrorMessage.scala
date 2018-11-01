package models

import play.api.libs.json.{Json, OFormat}

case class ErrorMessage (errorCode: Int, errorMessage: String)

object ErrorMessage {
  implicit val ErrorMessageFormats: OFormat[ErrorMessage] = Json.format[ErrorMessage]
}

package models

import play.api.libs.json.{Json, OFormat}

case class OkMessage(okMessage: String)

object OkMessage {
  implicit val OkMessageFormats: OFormat[OkMessage] = Json.format[OkMessage]
}
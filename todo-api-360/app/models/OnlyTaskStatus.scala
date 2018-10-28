package models

import models.TaskStatus.TaskStatus
import play.api.libs.json.{Json, OFormat}

case class OnlyTaskStatus(status: TaskStatus)

object OnlyTaskStatus {
  implicit val TaskFormats: OFormat[OnlyTaskStatus] = Json.format[OnlyTaskStatus]
}
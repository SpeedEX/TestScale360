package models

import models.TaskStatus.TaskStatus
import play.api.libs.json.{Json, OFormat}

case class TaskWithId(id: Long, title: String, body: String, status: TaskStatus)

object TaskWithId {

  implicit val TaskWithIdFormats: OFormat[TaskWithId] = Json.format[TaskWithId]
}

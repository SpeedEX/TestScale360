package models

import models.TaskStatus.TaskStatus
import play.api.libs.json.{Json, OFormat}

case class Task(title: String, body: String, status: TaskStatus) {

  def withId(id: Long): TaskWithId = {
    TaskWithId(id, title, body, status)
  }
}

object Task {
  implicit val TaskFormats: OFormat[Task] = Json.format[Task]
}

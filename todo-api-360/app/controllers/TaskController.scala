package controllers

import javax.inject._
import models._
import play.api.Logger
import play.api.mvc._
import repositories.TaskList

import scala.util.{Failure, Success, Try}
import play.api.libs.json._

@Singleton
class TaskController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  import Task._
  import TaskWithId._

  def tasks() = Action { implicit request: Request[AnyContent] =>
    val allTasks = TaskList.taskList
      .map {
        case (id, task) => Json.toJson(task.withId(id))
      }
      .toArray

    val value: JsValue = Json.toJson(allTasks)

    Logger.info(value.toString())
    Logger.info(allTasks.toString)
    Logger.info(TaskList.taskList.toString)

    Ok(value)
  }

  def getTask(id: Long) = Action { implicit request: Request[AnyContent] =>

    TaskList.taskList.get(id) match {
      case Some(task) => Ok(Json.toJson(task.withId(id)))
      case None => {
        NotFound(s"Task ID $id is not exist")
      }
    }
  }

  def newTask() = Action { implicit request: Request[AnyContent] =>
    request.body.asJson
      .map(jsonBody => {
        Try(jsonBody.as[Task]) match {
          case Success(newTask: Task) => {
            val maxId: Long = if (TaskList.taskList.isEmpty) 0 else TaskList.taskList.keys.max
            val newId = maxId + 1

            TaskList.taskList.put(newId, newTask)
            Ok(s"""task "${Json.toJson(newTask.withId(newId))}" has been added""")
          }
          case Failure(err: Throwable) => {
            Logger.error(s"""cannot deserialize "$jsonBody" to Task object""", err)
            BadRequest(s""""$jsonBody" is invalid""")
          }
        }
      })
      .getOrElse(BadRequest("Task body required"))
  }

  def deleteTask(id: Long) = Action { implicit request: Request[AnyContent] =>
    TaskList.taskList.remove(id) match {
      case Some(_) => Ok(s"Task ID $id has been removed")
      case None => NotFound(s"Task ID $id is not exist")
    }
  }

  def updateTaskStatus(id: Long) = Action { implicit request: Request[AnyContent] =>
    request.body.asJson
      .map(jsonBody => {
        Try(jsonBody.as[OnlyTaskStatus]) match {
          case Success(updatedStatus: OnlyTaskStatus) => {
            TaskList.taskList.get(id) match {
              case Some(existingTask) => {
                TaskList.taskList.remove(id)
                TaskList.taskList.put(id, existingTask.copy(status = updatedStatus.status))
                Ok(s"Task ID $id has been updated: ${Json.toJson(updatedStatus)}")
              }
              case None => NotFound(s"Task ID $id is not exist")
            }
          }
          case Failure(err: Throwable) => {
            Logger.error(s"""cannot deserialize "$jsonBody" to Task object""", err)
            BadRequest(s""""$jsonBody" is invalid""")
          }
        }
      })
      .getOrElse(BadRequest("Task body required"))
  }

  def updateTask(id: Long) = Action { implicit request: Request[AnyContent] =>
    request.body.asJson
      .map(jsonBody => {
        Try(jsonBody.as[Task]) match {
          case Success(updatedTask: Task) => {
            TaskList.taskList.get(id) match {
              case Some(_) => {
                TaskList.taskList.remove(id)
                TaskList.taskList.put(id, updatedTask)
                Ok(s"Task ID $id has been updated: ${Json.toJson(updatedTask)}")
              }
              case None => NotFound(s"Task ID $id is not exist")
            }
          }
          case Failure(err: Throwable) => {
            Logger.error(s"""cannot deserialize "$jsonBody" to Task object""", err)
            BadRequest(s""""$jsonBody" is invalid""")
          }
        }
      })
      .getOrElse(BadRequest("Task body required"))
  }
}


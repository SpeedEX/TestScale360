package controllers

import javax.inject._
import models._
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._
import repositories.TaskListRepository

import scala.util.{Failure, Success, Try}

@Singleton
class TaskController @Inject()(cc: ControllerComponents, repository: TaskListRepository)
  extends AbstractController(cc) {

  import ErrorMessage._
  import OkMessage._
  import Task._
  import TaskWithId._

  def getTasks = Action { implicit request: Request[AnyContent] =>
    val allTasks = repository.getAllTasks
    val value: JsValue = Json.toJson(allTasks)

    Ok(value)
  }

  def getTask(id: Long) = Action { implicit request: Request[AnyContent] =>

    repository.getTask(id) match {
      case Some(task) => Ok(Json.toJson(task))
      case None => NoContent
    }
  }

  def createNewTask() = Action { implicit request: Request[AnyContent] =>
    request.body.asJson
      .map(jsonBody => {
        Try(jsonBody.as[Task]) match {
          case Success(newTask: Task) => {
            repository.addTask(newTask) match {
              case Some(addedTask) => Created(Json.toJson(addedTask)).withHeaders(("Location", s"/tasks/${addedTask.id}"))
              case None => InternalServerError
            }
          }
          case Failure(err: Throwable) => {
            val errMessage = s"""Cannot deserialize "$jsonBody" to Task object"""
            Logger.error(errMessage, err)
            BadRequest(Json.toJson(ErrorMessage(BAD_REQUEST, errMessage)))
          }
        }
      })
      .getOrElse(BadRequest(Json.toJson(ErrorMessage(BAD_REQUEST, "Body required"))))
  }

  def deleteTask(id: Long) = Action { implicit request: Request[AnyContent] =>
    repository.delete(id) match {
      case Some(_) => Ok(Json.toJson(OkMessage(s"Task ID $id has been removed")))
      case None => NoContent
    }
  }

  def updateTaskStatus(id: Long) = Action { implicit request: Request[AnyContent] =>
    request.body.asJson
      .map(jsonBody => {
        Try(jsonBody.as[OnlyTaskStatus]) match {
          case Success(updatedStatus: OnlyTaskStatus) => {
            repository.updateStatus(id, updatedStatus.status) match {
              case Some(updatedTask) => Created(Json.toJson(updatedTask))
              case None => NoContent
            }
          }
          case Failure(err: Throwable) => {
            val errMessage = s"""Cannot deserialize "$jsonBody" to Task object"""
            Logger.error(errMessage, err)
            BadRequest(Json.toJson(ErrorMessage(BAD_REQUEST, errMessage)))
          }
        }
      })
      .getOrElse(BadRequest(Json.toJson(ErrorMessage(BAD_REQUEST, "Body required"))))
  }

  def updateTask(id: Long) = Action { implicit request: Request[AnyContent] =>
    request.body.asJson
      .map(jsonBody => {
        Try(jsonBody.as[Task]) match {
          case Success(updatingTask: Task) => {
            repository.update(updatingTask.withId(id)) match {
              case Some(updatedTask) => Created(Json.toJson(updatedTask))
              case None => NoContent
            }
          }
          case Failure(err: Throwable) => {
            val errMessage = s"""Cannot deserialize "$jsonBody" to Task object"""
            Logger.error(errMessage, err)
            BadRequest(Json.toJson(ErrorMessage(BAD_REQUEST, errMessage)))
          }
        }
      })
      .getOrElse(BadRequest(Json.toJson(ErrorMessage(BAD_REQUEST, "Body required"))))
  }
}


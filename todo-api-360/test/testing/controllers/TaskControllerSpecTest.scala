package testing.controllers

import controllers.TaskController
import models.{Task, TaskStatus, TaskWithId}
import org.mockito.ArgumentMatchers.{eq => eqTo}
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._
import repositories.TaskListRepository
import testing.models.IncompleteTaskForTest


class TaskControllerSpecTest extends PlaySpec with GuiceOneAppPerTest with Injecting with MockitoSugar {

  import Task._
  import TaskWithId._

  "TaskControllerSpec GET /tasks" should {

    "return all tasks as json" in {
      val task1 = TaskWithId(1, "title1", "body1", TaskStatus.Pending)
      val task2 = TaskWithId(2, "title2", "body2", TaskStatus.Done)
      val allTasks = Array(task1, task2)

      val repository = mock[TaskListRepository]

      when(repository.getAllTasks)
        .thenReturn(allTasks)

      val request = FakeRequest(GET, "/tasks")

      val controller = new TaskController(stubControllerComponents(), repository)
      val respond = controller.getTasks().apply(request)

      status(respond) mustBe OK
      contentType(respond) mustBe Some("application/json")
      contentAsJson(respond) mustBe Json.toJson(allTasks)
    }

    "return empty tasks as empty json" in {
      val allTasks = Array[TaskWithId]()

      val repository = mock[TaskListRepository]
      when(repository.getAllTasks)
        .thenReturn(allTasks)

      val request = FakeRequest(GET, "/tasks")

      val controller = new TaskController(stubControllerComponents(), repository)
      val respond = controller.getTasks().apply(request)

      status(respond) mustBe OK
      contentType(respond) mustBe Some("application/json")
      contentAsJson(respond) mustBe Json.toJson(allTasks)
    }
  }

  "TaskControllerSpec GET /tasks/:id" should {

    "return a corrected task when that task id exists" in {
      val taskId = 78L

      val task1 = TaskWithId(taskId, "title1", "body1", TaskStatus.Pending)

      val repository = mock[TaskListRepository]

      when(repository.getTask(eqTo(taskId)))
        .thenReturn(Some(task1))

      val request = FakeRequest(GET, s"/tasks/$taskId")
      //.withBody(Json.toJson(task1))

      val controller = new TaskController(stubControllerComponents(), repository)
      val respond = controller.getTask(taskId).apply(request)

      status(respond) mustBe OK
      contentType(respond) mustBe Some("application/json")
      contentAsJson(respond) mustBe Json.toJson(task1)
    }

    "return NoContent when input task id is not exists" in {
      val taskId = 78L

      val repository = mock[TaskListRepository]

      when(repository.getTask(eqTo(taskId)))
        .thenReturn(None)

      val request = FakeRequest(GET, s"/tasks/$taskId")
      //.withBody(Json.toJson(task1))

      val controller = new TaskController(stubControllerComponents(), repository)
      val respond = controller.getTask(taskId).apply(request)

      status(respond) mustBe NO_CONTENT
      contentType(respond) mustBe None
    }
  }

  "TaskControllerSpec POST /tasks" should {

    "return a created task when that task is created into data store" in {
      val newTaskId = 78L
      val newTask = Task("title1", "body1", TaskStatus.Pending)

      val repository = mock[TaskListRepository]

      when(repository.addTask(eqTo(newTask)))
        .thenReturn(Some(newTask.withId(newTaskId)))

      val request = FakeRequest(POST, s"/tasks")
        .withJsonBody(Json.toJson(newTask))

      val controller = new TaskController(stubControllerComponents(), repository)
      val respond = controller.createNewTask().apply(request)

      status(respond) mustBe CREATED
      contentType(respond) mustBe Some("application/json")
      contentAsJson(respond) mustBe Json.toJson(newTask.withId(newTaskId))
    }

    "return internal error when input task id cannot be created into data store" in {
      val newTask = Task("title1", "body1", TaskStatus.Pending)

      val repository = mock[TaskListRepository]

      when(repository.addTask(eqTo(newTask)))
        .thenReturn(None)

      val request = FakeRequest(POST, s"/tasks")
        .withJsonBody(Json.toJson(newTask))

      val controller = new TaskController(stubControllerComponents(), repository)
      val respond = controller.createNewTask().apply(request)

      status(respond) mustBe INTERNAL_SERVER_ERROR
      contentType(respond) mustBe None
    }
  }

  "TaskControllerSpec POST /tasks/:id/update" should {

    "return an updated task when that task is updated to data store" in {
      val updatingTask = TaskWithId(89, "title1", "body1", TaskStatus.Pending)

      val repository = mock[TaskListRepository]

      when(repository.update(eqTo(updatingTask)))
        .thenReturn(Some(updatingTask))

      val request = FakeRequest(POST, s"/tasks/${updatingTask.id}/update")
        .withJsonBody(Json.toJson(updatingTask.withoutId))

      val controller = new TaskController(stubControllerComponents(), repository)
      val respond = controller.updateTask(updatingTask.id).apply(request)

      status(respond) mustBe CREATED
      contentType(respond) mustBe Some("application/json")
      contentAsJson(respond) mustBe Json.toJson(updatingTask)
    }

    "return NoContent error when input task id cannot be updated to data store" in {
      val updatingTask = TaskWithId(89, "title1", "body1", TaskStatus.Pending)

      val repository = mock[TaskListRepository]

      when(repository.update(eqTo(updatingTask)))
        .thenReturn(None)

      val request = FakeRequest(POST, s"/tasks/${updatingTask.id}/update")
        .withJsonBody(Json.toJson(updatingTask.withoutId))

      val controller = new TaskController(stubControllerComponents(), repository)
      val respond = controller.updateTask(updatingTask.id).apply(request)

      status(respond) mustBe NO_CONTENT
      contentType(respond) mustBe None
    }

    "return BadRequest error when request have no body" in {
      val updatingTask = TaskWithId(89, "title1", "body1", TaskStatus.Pending)

      val repository = mock[TaskListRepository]

      val request = FakeRequest(POST, s"/tasks/${updatingTask.id}/update")

      val controller = new TaskController(stubControllerComponents(), repository)
      val respond = controller.updateTask(updatingTask.id).apply(request)

      status(respond) mustBe BAD_REQUEST
      contentType(respond) mustBe Some("text/plain")
      contentAsString(respond) mustBe "Task body required"
    }

    "return BadRequest error when request have invalid body" in {
      implicit val IncompleteTaskForTestFormats: OFormat[IncompleteTaskForTest] = Json.format[IncompleteTaskForTest]

      val taskId = 78L

      val updatingTask = IncompleteTaskForTest("title1", TaskStatus.Done)

      val repository = mock[TaskListRepository]

      val request = FakeRequest(POST, s"/tasks/$taskId/update")
        .withJsonBody(Json.toJson(updatingTask))

      val controller = new TaskController(stubControllerComponents(), repository)
      val respond = controller.updateTask(taskId).apply(request)

      status(respond) mustBe BAD_REQUEST
      contentType(respond) mustBe Some("text/plain")
      contentAsString(respond) mustBe s""""${Json.toJson(updatingTask)}" is invalid"""
    }
  }

  "TaskControllerSpec POST /tasks/:id/updatestatus" should {

    "return an updated task when that task is updated to data store" in {
      val updatingTask = TaskWithId(89, "title1", "body1", TaskStatus.Pending)

      val repository = mock[TaskListRepository]

      when(repository.updateStatus(eqTo(updatingTask.id), eqTo(updatingTask.status)))
        .thenReturn(Some(updatingTask))

      val request = FakeRequest(POST, s"/tasks/${updatingTask.id}/updatestatus")
        .withJsonBody(Json.toJson(updatingTask.toStatusOnly))

      val controller = new TaskController(stubControllerComponents(), repository)
      val respond = controller.updateTaskStatus(updatingTask.id).apply(request)

      status(respond) mustBe CREATED
      contentType(respond) mustBe Some("application/json")
      contentAsJson(respond) mustBe Json.toJson(updatingTask)
    }

    "return NoContent error when input task id cannot be updated to data store" in {
      val updatingTask = TaskWithId(89, "title1", "body1", TaskStatus.Pending)

      val repository = mock[TaskListRepository]

      when(repository.updateStatus(eqTo(updatingTask.id), eqTo(updatingTask.status)))
        .thenReturn(None)

      val request = FakeRequest(POST, s"/tasks/${updatingTask.id}/updatestatus")
        .withJsonBody(Json.toJson(updatingTask.withoutId))

      val controller = new TaskController(stubControllerComponents(), repository)
      val respond = controller.updateTaskStatus(updatingTask.id).apply(request)

      status(respond) mustBe NO_CONTENT
      contentType(respond) mustBe None
    }

    "return BadRequest error when request have no body" in {
      val updatingTask = TaskWithId(89, "title1", "body1", TaskStatus.Pending)

      val repository = mock[TaskListRepository]

      val request = FakeRequest(POST, s"/tasks/${updatingTask.id}/update")

      val controller = new TaskController(stubControllerComponents(), repository)
      val respond = controller.updateTaskStatus(updatingTask.id).apply(request)

      status(respond) mustBe BAD_REQUEST
      contentType(respond) mustBe Some("text/plain")
      contentAsString(respond) mustBe "Task body required"
    }

    "return BadRequest error when request have invalid body" in {
      implicit val IncompleteTaskForTestFormats: OFormat[IncompleteTaskForTest] = Json.format[IncompleteTaskForTest]

      val taskId = 78L

      val updatingTask = IncompleteTaskForTest("title1", TaskStatus.Done)

      val repository = mock[TaskListRepository]

      val request = FakeRequest(POST, s"/tasks/taskId/update")
        .withJsonBody(Json.toJson(updatingTask))

      val controller = new TaskController(stubControllerComponents(), repository)
      val respond = controller.updateTaskStatus(taskId).apply(request)

      status(respond) mustBe BAD_REQUEST
      contentType(respond) mustBe Some("text/plain")
      contentAsString(respond) mustBe s""""${Json.toJson(updatingTask)}" is invalid"""
    }
  }

  "TaskControllerSpec DELETE /tasks/:id" should {

    "return OK respond when task got removed from data store" in {
      val taskId = 78L

      val task1 = TaskWithId(taskId, "title1", "body1", TaskStatus.Pending)

      val repository = mock[TaskListRepository]

      when(repository.delete(eqTo(taskId)))
        .thenReturn(Some(task1))

      val request = FakeRequest(DELETE, s"/tasks/$taskId")

      val controller = new TaskController(stubControllerComponents(), repository)
      val respond = controller.deleteTask(taskId).apply(request)

      status(respond) mustBe OK
      contentType(respond) mustBe Some("text/plain")
      contentAsString(respond) mustBe s"Task ID $taskId has been removed"
    }

    "return NoContent when task id is not exists" in {
      val taskId = 78L

      val repository = mock[TaskListRepository]

      when(repository.delete(eqTo(taskId)))
        .thenReturn(None)

      val request = FakeRequest(DELETE, s"/tasks/$taskId")

      val controller = new TaskController(stubControllerComponents(), repository)
      val respond = controller.deleteTask(taskId).apply(request)

      status(respond) mustBe NO_CONTENT
      contentType(respond) mustBe None
    }
  }
}


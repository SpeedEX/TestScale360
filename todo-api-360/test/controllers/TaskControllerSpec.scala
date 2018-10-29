package controllers

import models.{Task, TaskStatus, TaskWithId}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import repositories.TaskListRepository
import org.mockito.Mockito._
import play.api.libs.json._


class TaskControllerSpec  extends PlaySpec with GuiceOneAppPerTest with Injecting with MockitoSugar {

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

      val request = FakeRequest(GET, "/task")

      val controller = new TaskController(stubControllerComponents(), repository)
      val tasksResult = controller.tasks().apply(request)

      status(tasksResult) mustBe OK
      contentType(tasksResult) mustBe Some("application/json")
      contentAsJson(tasksResult) mustBe Json.toJson(allTasks)
    }

    "return empty tasks as empty json" in {
      val allTasks = Array[TaskWithId]()

      val repository = mock[TaskListRepository]
      when(repository.getAllTasks)
        .thenReturn(allTasks)

      val request = FakeRequest(GET, "/task")

      val controller = new TaskController(stubControllerComponents(), repository)
      val tasksResult = controller.tasks().apply(request)

      status(tasksResult) mustBe OK
      contentType(tasksResult) mustBe Some("application/json")
      contentAsJson(tasksResult) mustBe Json.toJson(allTasks)
    }
  }
}

package testing.repositories

import models.{TaskStatus, TaskWithId}
import org.scalatest._
import repositories.{TaskListRepository, TaskListStorage}

class TaskListRepositoryTest extends WordSpec with Matchers with BeforeAndAfter {

  val exampleTask1 = TaskWithId(91, "Example Pending 91", "Body example", TaskStatus.Pending)
  val exampleTask2 = TaskWithId(92, "Example Pending 92", "Body example", TaskStatus.Done)
  val exampleTask3 = TaskWithId(93, "Example Pending 93", "Body example", TaskStatus.Pending)

  before {
    TaskListStorage.taskList.clear()
  }
  after {
    TaskListStorage.taskList.clear()
  }

  "getAllTasks" should {

    "return empty array when data store is empty" in {
      // arrange

      // act
      val repository = new TaskListRepository()
      val result = repository.getAllTasks

      // assert
      result shouldBe empty
    }

    "return every tasks in data store" in {
      // arrange
      TaskListStorage.taskList.put(exampleTask1.id, exampleTask1)
      TaskListStorage.taskList.put(exampleTask2.id, exampleTask2)
      TaskListStorage.taskList.put(exampleTask3.id, exampleTask3)

      // act
      val repository = new TaskListRepository()
      val result = repository.getAllTasks

      // assert
      result should have length 3
      result should contain(exampleTask1)
      result should contain(exampleTask2)
      result should contain(exampleTask3)
    }
  }

  "getTask" should {

    "return None when specified id not exist" in {
      // arrange
      TaskListStorage.taskList.put(exampleTask1.id, exampleTask1)
      TaskListStorage.taskList.put(exampleTask2.id, exampleTask2)
      TaskListStorage.taskList.put(exampleTask3.id, exampleTask3)

      // act
      val repository = new TaskListRepository()
      val result = repository.getTask(583432)

      // assert
      result shouldBe None
    }

    "return a tasks that matched the id" in {
      // arrange
      TaskListStorage.taskList.put(exampleTask1.id, exampleTask1)
      TaskListStorage.taskList.put(exampleTask2.id, exampleTask2)
      TaskListStorage.taskList.put(exampleTask3.id, exampleTask3)

      // act
      val repository = new TaskListRepository()
      val result = repository.getTask(exampleTask2.id)

      // assert
      result shouldBe Some(exampleTask2)
    }
  }

  "addTask" should {

    "return newly added task with id back when specified id not exist" in {
      // arrange
      TaskListStorage.taskList.put(exampleTask1.id, exampleTask1)
      TaskListStorage.taskList.put(exampleTask2.id, exampleTask2)

      // act
      val repository = new TaskListRepository()
      val result = repository.addTask(exampleTask3.withoutId)

      // assert
      val newId = Seq(exampleTask1, exampleTask2).map(_.id).max + 1
      val expectedNewTask = exampleTask3.copy(id = newId)

      result shouldBe Some(expectedNewTask)
      val dataStore = TaskListStorage.taskList.toSeq
      dataStore should have length 3
      dataStore should contain(exampleTask1.id, exampleTask1)
      dataStore should contain(exampleTask2.id, exampleTask2)
      dataStore should contain(newId, expectedNewTask)
    }
  }

  "update" should {

    "return newly updated task when update operation is successful" in {
      // arrange
      TaskListStorage.taskList.put(exampleTask1.id, exampleTask1)
      TaskListStorage.taskList.put(exampleTask2.id, exampleTask2)

      val updatingTask = exampleTask2.copy(title = "Update to Done Title", status = TaskStatus.Done)

      // act
      val repository = new TaskListRepository()
      val result = repository.update(updatingTask)

      // assert
      result shouldBe Some(updatingTask)
      val dataStore = TaskListStorage.taskList.toSeq
      dataStore should have length 2
      dataStore should contain(exampleTask1.id, exampleTask1)
      dataStore should contain(updatingTask.id, updatingTask)
    }

    "return None when updating task id is not exists" in {
      // arrange
      TaskListStorage.taskList.put(exampleTask1.id, exampleTask1)
      TaskListStorage.taskList.put(exampleTask2.id, exampleTask2)

      val updatingTask = exampleTask2.copy(id = 77779, title = "Update to Done Title", status = TaskStatus.Done)

      // act
      val repository = new TaskListRepository()
      val result = repository.update(updatingTask)

      // assert
      result shouldBe None
      val dataStore = TaskListStorage.taskList.toSeq
      dataStore should have length 2
      dataStore should contain(exampleTask1.id, exampleTask1)
      dataStore should contain(exampleTask2.id, exampleTask2)
    }
  }

  "updateStatus" should {

    "return newly updated task when update operation is successful" in {
      // arrange
      TaskListStorage.taskList.put(exampleTask1.id, exampleTask1)
      TaskListStorage.taskList.put(exampleTask2.id, exampleTask2)

      val taskWithNewStatus = exampleTask2.copy(status = TaskStatus.Pending)

      // act
      val repository = new TaskListRepository()
      val result = repository.updateStatus(taskWithNewStatus.id, taskWithNewStatus.status)

      // assert
      result shouldBe Some(taskWithNewStatus)
      val dataStore = TaskListStorage.taskList.toSeq
      dataStore should have length 2
      dataStore should contain(exampleTask1.id, exampleTask1)
      dataStore should contain(taskWithNewStatus.id, taskWithNewStatus)
    }

    "return None when updating task id is not exists" in {
      // arrange
      TaskListStorage.taskList.put(exampleTask1.id, exampleTask1)
      TaskListStorage.taskList.put(exampleTask2.id, exampleTask2)

      val taskWithNewStatus = exampleTask2.copy(id = 847864, status = TaskStatus.Pending)

      // act
      val repository = new TaskListRepository()
      val result = repository.updateStatus(taskWithNewStatus.id, taskWithNewStatus.status)

      // assert
      result shouldBe None
      val dataStore = TaskListStorage.taskList.toSeq
      dataStore should have length 2
      dataStore should contain(exampleTask1.id, exampleTask1)
      dataStore should contain(exampleTask2.id, exampleTask2)
    }
  }

  "delete" should {

    "return removed task when task got removed successfully" in {
      // arrange
      TaskListStorage.taskList.put(exampleTask1.id, exampleTask1)
      TaskListStorage.taskList.put(exampleTask2.id, exampleTask2)
      TaskListStorage.taskList.put(exampleTask3.id, exampleTask3)

      // act
      val repository = new TaskListRepository()
      val result = repository.delete(exampleTask2.id)

      // assert
      result shouldBe Some(exampleTask2)
      val dataStore = TaskListStorage.taskList.toSeq
      dataStore should have length 2
      dataStore should contain(exampleTask1.id, exampleTask1)
      dataStore should contain(exampleTask3.id, exampleTask3)
    }

    "return None when deleting task id is not exists" in {
      // arrange
      TaskListStorage.taskList.put(exampleTask1.id, exampleTask1)
      TaskListStorage.taskList.put(exampleTask2.id, exampleTask2)
      TaskListStorage.taskList.put(exampleTask3.id, exampleTask3)

      // act
      val repository = new TaskListRepository()
      val result = repository.delete(14614514)

      // assert
      result shouldBe None
      val dataStore = TaskListStorage.taskList.toSeq
      dataStore should have length 3
      dataStore should contain(exampleTask1.id, exampleTask1)
      dataStore should contain(exampleTask2.id, exampleTask2)
      dataStore should contain(exampleTask3.id, exampleTask3)
    }
  }
}

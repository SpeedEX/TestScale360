package repositories

import models.TaskStatus.TaskStatus
import models.{Task, TaskWithId}

class TaskListRepository() {

  def getTask(id: Long): Option[TaskWithId] = {
    TaskListStorage.taskList.get(id)
  }

  def getAllTasks: Array[TaskWithId] = {
    TaskListStorage.taskList.toArray.map(_._2)
  }

  def addTask(newTask: Task): Option[TaskWithId] = {
    val maxId: Long = if (TaskListStorage.taskList.isEmpty) 0 else TaskListStorage.taskList.keys.max
    val newId = maxId + 1

    TaskListStorage.taskList.put(newId, newTask.withId(newId))

    TaskListStorage.taskList.get(newId)
  }

  def update(updatingTask: TaskWithId): Option[TaskWithId] = {
    TaskListStorage.taskList.get(updatingTask.id).foreach(_ => {
      TaskListStorage.taskList.remove(updatingTask.id)
      TaskListStorage.taskList.put(updatingTask.id, updatingTask)
    })

    TaskListStorage.taskList.get(updatingTask.id)
  }

  def updateStatus(id: Long, newStatus: TaskStatus): Option[TaskWithId] = {
    TaskListStorage.taskList.get(id).foreach(existingTask => {
      TaskListStorage.taskList.remove(id)
      TaskListStorage.taskList.put(id, existingTask.copy(status = newStatus))
    })

    TaskListStorage.taskList.get(id)
  }

  def delete(id: Long): Option[TaskWithId] = {
    TaskListStorage.taskList.remove(id)
  }
}

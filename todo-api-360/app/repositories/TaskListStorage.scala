package repositories

import models.{TaskStatus, TaskWithId}

import scala.collection.concurrent._


object TaskListStorage {
  val taskList: TrieMap[Long, TaskWithId] = {
    val x = new TrieMap[Long, TaskWithId]
    x.put(1, TaskWithId(1, "Example Pending", "Body example", TaskStatus.Pending))
    x.put(2, TaskWithId(2, "Example Done", "Body example", TaskStatus.Done))

    x
  }
}

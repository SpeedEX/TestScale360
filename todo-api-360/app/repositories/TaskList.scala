package repositories

import models.{Task, TaskStatus}

import scala.collection.parallel.mutable
import scala.collection.parallel.mutable.ParHashMap

object TaskList {
  val taskList: ParHashMap[Long, Task] = {
    val x = new mutable.ParHashMap[Long, Task]
    x.put(1, Task("Example Pending", "Body example", TaskStatus.Pending))
    x.put(2, Task("Example Done", "Body example", TaskStatus.Done))

    x
  }
}

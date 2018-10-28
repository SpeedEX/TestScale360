package models

import play.api.libs.json.{Format, JsString, JsSuccess, JsValue}

object TaskStatus extends Enumeration {
  type TaskStatus = Value
  val Pending, Done = Value

//  implicit val taskStatusReads: Reads[TaskStatus] = new Reads[TaskStatus] {
//    def reads(json: JsValue) = JsSuccess(TaskStatus.withName(json.as[String]))
//  }
//  implicit val taskStatusWrites: Writes[TaskStatus] = new Writes[TaskStatus] {
//    override def writes(o: TaskStatus): JsValue = JsString(o.toString)
//  }
  implicit val taskStatusFormat: Format[TaskStatus] = new Format[TaskStatus] {
    def reads(json: JsValue) = JsSuccess(TaskStatus.withName(json.as[String]))
    def writes(status: TaskStatus) = JsString(status.toString)
  }
  //implicit val myEnumReads = Reads.enumNameReads[TaskStatus](TaskStatus.Value)
  //implicit val myEnumWrites = Writes.enumNameWrites
}

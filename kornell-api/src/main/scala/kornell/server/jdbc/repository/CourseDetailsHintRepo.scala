package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.core.entity.Course
import kornell.core.entity.Person
import kornell.server.repository.Entities.newCourse
import kornell.server.jdbc.SQL._ 
import kornell.core.entity.AuditedEntityType
import kornell.core.entity.CourseDetailsHint


class CourseDetailsHintRepo(uuid: String) {

  val finder = sql"select * from CourseDetailsHint where uuid=$uuid"

  def get = finder.get[Course]
  def first = finder.first[Course]
  
  def update(courseDetailsHint: CourseDetailsHint): CourseDetailsHint = {    
    sql"""
    | update CourseDetailsHint c
    | set c.text = ${courseDetailsHint.getText},
    | c.index = ${courseDetailsHint.getIndex}, 
    | c.fontAwesomeClassName = ${courseDetailsHint.getFontAwesomeClassName},
    | where c.uuid = ${courseDetailsHint.getUUID}""".executeUpdate
    
    courseDetailsHint
  }

}

object CourseDetailsHintRepo {
  def apply(uuid: String) = new CourseRepo(uuid)
}
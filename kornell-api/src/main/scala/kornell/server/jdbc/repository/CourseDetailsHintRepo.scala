package kornell.server.jdbc.repository

import java.sql.ResultSet

import kornell.server.jdbc.SQL._ 
import kornell.core.entity.CourseDetailsHint


class CourseDetailsHintRepo(uuid: String) {

  val finder = sql"select * from CourseDetailsHint where uuid=$uuid"

  def get = finder.get[CourseDetailsHint]
  def first = finder.first[CourseDetailsHint]
  
  def update(courseDetailsHint: CourseDetailsHint): CourseDetailsHint = {    
    sql"""
    | update CourseDetailsHint c
    | set c.text = ${courseDetailsHint.getText},
    | c.index = ${courseDetailsHint.getIndex}, 
    | c.fontAwesomeClassName = ${courseDetailsHint.getFontAwesomeClassName}
    | where c.uuid = ${courseDetailsHint.getUUID}""".executeUpdate
    
    courseDetailsHint
  }

}

object CourseDetailsHintRepo {
  def apply(uuid: String) = new CourseDetailsHintRepo(uuid)
}
package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.server.jdbc.SQL._ 
import kornell.core.entity.CourseDetailsSection


class CourseDetailsSectionRepo(uuid: String) {

  val finder = sql"select * from CourseDetailsSection where uuid=$uuid"

  def get = finder.get[CourseDetailsSection]
  def first = finder.first[CourseDetailsSection]
  
  def update(courseDetailsSection: CourseDetailsSection): CourseDetailsSection = {    
    sql"""
    | update CourseDetailsSection s
    | set s.text = ${courseDetailsSection.getText},
    | s.index = ${courseDetailsSection.getIndex}, 
    | s.title = ${courseDetailsSection.getTitle},
    | where s.uuid = ${courseDetailsSection.getUUID}""".executeUpdate
    
    courseDetailsSection
  }

}

object CourseDetailsSectionRepo {
  def apply(uuid: String) = new CourseDetailsSectionRepo(uuid)
}
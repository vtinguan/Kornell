package kornell.server.jdbc.repository

import java.sql.ResultSet
import scala.collection.JavaConverters._
import kornell.core.entity.Course
import kornell.core.entity.Course
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import kornell.server.repository.TOs._
import kornell.server.repository.Entities
import kornell.core.util.UUID
import kornell.core.to.CoursesTO
import kornell.core.entity.AuditedEntityType
import kornell.core.entity.CourseDetailsHint

object CourseDetailsHintsRepo {

  def create(courseDetailsHint: CourseDetailsHint): CourseDetailsHint = {
    if (courseDetailsHint.getUUID == null){
      courseDetailsHint.setUUID(UUID.random)
    }    
    sql"""
    | insert into CourseDetailsHint (uuid,text,entityType,entityUUID,index,fontAwesomeClassName) 
    | values(
    | ${courseDetailsHint.getUUID},
    | ${courseDetailsHint.getText},
    | ${courseDetailsHint.getEntityType.toString}, 
    | ${courseDetailsHint.getEntityUUID},
    | ${courseDetailsHint.getIndex},
    | ${courseDetailsHint.getFontAwesomeClassName})""".executeUpdate
    
    courseDetailsHint
  }  
}
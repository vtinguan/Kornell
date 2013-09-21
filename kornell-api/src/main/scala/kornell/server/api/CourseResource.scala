package kornell.server.api

import scala.collection.JavaConverters._
import javax.ws.rs._
import javax.ws.rs.core._
import kornell.core.shared.data._
import kornell.server.repository.jdbc.Courses
import kornell.core.shared.to.CoursesTO
import kornell.core.shared.to.CourseTO
import kornell.core.shared.to.CoursesTO
import kornell.core.shared.to.CourseTO
import kornell.server.repository.jdbc.Courses

@Produces(Array(CoursesTO.TYPE))
class CourseResource(uuid:String) {
  @GET
  def getCourse(implicit @Context sc: SecurityContext) = 
    Courses.byUUID(uuid)
}

object CourseResource{
  def apply(uuid:String) = new CourseResource(uuid)
}
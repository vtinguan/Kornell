package kornell.api

import scala.collection.JavaConverters._
import javax.ws.rs._
import javax.ws.rs.core._
import kornell.core.shared.data._
import kornell.repository.slick.plain.Courses

@Path("courses")
class CoursesResource {
  @GET
  @Produces(Array(CoursesTO.MIME_TYPE))
  def getCourses(implicit @Context sc: SecurityContext) = 
    Courses.allWithEnrollment
  
  @Path("{uuid}")
  @Produces(Array(CourseTO.MIME_TYPE))
  @GET
  def getCourse(@PathParam("uuid") uuid:String)(implicit @Context sc: SecurityContext) = 
    Courses.byUUID(uuid)
}

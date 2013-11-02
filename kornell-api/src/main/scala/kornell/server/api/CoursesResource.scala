package kornell.server.api

import scala.collection.JavaConverters._
import javax.ws.rs._
import javax.ws.rs.core._
import kornell.core.lom._
import kornell.core.to._
import kornell.server.repository.jdbc.Courses

@Path("courses")
class CoursesResource {
  @GET
  @Produces(Array(CoursesTO.TYPE))
  def getCourses(implicit @Context sc: SecurityContext) = ???
  
  @Path("{uuid}")
  def getCourse(@PathParam("uuid") uuid:String) = CourseResource(uuid)
}

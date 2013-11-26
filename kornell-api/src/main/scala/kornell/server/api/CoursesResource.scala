package kornell.server.api

import scala.collection.JavaConverters._
import javax.ws.rs._
import javax.ws.rs.core._
import kornell.core.lom._
import kornell.core.to._
import kornell.server.repository.jdbc.Courses
import kornell.server.repository.jdbc.Auth
import kornell.server.dev.util.ContentsParser
import kornell.server.repository.s3.S3

@Path("courses")
class CoursesResource {
  
  @Path("{uuid}")
  def getCourse(@PathParam("uuid") uuid:String) = CourseResource(uuid)
}

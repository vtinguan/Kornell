package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.dev.util.ContentsParser
import kornell.server.repository.jdbc.Courses
import kornell.server.repository.s3.S3
import kornell.core.to.CourseTO
import kornell.core.lom.Contents
import kornell.server.repository.jdbc.Auth
import kornell.core.entity.Person
import kornell.core.lom.Content
 
class CourseResource(uuid: String) {
  @GET
  @Produces(Array(CourseTO.TYPE))
  def getCourse(implicit @Context sc: SecurityContext) =
    Courses.byUUID(uuid)

}

object CourseResource {
  def apply(uuid: String) = new CourseResource(uuid)
}
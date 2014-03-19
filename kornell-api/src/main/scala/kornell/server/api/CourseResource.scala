package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.CourseRepo
import kornell.core.entity.Course

class CourseResource(uuid: String) {
  @GET
  @Produces(Array(Course.TYPE))
  def getCourse(implicit @Context sc: SecurityContext) =
    CourseRepo(uuid).get

}

object CourseResource {
  def apply(uuid: String) = new CourseResource(uuid)
}
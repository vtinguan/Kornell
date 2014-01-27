package kornell.server.api

import javax.ws.rs.Produces
import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.CoursesRepo
import javax.ws.rs.GET
import javax.ws.rs.core.Context
import kornell.core.to.CourseTO

class CourseResource(uuid: String) {
  @GET
  @Produces(Array(CourseTO.TYPE))
  def getCourse(implicit @Context sc: SecurityContext) =
    CoursesRepo.byUUID(uuid)

}

object CourseResource {
  def apply(uuid: String) = new CourseResource(uuid)
}
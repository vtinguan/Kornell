package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.AuthRepo
import kornell.core.to.CourseVersionsTO
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.server.repository.Entities

@Path("courseVersions")
class CourseVersionsResource(val courseUUID: String) {
	def this() = this(null)
  
  def create(repositoryUUID:String) = {
    Entities.newCourseVersion(repositoryUUID=repositoryUUID)
  }

  @GET
  @Produces(Array(CourseVersionsTO.TYPE))
  def getCourseVersions(implicit @Context sc: SecurityContext, @QueryParam("courseUUID") courseUUID: String) =
    AuthRepo().withPerson { person =>
      {
        if (courseUUID != null) {
          CourseVersionsRepo.byCourse(courseUUID)
        }
      }
    }
}

object CourseVersionsResource{
  def apply(courseUUID:String) = new CourseVersionsResource(courseUUID)
}

package kornell.api

import scala.collection.JavaConverters._
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.shared.data.BeanFactory
import kornell.dev.Mocks
import kornell.repository.slick.plain.Courses
import kornell.core.shared.data.CoursesTO

@Produces(Array(CoursesTO.MIME_TYPE))
@Path("courses")
class CoursesResource {

  @GET
  def getCourses(@Context sc: SecurityContext) = {
    val username = sc.getUserPrincipal.getName //TODO: Can username be implicit?
    Courses.allWithEnrollment(username) 
  }
}
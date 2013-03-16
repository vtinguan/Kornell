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
import javax.ws.rs.PathParam
import kornell.core.shared.data.CourseTO

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

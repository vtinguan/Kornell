package kornell.server.api

import scala.collection.JavaConverters._
import javax.ws.rs._
import javax.ws.rs.core._
import kornell.core.lom._
import kornell.core.to._
import kornell.server.jdbc.SQL._
import kornell.server.dev.util.ContentsParser
import kornell.server.repository.s3.S3
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.CoursesRepo
import kornell.server.jdbc.repository.CourseRepo
import kornell.core.entity.Course
import kornell.server.util.Conditional.toConditional
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.util.AccessDeniedErr

@Path("courses")
class CoursesResource {
  
  @Path("{uuid}")
  def getCourse(@PathParam("uuid") uuid:String) = CourseResource(uuid)
  
  @GET
  @Produces(Array(CoursesTO.TYPE))
  def getCourses(@QueryParam("fetchChildCourses") fetchChildCourses: String) =
	  AuthRepo().withPerson { person => {
	    	 CoursesRepo.byInstitution(fetchChildCourses == "true", person.getInstitutionUUID)
	  }
  }
  
  @POST
  @Produces(Array(Course.TYPE))
  @Consumes(Array(Course.TYPE))
  def create(course: Course) = {
    CoursesRepo.create(course)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get
}

object CoursesResource {
  def apply() = new CoursesResource();
}
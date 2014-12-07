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
import kornell.server.util.RequirementNotMet
import kornell.server.jdbc.repository.PersonRepo

@Path("courses")
class CoursesResource {
  
  @Path("{uuid}")
  def getCourse(@PathParam("uuid") uuid:String) = CourseResource(uuid)
  
  @GET
  @Produces(Array(CoursesTO.TYPE))
  def getCourses =
	  AuthRepo().withPerson { person => {
	    	 CoursesRepo.byInstitution(person.getInstitutionUUID)
	  }
  }
  
  @POST
  @Produces(Array(Course.TYPE))
  @Consumes(Array(Course.TYPE))
  def create(course: Course) = {
    CoursesRepo.create(course)
  }.requiring(isPlatformAdmin, RequirementNotMet)
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), RequirementNotMet)
   .get
}

object CoursesResource {
  def apply() = new CoursesResource();
}
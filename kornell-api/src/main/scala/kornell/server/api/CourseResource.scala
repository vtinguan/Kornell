package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.CourseRepo
import kornell.core.entity.Course
import kornell.server.util.Conditional.toConditional
import kornell.server.jdbc.repository.PersonRepo
import javax.ws.rs.Consumes
import javax.ws.rs.PUT
import javax.ws.rs.DELETE
import kornell.server.util.AccessDeniedErr

class CourseResource(uuid: String) {
  
  @GET
  @Produces(Array(Course.TYPE))
  def get = {
    CourseRepo(uuid).get
  }.requiring(isPlatformAdmin, AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get
   
  @PUT
  @Consumes(Array(Course.TYPE))
  @Produces(Array(Course.TYPE))
  def update(course: Course) = {
    CourseRepo(uuid).update(course)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get
}

object CourseResource {
  def apply(uuid: String) = new CourseResource(uuid)
}
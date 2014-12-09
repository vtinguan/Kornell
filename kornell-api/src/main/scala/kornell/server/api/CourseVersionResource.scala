package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Produces
import kornell.core.entity.CourseVersion
import kornell.server.jdbc.repository.CourseVersionRepo
import kornell.server.util.Conditional.toConditional
import kornell.server.util.RequirementNotMet
import kornell.server.jdbc.repository.PersonRepo
import javax.ws.rs.PUT
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import kornell.core.to.CourseVersionTO

class CourseVersionResource(uuid: String) {

  //Should be TO cause the UI needs Course
  @GET
  @Produces(Array(CourseVersionTO.TYPE))
  def get : CourseVersionTO  = {
    CourseVersionRepo(uuid).getWithCourse
  }.requiring(isPlatformAdmin, RequirementNotMet)
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), RequirementNotMet)
   .get
   
  @PUT
  @Consumes(Array(CourseVersion.TYPE))
  @Produces(Array(CourseVersion.TYPE))
  def update(courseVersion: CourseVersion) = {
    CourseVersionRepo(uuid).update(courseVersion)
  }.requiring(isPlatformAdmin, RequirementNotMet)
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), RequirementNotMet)
   .get
}

object CourseVersionResource {
  def apply(uuid: String) = new CourseVersionResource(uuid)
}
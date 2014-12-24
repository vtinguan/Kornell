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
import javax.enterprise.context.Dependent
import kornell.server.util.Identifiable
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.auth.Authorizator

@Dependent
class CourseVersionResource(
  val auth:Authorizator,
  val peopleRepo: PeopleRepo) extends Identifiable {
  def this() = this(null,null)

  //Should be TO cause the UI needs Course
  @GET
  @Produces(Array(CourseVersionTO.TYPE))
  def get: CourseVersionTO = {
    CourseVersionRepo(uuid).getWithCourse
  }.requiring(auth.isPlatformAdmin, RequirementNotMet)
    .or(auth.isInstitutionAdmin(peopleRepo.byUUID(auth.getAuthenticatedPersonUUID).get.getInstitutionUUID), RequirementNotMet)
    .get

  @PUT
  @Consumes(Array(CourseVersion.TYPE))
  @Produces(Array(CourseVersion.TYPE))
  def update(courseVersion: CourseVersion) = {
    CourseVersionRepo(uuid).update(courseVersion)
  }.requiring(auth.isPlatformAdmin, RequirementNotMet)
    .or(auth.isInstitutionAdmin(peopleRepo.byUUID(auth.getAuthenticatedPersonUUID).get.getInstitutionUUID), RequirementNotMet)
    .get
}
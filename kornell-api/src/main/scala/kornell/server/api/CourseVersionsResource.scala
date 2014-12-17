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
import javax.ws.rs.PathParam
import javax.ws.rs.POST
import javax.ws.rs.Consumes
import kornell.core.entity.CourseVersion
import kornell.server.util.Conditional.toConditional
import kornell.server.util.RequirementNotMet
import kornell.server.jdbc.repository.PersonRepo
import javax.inject.Inject
import javax.enterprise.inject.Instance
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.jdbc.repository.PeopleRepo

@Path("courseVersions")
class CourseVersionsResource @Inject() (
  val peopleRepo: PeopleRepo,
  val courseVersionResource: Instance[CourseVersionResource],
  val courseVersionsRepo:CourseVersionsRepo) {

  def this() = this(null,null,null)

  @Path("{uuid}")
  def getCourseVersion(@PathParam("uuid") uuid: String) = courseVersionResource.get.withUUID(uuid)

  @GET
  @Produces(Array(CourseVersionsTO.TYPE))
  def getCourseVersions(@QueryParam("courseUUID") courseUUID: String) = {
    if (courseUUID != null)
      courseVersionsRepo.byCourse(courseUUID)
    else
      courseVersionsRepo.byInstitution(peopleRepo.byUUID(getAuthenticatedPersonUUID).get.getInstitutionUUID)
  }.requiring(isPlatformAdmin, RequirementNotMet)
    .or(isInstitutionAdmin(peopleRepo.byUUID(getAuthenticatedPersonUUID).get.getInstitutionUUID), RequirementNotMet)
    .get

  @POST
  @Produces(Array(CourseVersion.TYPE))
  @Consumes(Array(CourseVersion.TYPE))
  def create(courseVersion: CourseVersion) = {
    courseVersionsRepo.create(courseVersion)
  }.requiring(isPlatformAdmin, RequirementNotMet)
    .or(isInstitutionAdmin(peopleRepo.byUUID(getAuthenticatedPersonUUID).get.getInstitutionUUID), RequirementNotMet)
    .get
}

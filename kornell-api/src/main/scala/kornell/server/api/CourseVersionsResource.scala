package kornell.server.api

import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Instance
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import kornell.core.entity.CourseVersion
import kornell.server.auth.Authorizator
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.util.Conditional.toConditional
import kornell.server.util.RequirementNotMet
import kornell.core.to.CourseVersionsTO

@Path("courseVersions")
class CourseVersionsResource @Inject() (
  val auth:Authorizator,
  val peopleRepo: PeopleRepo,
  val courseVersionResource: Instance[CourseVersionResource],
  val courseVersionsRepo:CourseVersionsRepo) {

  def this() = this(null,null,null,null)

  @Path("{uuid}")
  def getCourseVersion(@PathParam("uuid") uuid: String) = courseVersionResource.get.withUUID(uuid)

  @GET
  @Produces(Array(CourseVersionsTO.TYPE))
  def getCourseVersions(@QueryParam("courseUUID") courseUUID: String) = {
    if (courseUUID != null)
      courseVersionsRepo.byCourse(courseUUID)
    else
      courseVersionsRepo.byInstitution(peopleRepo.byUUID(auth.getAuthenticatedPersonUUID).get.getInstitutionUUID)
  }.requiring(auth.isPlatformAdmin, RequirementNotMet)
    .or(auth.isInstitutionAdmin(peopleRepo.byUUID(auth.getAuthenticatedPersonUUID).get.getInstitutionUUID), RequirementNotMet)
    .get

  @POST
  @Produces(Array(CourseVersion.TYPE))
  @Consumes(Array(CourseVersion.TYPE))
  def create(courseVersion: CourseVersion) = {
    courseVersionsRepo.create(courseVersion)
  }.requiring(auth.isPlatformAdmin, RequirementNotMet)
    .or(auth.isInstitutionAdmin(peopleRepo.byUUID(auth.getAuthenticatedPersonUUID).get.getInstitutionUUID), RequirementNotMet)
    .get
}

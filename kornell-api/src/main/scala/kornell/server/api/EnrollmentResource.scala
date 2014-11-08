package kornell.server.api

import scala.language.postfixOps
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.Assessment
import kornell.core.entity.Enrollment
import kornell.core.entity.RoleCategory
import kornell.core.lom.Contents
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.jdbc.repository.EnrollmentRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.jdbc.repository.RegistrationsRepo
import scala.collection.JavaConverters._
import kornell.server.util.Errors._
import kornell.server.util.Conditional.toConditional
import kornell.server.util.Err
import kornell.server.authentication.ThreadLocalAuthenticator
import kornell.server.util.RequirementNotMet
import kornell.server.repository.TOs
import kornell.core.to.ActionType
import kornell.server.jdbc.repository.CourseVersionRepo
import kornell.server.repository.ContentRepository
import kornell.server.jdbc.repository.ContentStoreRepo
import kornell.server.content.ContentManager
import kornell.server.scorm.scorm12.SCORM12PackageManager
import kornell.core.to.ActionTO
import kornell.core.to.InfosTO
import kornell.server.jdbc.repository.InfosRepo
import scala.collection.immutable.TreeMap
import scala.collection.JavaConverters._
import kornell.core.to.CourseDetailsTO
import kornell.core.to.EnrollmentLaunchTO
import kornell.core.entity.ContentStore

@Produces(Array(Enrollment.TYPE))
class EnrollmentResource(uuid: String) {
  lazy val enrollment = get
  lazy val enrollmentRepo = EnrollmentRepo(uuid)

  def get = enrollmentRepo.get

  @GET
  def first = enrollmentRepo.first

  @PUT
  @Produces(Array("text/plain"))
  @Path("acceptTerms")
  def acceptTerms() = AuthRepo().withPerson { p =>
    RegistrationsRepo(p.getUUID, uuid).acceptTerms
  }

  @PUT
  @Produces(Array("text/plain"))
  @Consumes(Array(Enrollment.TYPE))
  def update(enrollment: Enrollment) = {
    EnrollmentRepo(enrollment.getUUID).update(enrollment)
  }
    .requiring(PersonRepo(getAuthenticatedPersonUUID).hasPowerOver(enrollment.getPersonUUID), RequirementNotMet)
    .get

  @Path("actoms/{actomKey}")
  def actom(@PathParam("actomKey") actomKey: String) = ActomResource(uuid, actomKey)

  @GET
  @Path("contents")
  @Produces(Array(Contents.TYPE))
  def contents: Option[Contents] = first map { e =>
    val courseClassResource = CourseClassResource(e.getCourseClassUUID)
    val contents = courseClassResource.getLatestContents()
    contents
  }

  @GET
  @Path("approved")
  @Produces(Array("application/boolean"))
  def approved = first map { Assessment.PASSED == _.getAssessment } get

  @DELETE
  @Produces(Array(Enrollment.TYPE))
  def delete() = {
    val enrollmentRepo = EnrollmentRepo(uuid)
    val enrollment = enrollmentRepo.get
    enrollmentRepo.delete(uuid)
    enrollment
  }.requiring(isPlatformAdmin, UserNotInRole)
    .or(isInstitutionAdmin(CourseClassRepo(EnrollmentRepo(uuid).get.getCourseClassUUID).get.getInstitutionUUID), UserNotInRole)
    .or(isCourseClassAdmin(EnrollmentRepo(uuid).get.getCourseClassUUID), UserNotInRole)

  @GET
  @Path("launch")
  @Produces(Array(EnrollmentLaunchTO.TYPE))
  def launch: Option[EnrollmentLaunchTO] = for {
    e <- first
    cc <- CourseClassRepo(e.getCourseClassUUID).first
    cv <- CourseVersionRepo(cc.getCourseVersionUUID).first
    cs <- ContentStoreRepo(cv.getRepositoryUUID, cv.getDistributionPrefix()).first 
    details <- launchDetails(e) 
  } yield TOs.newEnrollmentLaunchTO(
      launchAction(e,cs), 
      details,
      cv)
      
  
  def launchAction(e: Enrollment, cs: ContentStore): ActionTO = {
    val cm = ContentManager(cs)
    val pm = SCORM12PackageManager(cm)
    pm.launch(e)
  }

  def launchDetails(e:Enrollment): 
  	Option[kornell.core.to.CourseDetailsTO] =  EnrollmentRepo(e).findDetails

}
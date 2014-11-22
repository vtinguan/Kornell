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
import kornell.server.content.ContentManagers
import javax.inject.Inject
import kornell.core.entity.CourseVersion
import kornell.server.ep.EnrollmentSEP
import kornell.server.scorm.scorm12.rte.SCORM12PackageManagers

@Produces(Array(Enrollment.TYPE))
class EnrollmentResource(    
    cms:ContentManagers,
    scorm12pm:SCORM12PackageManagers,
    enrollmentSEP:EnrollmentSEP,
    enrollmentRepo:EnrollmentRepo,
    contentStoreRepo:ContentStoreRepo,
    uuid: String) {
  
  lazy val enrollment = enrollmentRepo.get(uuid)

  def get = enrollment
  
  @GET
  def first = enrollmentRepo.first(uuid)

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
    enrollmentRepo.update(enrollment)
  }
    .requiring(PersonRepo(getAuthenticatedPersonUUID).hasPowerOver(enrollment.getPersonUUID), RequirementNotMet)
    .get

  @Path("actoms/{actomKey}")
  def actom(@PathParam("actomKey") actomKey: String) = new ActomResource(enrollmentSEP, uuid, actomKey)


  @GET
  @Path("approved")
  @Produces(Array("application/boolean"))
  def approved = first map { Assessment.PASSED == _.getAssessment } get

  @DELETE
  @Produces(Array(Enrollment.TYPE))
  def delete() = {    
    val enrollment = enrollmentRepo.get(uuid)
    enrollmentRepo.delete(uuid)
    enrollment
  }.requiring(isPlatformAdmin, UserNotInRole)
    .or(isInstitutionAdmin(CourseClassRepo(enrollmentRepo.get(uuid).getCourseClassUUID).get.getInstitutionUUID), UserNotInRole)
    .or(isCourseClassAdmin(enrollmentRepo.get(uuid).getCourseClassUUID), UserNotInRole)

  @GET
  @Path("launch")
  @Produces(Array(EnrollmentLaunchTO.TYPE))
  def launch: Option[EnrollmentLaunchTO] = for {
    e <- first
    cc <- CourseClassRepo(e.getCourseClassUUID).first
    cv <- CourseVersionRepo(cc.getCourseVersionUUID).first
    cs <- contentStoreRepo.first(cv.getRepositoryUUID) 
    details <- launchDetails(e) 
  } yield TOs.newEnrollmentLaunchTO(
      launchAction(e,cv,cs), 
      details,
      cv)
      
  
  def launchAction(e: Enrollment,cv:CourseVersion, cs: ContentStore): ActionTO = {    
    val cm = cms.get(cs,cv.getDistributionPrefix)
    val pm = scorm12pm.get(cm)
    pm.launch(e)
  }

  def launchDetails(e:Enrollment): 
  	Option[kornell.core.to.CourseDetailsTO] =  enrollmentRepo.findDetails(e.getUUID())

}
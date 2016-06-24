package kornell.server.api

import scala.collection.JavaConverters.asScalaBufferConverter
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.core.to.EnrollmentRequestsTO
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.repository.service.RegistrationEnrollmentService
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional
import kornell.core.to.SimplePeopleTO
import kornell.core.to.EnrollmentsTO
import kornell.server.jdbc.repository.InstitutionRepo
import kornell.server.util.EmailService
import kornell.server.jdbc.repository.EventsRepo
import kornell.core.util.UUID
import kornell.server.jdbc.repository.EnrollmentRepo
import kornell.core.to.EnrollmentLaunchTO
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.core.error.exception.ServerErrorException
import kornell.core.entity.InstitutionType
import kornell.core.to.DashboardLeaderboardTO

@Path("enrollments")
@Produces(Array(Enrollment.TYPE))
class EnrollmentsResource {

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String): EnrollmentResource = new EnrollmentResource(uuid)

  @POST
  @Consumes(Array(Enrollment.TYPE))
  @Produces(Array(Enrollment.TYPE))
  def create(enrollment: Enrollment) = {
    val existingEnrollment = EnrollmentsRepo.find(enrollment.getPersonUUID, enrollment.getCourseClassUUID)
    if (existingEnrollment.isDefined) {
      val oldState = existingEnrollment.get.getState
      existingEnrollment.get.setState(enrollment.getState)
      EnrollmentRepo(existingEnrollment.get.getUUID).update(existingEnrollment.get)
      EventsRepo.logEnrollmentStateChanged(UUID.random, getAuthenticatedPersonUUID, existingEnrollment.get.getUUID, oldState, existingEnrollment.get.getState, false)
      existingEnrollment.get
    } else {
      val e = EnrollmentsRepo.create(enrollment)
      EventsRepo.logEnrollmentStateChanged(UUID.random, getAuthenticatedPersonUUID, e.getUUID, EnrollmentState.notEnrolled, e.getState, false)
      e
    }
  }.requiring(PersonRepo(getAuthenticatedPersonUUID).hasPowerOver(enrollment.getPersonUUID), AccessDeniedErr())
    .requiring(CourseClassRepo(enrollment.getCourseClassUUID()).get.isPublicClass() == true, AccessDeniedErr())
    .requiring(enrollment.getState.equals(EnrollmentState.requested) ||
      (enrollment.getState.equals(EnrollmentState.enrolled) && CourseClassRepo(enrollment.getCourseClassUUID()).get.isApproveEnrollmentsAutomatically == true), AccessDeniedErr())
    .get

  @GET
  @Produces(Array(EnrollmentsTO.TYPE))
  def getByCourseUUID(@QueryParam("courseClassUUID") courseClassUUID: String, @QueryParam("searchTerm") searchTerm: String,
    @QueryParam("ps") pageSize: Int, @QueryParam("pn") pageNumber: Int) = {
    EnrollmentsRepo.byCourseClassPaged(courseClassUUID, searchTerm, pageSize, pageNumber)
  }.requiring(isPlatformAdmin(CourseClassRepo(courseClassUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(CourseClassRepo(courseClassUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isCourseClassAdmin(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassTutor(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassObserver(courseClassUUID), AccessDeniedErr()).get

  @PUT
  @Path("requests")
  @Consumes(Array(kornell.core.to.EnrollmentRequestsTO.TYPE))
  def putEnrollments(enrollmentRequests: EnrollmentRequestsTO) =
    AuthRepo().withPerson { p =>
      //TODO: <strike>Understand and refactor</strike> Return an error
      if (enrollmentRequests.getEnrollmentRequests.asScala exists (e => !RegistrationEnrollmentService.isInvalidRequestEnrollment(e, p.getUUID))) {
        RegistrationEnrollmentService.deanRequestEnrollments(enrollmentRequests, p)
      }
    }

  @PUT
  @Path("{courseClassUUID}/notesUpdated")
  @Produces(Array("text/plain"))
  def putNotesChange(@PathParam("courseClassUUID") courseClassUUID: String, notes: String) =
    AuthRepo().withPerson { p =>
      sql"""
    	update Enrollment set notes=$notes
    	where person_uuid=${p.getUUID}
    	and class_uuid=${courseClassUUID}
    	""".executeUpdate
    }

  @GET
  @Path("{courseClassUUID}/simpleEnrollments")
  @Produces(Array(SimplePeopleTO.TYPE))
  def getEnrollmentsList(@PathParam("courseClassUUID") courseClassUUID: String) = {
    EnrollmentsRepo.simplePersonList(courseClassUUID)
  }.requiring(isPlatformAdmin(CourseClassRepo(courseClassUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(CourseClassRepo(courseClassUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isCourseClassAdmin(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassTutor(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassObserver(courseClassUUID), AccessDeniedErr()).get
  
  @GET
  @Path("leaderboard/{dashboardEnrollmentUUID}")
  @Produces(Array(DashboardLeaderboardTO.TYPE))
  def leaderboard(@PathParam("dashboardEnrollmentUUID") dashboardEnrollmentUUID: String) = 
    EnrollmentsRepo.getLeaderboardForDashboard(dashboardEnrollmentUUID)
  
  @GET
  @Path("leaderboardPosition/{dashboardEnrollmentUUID}")
  @Produces(Array("application/octet-stream"))
  def leaderboardPosition(@PathParam("dashboardEnrollmentUUID") dashboardEnrollmentUUID: String) = 
    EnrollmentsRepo.getLeaderboardPosition(dashboardEnrollmentUUID)

}

object EnrollmentsResource {
  def apply() = new EnrollmentsResource()
}

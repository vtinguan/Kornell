package kornell.server.api

import scala.collection.JavaConverters.asScalaBufferConverter
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.Dependent
import javax.inject.Inject
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
import kornell.server.content.ContentManagers
import kornell.server.ep.EnrollmentSEP
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.ContentStoreRepo
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.jdbc.repository.EnrollmentRepo
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.repository.service.RegistrationEnrollmentService
import kornell.server.scorm.scorm12.rte.SCORM12PackageManagers
import kornell.server.util.Conditional.toConditional
import kornell.server.util.RequirementNotMet
import kornell.core.to.EnrollmentsTO
import javax.enterprise.inject.Instance
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.jdbc.repository.CourseClassesRepo

@Path("enrollments")
@Produces(Array(Enrollment.TYPE))
@ApplicationScoped
class EnrollmentsResource @Inject() (
	 //val cms:ContentManagers,
	 //val s12pms:SCORM12PackageManagers,
	 //val enrollmentSEP:EnrollmentSEP,
	 //val enrollmentRepo:EnrollmentRepo,
	 val enrollmentsRepo:EnrollmentsRepo,
	 //val contentStoreRepo:ContentStoreRepo,
	 val registrationEnrollmentService:RegistrationEnrollmentService,
	 val courseClassesRepo:CourseClassesRepo,
	 val enrollmentResource:Instance[EnrollmentResource],
	 val peopleRepo:PeopleRepo,
	 val authRepo:AuthRepo
  ) {
  
  def this() = this(null,null,null,null,null,null)

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String): EnrollmentResource = 
   enrollmentResource.get.withUUID(uuid)

  @POST
  @Consumes(Array(Enrollment.TYPE))
  @Produces(Array(Enrollment.TYPE))
  def create(enrollment: Enrollment) = {
      enrollmentsRepo.create(enrollment)
  }.requiring(peopleRepo.byUUID(getAuthenticatedPersonUUID).hasPowerOver(enrollment.getPersonUUID),  RequirementNotMet )
  .requiring(courseClassesRepo.byUUID(enrollment.getCourseClassUUID()).get.isPublicClass() == true, RequirementNotMet)
  .requiring(enrollment.getState.equals(EnrollmentState.requested), RequirementNotMet)
  .get

  @GET
  @Produces(Array(EnrollmentsTO.TYPE))
  def getByCourseUUID(@QueryParam("courseClassUUID") courseClassUUID: String) = enrollmentsRepo.byCourseClass(courseClassUUID)

  @PUT
  @Path("requests")
  @Consumes(Array(kornell.core.to.EnrollmentRequestsTO.TYPE))
  def putEnrollments(enrollmentRequests: EnrollmentRequestsTO) =
    authRepo.withPerson { p =>
      if (enrollmentRequests.getEnrollmentRequests.asScala exists (e => !registrationEnrollmentService.isInvalidRequestEnrollment(e, p.getUUID))) {
    	  registrationEnrollmentService.deanRequestEnrollments(enrollmentRequests, p)
      }
    }

  @PUT
  @Path("{courseClassUUID}/notesUpdated")
  @Produces(Array("text/plain"))
  def putNotesChange(@PathParam("courseClassUUID") courseClassUUID: String, notes: String) =
    authRepo.withPerson { p =>
      sql"""
    	update Enrollment set notes=$notes
    	where person_uuid=${p.getUUID}
    	and class_uuid=${courseClassUUID}
    	""".executeUpdate
    }

}

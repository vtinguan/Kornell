package kornell.server.api

import javax.ws.rs.Produces
import scala.collection.JavaConverters._
import javax.ws.rs.Consumes
import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.EnrollmentRepo
import kornell.server.jdbc.repository.EnrollmentsRepo
import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam
import javax.ws.rs.PUT
import javax.ws.rs.GET
import kornell.server.jdbc.repository.AuthRepo
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import kornell.server.repository.service.RegistrationEnrollmentService
import kornell.server.jdbc.SQL._
import kornell.core.entity.Enrollment
import kornell.core.entity.Enrollments
import javax.ws.rs.POST
import javax.servlet.http.HttpServletResponse
import kornell.core.to.EnrollmentRequestsTO
import kornell.server.repository.Entities
import kornell.core.to.EnrollmentsTO

@Path("enrollments")
@Produces(Array(Enrollment.TYPE))
class EnrollmentsResource {

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String): EnrollmentResource = new EnrollmentResource(uuid)

  @POST
  @Consumes(Array(Enrollment.TYPE))
  @Produces(Array(Enrollment.TYPE))
  def create(implicit @Context sc: SecurityContext, enrollment: Enrollment) =
    AuthRepo().withPerson { p =>
      EnrollmentsRepo.create(enrollment)
    }

  @GET
  @Produces(Array(EnrollmentsTO.TYPE))
  def getByCourseUUID(@QueryParam("courseClassUUID") courseClassUUID: String) = EnrollmentsRepo.byCourseClass(courseClassUUID)

  @PUT
  @Path("requests")
  @Consumes(Array(kornell.core.to.EnrollmentRequestsTO.TYPE))
  def putEnrollments(@Context resp: HttpServletResponse,
    enrollmentRequests: EnrollmentRequestsTO) =
    AuthRepo().withPerson { p =>
      //TODO: Understand and refactor
      //if (enrollmentRequests.getEnrollmentRequests.asScala exists (e => RegistrationEnrollmentService.isInvalidRequestEnrollment(e, p.getFullName))) {
      RegistrationEnrollmentService.deanRequestEnrollments(enrollmentRequests, p)
    }

  @PUT
  @Path("{courseClassUUID}/notesUpdated")
  @Produces(Array("text/plain"))
  def putNotesChange(implicit @Context sc: SecurityContext,
    @PathParam("courseClassUUID") courseClassUUID: String,
    notes: String) =
    AuthRepo().withPerson { p =>
      sql"""
    	update Enrollment set notes=$notes
    	where person_uuid=${p.getUUID}
    	and class_uuid=${courseClassUUID}
    	""".executeUpdate
    }

}

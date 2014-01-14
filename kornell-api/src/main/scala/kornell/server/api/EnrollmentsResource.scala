package kornell.server.api

import javax.ws.rs._
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.Enrollment
import kornell.server.repository.jdbc.Auth
import kornell.server.repository.jdbc.Enrollments
import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.server.repository.service.RegistrationEnrollmentService

@Path("enrollments")
@Produces(Array(Enrollment.TYPE))
class EnrollmentsResource {
  
  @Path("{uuid}")
  def get(@PathParam("uuid") uuid:String):EnrollmentResource = new EnrollmentResource(uuid) 
    
  @GET
  @Produces(Array(kornell.core.entity.Enrollments.TYPE))
  def getByCourseUUID(@QueryParam("courseClassUUID") courseClassUUID:String) = Enrollments.byCourseClass(courseClassUUID)
  
  @PUT
  @Path("requests")
  @Consumes(Array(kornell.core.to.EnrollmentRequestsTO.TYPE))
  def putEnrollments(implicit @Context sc: SecurityContext, enrollmentRequests:kornell.core.to.EnrollmentRequestsTO) =
    Auth.withPerson { p => RegistrationEnrollmentService.deanRequestEnrollments(enrollmentRequests, p) }  
  
  @PUT
  @Path("{courseClassUUID}/notesUpdated")
  @Produces(Array("text/plain"))
  def putNotesChange(implicit @Context sc: SecurityContext, 
      @PathParam("courseClassUUID") courseClassUUID: String, 
      notes: String) = 
    Auth.withPerson { p => 
    	sql"""
    	update Enrollment set notes=$notes
    	where person_uuid=${p.getUUID}
    	and class_uuid=${courseClassUUID}
    	""".executeUpdate
    }

}
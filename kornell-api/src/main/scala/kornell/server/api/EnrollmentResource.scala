package kornell.server.api

import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.repository.jdbc.Auth
import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper
import kornell.core.entity.Enrollment
import javax.ws.rs.GET
import javax.ws.rs.QueryParam
import kornell.server.repository.jdbc.Enrollments
import scala.collection.JavaConverters._
import javax.ws.rs.Consumes
import kornell.server.repository.service.RegistrationEnrollmentService

@Path("enrollment")
@Produces(Array(Enrollment.TYPE))
class EnrollmentResource{
  
  @GET
  @Produces(Array(kornell.core.entity.Enrollments.TYPE))
  def getByCourseUUID(@QueryParam("courseClassUUID") courseClassUUID:String) = Enrollments().byCourseClass(courseClassUUID)
  
  @PUT
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

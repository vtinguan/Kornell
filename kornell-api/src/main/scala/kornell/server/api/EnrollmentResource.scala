package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.repository.Beans
import kornell.server.repository.TOs
import kornell.server.repository.jdbc.Auth
import kornell.server.repository.jdbc.Registrations
import kornell.core.shared.to.RegistrationsTO
import kornell.server.repository.TOs
import javax.ws.rs.PUT
import kornell.server.repository.jdbc.SQLInterpolation._
import javax.ws.rs.PathParam

@Path("enrollment")
class EnrollmentResource extends Resource with Beans with TOs {
  
  @PUT
  @Path("{courseUUID}/notesUpdated")
  @Produces(Array("text/plain"))
  def putPlaceChange(implicit @Context sc: SecurityContext, 
      @PathParam("courseUUID") courseUUID: String, 
      notes: String) = 
    Auth.withPerson { p => 
    	sql"""
    	update Enrollment set notes=$notes
    	where person_uuid=${p.getUUID}
    	and course_uuid=${courseUUID}
    	""".executeUpdate
    }
}

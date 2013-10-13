package kornell.server.api

import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.repository.jdbc.Auth
import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper

@Path("enrollment")
class EnrollmentResource{
  
  @PUT
  @Path("{courseUUID}/notesUpdated")
  @Produces(Array("text/plain"))
  def putNotesChange(implicit @Context sc: SecurityContext, 
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

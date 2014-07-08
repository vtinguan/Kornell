package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.core.to.CourseVersionsTO

@Path("courseVersions")
class CourseVersionsResource {
  /*
  @Path("{uuid}")
  def getCourseVersion(@PathParam("uuid") uuid:String) = CourseVersionResource(uuid)*/
  
  @GET
  @Produces(Array(CourseVersionsTO.TYPE))
  def getCourseVersions(implicit @Context sc: SecurityContext, @QueryParam("courseUUID") courseUUID:String) =
	  AuthRepo().withPerson { person => {
	     if(courseUUID != null){
	    	 CourseVersionsRepo.byCourse(courseUUID)
	     } 
	  }
  }
  
}

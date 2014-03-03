package kornell.server.api
import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.SecurityContext
import javax.ws.rs.core.Context
import kornell.core.lom.Contents
import kornell.core.to.CoursesTO
import kornell.core.to.CourseClassesTO
import javax.ws.rs.QueryParam
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.CourseClassesRepo


@Path("courseClasses")
class CourseClassesResource {
  
  @Path("{uuid}")
  def getCourseClassResource(@PathParam("uuid") uuid:String) = CourseClassResource(uuid)
  
  
  @GET
  @Produces(Array(CourseClassesTO.TYPE))
  def getClasses(implicit @Context sc: SecurityContext, @QueryParam("institutionUUID") institutionUUID:String) =
	  AuthRepo.withPerson { person => {
	     if(institutionUUID != null){
	    	 CourseClassesRepo.byPersonAndInstitution(person.getUUID, institutionUUID)
	     }
	  }
  }
  
  @GET
  @Produces(Array(CourseClassesTO.TYPE))
  @Path("administrated")
  def getAdministratedClasses(implicit @Context sc: SecurityContext, @QueryParam("institutionUUID") institutionUUID:String) =
	  AuthRepo.withPerson { person => {
	     if(institutionUUID != null){
	    	 val roles = AuthRepo.rolesOf(sc.getUserPrincipal().getName())
	    	 CourseClassesRepo.administratedByPersonOnInstitution(person, institutionUUID, roles)
	     }
	  }
  }
}

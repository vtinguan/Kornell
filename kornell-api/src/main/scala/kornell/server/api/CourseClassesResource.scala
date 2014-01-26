package kornell.server.api
import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.SecurityContext
import kornell.server.repository.jdbc.Auth
import javax.ws.rs.core.Context
import kornell.core.lom.Contents
import kornell.core.to.CoursesTO
import kornell.core.to.CourseClassesTO
import kornell.server.repository.jdbc.CourseClasses
import javax.ws.rs.QueryParam

@Path("courseClasses")
class CourseClassesResource {
  
  @Path("{uuid}")
  def getCourseClassResource(@PathParam("uuid") uuid:String) = CourseClassResource(uuid)
  
  /*@GET
  @Produces(Array(CourseClassesTO.TYPE))
  def getClasses(implicit @Context sc: SecurityContext) = 
  Auth.withPerson { person =>
	 CourseClasses.byPerson(person.getUUID)
  }*/
  
  @GET
  @Produces(Array(CourseClassesTO.TYPE))
  def getClasses(implicit @Context sc: SecurityContext, @QueryParam("institutionUUID") institutionUUID:String) = 
	  Auth.withPerson { person => {
	    if(institutionUUID == null){
	    	CourseClasses.byPerson(person.getUUID)
	    } else {
	    	CourseClasses.byPersonAndInstitution(person.getUUID, institutionUUID)
	    }
	  }
    
  }
}
package kornell.server.api

import scala.collection.JavaConverters._
import javax.ws.rs._
import javax.ws.rs.core._
import kornell.core.lom._
import kornell.core.to._
import kornell.server.jdbc.SQL._
import kornell.server.dev.util.ContentsParser
import kornell.server.repository.s3.S3
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.CoursesRepo

@Path("courses")
class CoursesResource {
  
  @Path("{uuid}")
  def getCourse(@PathParam("uuid") uuid:String) = CourseResource(uuid)
  
  @GET
  @Produces(Array(CoursesTO.TYPE))
  def getCourses(implicit @Context sc: SecurityContext, @QueryParam("institutionUUID") institutionUUID:String) =
	  AuthRepo.withPerson { person => {
	     if(institutionUUID != null){
	    	 CoursesRepo.byInstitution(institutionUUID)
	     } 
	  }
  }
  
}

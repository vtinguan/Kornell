package kornell.api

import javax.ws.rs.Produces
import javax.ws.rs.Path
import scala.collection.JavaConverters._
import javax.ws.rs.GET

@Produces(Array("application/vnd.kornell.v1.course+json"))
@Path("courses")
class CoursesResource {
  
	@GET
	def getCourses = 
	  List("https://s3.amazonaws.com/kornell/scorm/SCORM2004.4.SECE.1.0.CP/") asJava
	 

}
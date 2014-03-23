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
import kornell.core.entity.CourseClass
import kornell.server.jdbc.repository.CourseClassRepo
import javax.ws.rs.PUT
import javax.ws.rs.Consumes
import kornell.core.entity.RoleCategory
import scala.collection.JavaConverters._
import javax.servlet.http.HttpServletResponse
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException


@Path("courseClasses")
class CourseClassesResource {
  
  @Path("{uuid}")
  def getCourseClassResource(@PathParam("uuid") uuid:String) = CourseClassResource(uuid)
    
  @PUT
  @Consumes(Array(CourseClass.TYPE))
  @Produces(Array(CourseClass.TYPE))
  def create(implicit @Context sc: SecurityContext,
      @Context resp: HttpServletResponse, 
      courseClass: CourseClass) = AuthRepo.withPerson{ p => {
        val roles = (Set.empty ++ AuthRepo.rolesOf(sc.getUserPrincipal.getName)).asJava
		    if(!(RoleCategory.isPlatformAdmin(roles) ||
				        RoleCategory.isInstitutionAdmin(roles, courseClass.getInstitutionUUID)))
		    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized attempt to create a class without platformAdmin or institutionAdmin rights.");
		    else
					try { 
						CourseClassesRepo.create(courseClass)
					} catch {
						case ioe: MySQLIntegrityConstraintViolationException => 
						  resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Constraint Violated (uuid or name).");
					}
    }
  }


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

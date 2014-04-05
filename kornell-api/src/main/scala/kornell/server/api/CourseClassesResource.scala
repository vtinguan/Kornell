package kornell.server.api

import scala.collection.JavaConverters.setAsJavaSetConverter
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.CourseClass
import kornell.core.entity.RoleCategory
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.server.util.Conditional.toConditional
import kornell.core.to.CourseClassesTO
import kornell.server.util.Errors
import kornell.server.util.Errors

@Path("courseClasses")
class CourseClassesResource {

  //TODO: Cache 

  @Path("{uuid}")
  def getCourseClassResource(@PathParam("uuid") uuid: String)(implicit sc:SecurityContext) = 
    CourseClassResource(uuid) 
  
 

  //TODO: Refactor: auth,excep
  //TOOD: Error cases
  //TODO: Test
  //resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized attempt to create a class without platformAdmin or institutionAdmin rights.");
  //case ioe: MySQLIntegrityConstraintViolationException => resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Constraint Violated (uuid or name).");
  @PUT
  @Consumes(Array(CourseClass.TYPE))
  @Produces(Array(CourseClass.TYPE))
  def create(implicit @Context sc: SecurityContext, courseClass: CourseClass) = {
    CourseClassesRepo.create(courseClass)
  }.requiring(isPlatformAdmin, Errors.UserNotInRole )
   .or(isInstitutionAdmin(courseClass.getInstitutionUUID) , Errors.UserNotInRole)

  /*
    Require.PlatformAdmin
      .or(Require.InstitutionAdmin(courseClass.getInstitutionUUID))
      .map 
      .get
*/

  @GET
  @Produces(Array(CourseClassesTO.TYPE))
  def getClasses(implicit @Context sc: SecurityContext, @QueryParam("institutionUUID") institutionUUID: String) =
    AuthRepo.withPerson { person =>
      {
        if (institutionUUID != null) {
          CourseClassesRepo.byPersonAndInstitution(person.getUUID, institutionUUID)
        }
      }
    }

  @GET
  @Produces(Array(CourseClassesTO.TYPE))
  @Path("administrated")
  def getAdministratedClasses(implicit @Context sc: SecurityContext, @QueryParam("institutionUUID") institutionUUID: String) =
    AuthRepo.withPerson { person =>
      {
        if (institutionUUID != null) {
          val roles = AuthRepo.rolesOf(sc.getUserPrincipal().getName())
          CourseClassesRepo.administratedByPersonOnInstitution(person, institutionUUID, roles.toList)
        }
      }
    }
}

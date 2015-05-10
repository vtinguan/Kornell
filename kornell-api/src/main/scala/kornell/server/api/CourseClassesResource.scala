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
import kornell.server.repository.Entities
import javax.ws.rs.POST
import kornell.core.entity.RegistrationType
import kornell.server.util.AccessDeniedErr

@Path("courseClasses")
class CourseClassesResource {
  
  @POST
  @Consumes(Array(CourseClass.TYPE))
  @Produces(Array(CourseClass.TYPE))
  def create(courseClass: CourseClass) = {
    CourseClassesRepo.create(courseClass)
  }.requiring(isPlatformAdmin, AccessDeniedErr()) 
   .or(isInstitutionAdmin(courseClass.getInstitutionUUID), AccessDeniedErr())
   .get
   
  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String):CourseClassResource = CourseClassResource(uuid)

  @GET
  @Produces(Array(CourseClassesTO.TYPE))
  def getClasses(implicit @Context sc: SecurityContext) =
    AuthRepo().withPerson { person =>
      {
          CourseClassesRepo.byPersonAndInstitution(person.getUUID, person.getInstitutionUUID)
      }
    }

  @GET
  @Produces(Array(CourseClassesTO.TYPE))
  @Path("administrated")
  def getAdministratedClasses(implicit @Context sc: SecurityContext, @QueryParam("courseVersionUUID") courseVersionUUID: String, @QueryParam("searchTerm") searchTerm: String,
      @QueryParam("ps") pageSize: Int, @QueryParam("pn") pageNumber: Int) =
    AuthRepo().withPerson { person =>
      {
          CourseClassesRepo.getAllClassesByInstitutionPaged(person.getInstitutionUUID, searchTerm, pageSize, pageNumber, person.getUUID, courseVersionUUID)
      }
    }
}

object CourseClassesResource{
  def apply() = new CourseClassesResource()
}
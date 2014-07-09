package kornell.server.api

import javax.ws.rs.Produces
import scala.collection.JavaConverters._
import javax.ws.rs.core.SecurityContext
import javax.ws.rs.GET
import kornell.server.jdbc.repository.AuthRepo
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import kornell.core.lom.Contents
import kornell.core.to.CourseClassTO
import kornell.server.dev.util.ContentsParser
import kornell.server.repository.s3.S3
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.CourseClassesRepo
import javax.servlet.http.HttpServletRequest
import kornell.core.entity.CourseClass
import javax.ws.rs.PUT
import javax.ws.rs.Consumes
import kornell.server.jdbc.repository.CourseClassRepo
import javax.ws.rs.QueryParam
import kornell.core.entity.Role
import kornell.core.entity.Roles
import javax.ws.rs.DELETE
import kornell.core.entity.RoleCategory
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
import javax.servlet.http.HttpServletResponse
import kornell.server.repository.ContentRepository
import kornell.core.to.RolesTO
import kornell.core.to.LibraryFilesTO
import kornell.server.repository.LibraryFilesRepository
import java.io.IOException

@Path("courseClass")
class CourseClassResource(uuid: String) {

  @GET
  @Path("to")
  @Produces(Array(CourseClassTO.TYPE))
  def get(implicit @Context sc: SecurityContext) =
    AuthRepo().withPerson { person =>
      //CourseClasses(uuid).byPerson(person.getUUID)
    }

  @PUT
  @Consumes(Array(CourseClass.TYPE))
  @Produces(Array(CourseClass.TYPE))
  def update(@Context resp: HttpServletResponse, courseClass: CourseClass) = AuthRepo().withPerson { p =>
    val roles = AuthRepo().getUserRoles
    if (!(RoleCategory.isPlatformAdmin(roles) ||
      RoleCategory.isInstitutionAdmin(roles, courseClass.getInstitutionUUID)))
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized attempt to update a class without platformAdmin or institutionAdmin rights.");
    else
      try {
        CourseClassRepo(uuid).update(courseClass)
      } catch {
        case ioe: MySQLIntegrityConstraintViolationException =>
          resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Constraint Violated (uuid or name).");
      }
  }

  @DELETE
  @Produces(Array(CourseClass.TYPE))
  def delete(@Context resp: HttpServletResponse) = AuthRepo().withPerson { p =>
    val courseClass = CourseClassRepo(uuid).get
    val roles = AuthRepo().getUserRoles
    if (courseClass == null)
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Can't delete a class that doesn't exist.");
    else if (!(RoleCategory.isPlatformAdmin(roles) ||
      RoleCategory.isInstitutionAdmin(roles, CourseClassRepo(uuid).get.getInstitutionUUID)))
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized attempt to update a class without platformAdmin or institutionAdmin rights.");
    else
      try {
        CourseClassRepo(uuid).delete(uuid)
        courseClass
      } catch {
        case ioe: MySQLIntegrityConstraintViolationException =>
          resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Constraint Violated (uuid or name).");
      }
  }

  @Produces(Array(Contents.TYPE))
  @Path("contents")
  @GET
  def getLatestContents(implicit @Context sc: SecurityContext): Contents =
    //TODO: Refactor to Option.map
    AuthRepo().withPerson { person =>            
      ContentRepository.findKNLVisitedContent(uuid,person.getUUID)
    }

  @Produces(Array(LibraryFilesTO.TYPE))
  @Path("libraryFiles")
  @GET
  def getLibraryFiles =  LibraryFilesRepository.findLibraryFiles(uuid)
     

  @PUT
  @Consumes(Array(Roles.TYPE))
  @Produces(Array(Roles.TYPE))
  @Path("admins")
  def updateAdmins(implicit @Context sc: SecurityContext, roles: Roles) =
    AuthRepo().withPerson { person =>
      {
        CourseClassRepo(uuid).updateAdmins(roles)
      }
    }

  @GET
  @Produces(Array(RolesTO.TYPE))
  @Path("admins")
  def getAdmins(implicit @Context sc: SecurityContext,
      @QueryParam("bind") bindMode:String) =
    AuthRepo().withPerson { person =>
      {
        CourseClassRepo(uuid).getAdmins(bindMode)
      }
    }

}

object CourseClassResource {
  def apply(uuid: String) = new CourseClassResource(uuid)
}
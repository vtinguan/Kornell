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
import javax.servlet.http.HttpServletResponse
import kornell.server.repository.ContentRepository
import kornell.core.to.RolesTO
import kornell.core.to.LibraryFilesTO
import kornell.server.repository.LibraryFilesRepository
import java.io.IOException
import kornell.server.jdbc.repository.RolesRepo
import kornell.server.jdbc.repository.ChatThreadsRepo
import java.sql.SQLException
import kornell.server.repository.LibraryFilesRepository
import javax.inject.Inject
import javax.enterprise.context.Dependent
import kornell.server.util.Identifiable
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.jdbc.repository.RolesRepo
import kornell.server.jdbc.repository.ChatThreadsRepo

@Dependent
class CourseClassResource @Inject() (    
    val libRepo:LibraryFilesRepository,
    val courseClassesRepo:CourseClassesRepo,
    val chatThreadsRepo:ChatThreadsRepo,
    val authRepo:AuthRepo,
    val rolesRepo:RolesRepo) 
	extends Identifiable{  
  
  def this() = this(null,null,null,null,null)
  
  @GET
  @Produces(Array(CourseClass.TYPE))
  def get = courseClassesRepo.byUUID(uuid).first
  
  @GET
  @Path("to")
  @Produces(Array(CourseClassTO.TYPE))
  def get(implicit @Context sc: SecurityContext) =
    authRepo.withPerson { person =>
      //CourseClasses(uuid).byPerson(person.getUUID)
    }

  //TODO: exception handling
  @PUT
  @Consumes(Array(CourseClass.TYPE))
  @Produces(Array(CourseClass.TYPE))
  def update(@Context resp: HttpServletResponse, courseClass: CourseClass) = authRepo.withPerson { p =>
    val roles = authRepo.getUserRoles
    if (!(RoleCategory.isPlatformAdmin(roles) ||
      RoleCategory.isInstitutionAdmin(roles, courseClass.getInstitutionUUID)))
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized attempt to update a class without platformAdmin or institutionAdmin rights.");
    else
      try {
        courseClassesRepo.byUUID(uuid).update(courseClass)
      } catch {
        case ioe: SQLException =>
          resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Constraint Violated (uuid or name).");
      }
  }

  @DELETE
  @Produces(Array(CourseClass.TYPE))
  def delete(@Context resp: HttpServletResponse) = authRepo.withPerson { p =>
    val courseClass = courseClassesRepo.byUUID(uuid).get
    val roles = authRepo.getUserRoles
    if (courseClass == null)
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Can't delete a class that doesn't exist.");
    else if (!(RoleCategory.isPlatformAdmin(roles) ||
      RoleCategory.isInstitutionAdmin(roles, courseClassesRepo.byUUID(uuid).get.getInstitutionUUID)))
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized attempt to update a class without platformAdmin or institutionAdmin rights.");
    else
      try {
       courseClassesRepo.byUUID(uuid).delete(uuid)
        courseClass
      } catch {
        case ioe: SQLException =>
          resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Constraint Violated (uuid or name).");
      }
  }

  /*
  @Produces(Array(Contents.TYPE))
  @Path("contents")
  @GET
  def getLatestContents(): Contents =
    AuthRepo().withPerson { person =>            
      val cr = ContentRepository
      cr.findKNLVisitedContent(uuid,person.getUUID)
    }
  */

  @Produces(Array(LibraryFilesTO.TYPE))
  @Path("libraryFiles")
  @GET
  def getLibraryFiles = libRepo.findLibraryFiles(uuid)
  
     

  @PUT
  @Consumes(Array(Roles.TYPE))
  @Produces(Array(Roles.TYPE))
  @Path("admins")
  def updateAdmins(implicit @Context sc: SecurityContext, roles: Roles) =
    authRepo.withPerson { person =>
      {
        val r = rolesRepo.updateCourseClassAdmins(uuid, roles)
        chatThreadsRepo.updateParticipantsInCourseClassSupportThreads(uuid)
        r
      }
    }

  @GET
  @Produces(Array(RolesTO.TYPE))
  @Path("admins")
  def getAdmins(implicit @Context sc: SecurityContext,
      @QueryParam("bind") bindMode:String) =
    authRepo.withPerson { person =>
      {
        rolesRepo.getCourseClassAdmins(uuid, bindMode)
      }
    }

}

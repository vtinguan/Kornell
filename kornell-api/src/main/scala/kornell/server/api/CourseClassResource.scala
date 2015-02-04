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
import kornell.server.util.Conditional.toConditional
import kornell.server.util.AccessDeniedErr
import kornell.server.jdbc.repository.PersonRepo
import kornell.core.entity.ChatThreadType
import java.sql.SQLException
import kornell.server.repository.LibraryFilesRepository
import javax.inject.Inject
import javax.enterprise.context.Dependent
import kornell.server.util.Identifiable
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.jdbc.repository.RolesRepo
import kornell.server.jdbc.repository.ChatThreadsRepo
import kornell.server.auth.Authorizator

@Dependent
class CourseClassResource @Inject() (
    val auth:Authorizator,
    val libRepo:LibraryFilesRepository,
    val courseClassesRepo:CourseClassesRepo,
    val chatThreadsRepo:ChatThreadsRepo,
    val authRepo:AuthRepo,
    val rolesRepo:RolesRepo,
    val personRepo: PersonRepo) 
	extends Identifiable{  
  
  def this() = this(null, null, null,null,null,null,null)
  
  @GET
  @Produces(Array(CourseClass.TYPE))
  def get = courseClassesRepo.byUUID(uuid).first
  
  @GET
  @Path("to")
  @Produces(Array(CourseClassTO.TYPE))
  def getTO = 
    authRepo.withPerson { person =>
		val courseClassesTO = courseClassesRepo.getAllClassesByInstitutionAndVersion(personRepo.withUUID(auth.getAuthenticatedPersonUUID).get.getInstitutionUUID, null, uuid)
		if(courseClassesTO.getCourseClasses.size > 0){
		  courseClassesTO.getCourseClasses.get(0)
		}
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
        chatThreadsRepo.updateParticipantsInSupportThreads(uuid, ChatThreadType.SUPPORT)
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
  
  @PUT
  @Consumes(Array(Roles.TYPE))
  @Produces(Array(Roles.TYPE))
  @Path("tutors")
  def updateTutors(roles: Roles) = authRepo.withPerson { person => {
        val r = rolesRepo.updateTutors(uuid, roles)
        chatThreadsRepo.updateParticipantsInSupportThreads(uuid, ChatThreadType.TUTORING)
        r
  }
  }.requiring(auth.isPlatformAdmin, AccessDeniedErr())
   .or(auth.isInstitutionAdmin(personRepo.withUUID(auth.getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get

  @GET
  @Produces(Array(RolesTO.TYPE))
  @Path("tutors")
  def updateTutors(@QueryParam("bind") bindMode:String) = {
        rolesRepo.getTutorsForCourseClass(uuid, bindMode)
  }.requiring(auth.isPlatformAdmin, AccessDeniedErr())
   .or(auth.isInstitutionAdmin(personRepo.withUUID(auth.getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get

}

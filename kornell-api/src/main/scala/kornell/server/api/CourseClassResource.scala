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
import kornell.core.error.exception.UnauthorizedAccessException
import kornell.core.error.exception.EntityNotFoundException
import kornell.core.error.exception.EntityConflictException


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
  def update(courseClass: CourseClass) = AuthRepo().withPerson { p =>
    val roles = AuthRepo().getUserRoles
    if (!(RoleCategory.isPlatformAdmin(roles) ||
      RoleCategory.isInstitutionAdmin(roles, courseClass.getInstitutionUUID)))
      throw new UnauthorizedAccessException("classNoRights")
    else
      try {
        CourseClassRepo(uuid).update(courseClass)
      } catch {
        case ioe: MySQLIntegrityConstraintViolationException =>
          throw new EntityConflictException("constraintViolatedUUIDName")
      }
  }

  @DELETE
  @Produces(Array(CourseClass.TYPE))
  def delete() = AuthRepo().withPerson { p =>
    val courseClass = CourseClassRepo(uuid).get
    if (courseClass == null)
      throw new EntityNotFoundException("classNotFound")
    
    val roles = AuthRepo().getUserRoles
    if (!(RoleCategory.isPlatformAdmin(roles) ||
      RoleCategory.isInstitutionAdmin(roles, CourseClassRepo(uuid).get.getInstitutionUUID)))
      throw new UnauthorizedAccessException("classNoRights")
    else
      try {
        CourseClassRepo(uuid).delete(uuid)
        courseClass
      } catch {
        case ioe: MySQLIntegrityConstraintViolationException =>
          throw new EntityConflictException("constraintViolatedUUIDName")
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
        val r = RolesRepo.updateCourseClassAdmins(uuid, roles)
        ChatThreadsRepo.updateParticipantsInSupportThreads(uuid, ChatThreadType.SUPPORT)
        r
      }
    }

  @GET
  @Produces(Array(RolesTO.TYPE))
  @Path("admins")
  def getAdmins(implicit @Context sc: SecurityContext,
      @QueryParam("bind") bindMode:String) =
    AuthRepo().withPerson { person =>
      {
        RolesRepo.getCourseClassAdmins(uuid, bindMode)
      }
    }
  
  @PUT
  @Consumes(Array(Roles.TYPE))
  @Produces(Array(Roles.TYPE))
  @Path("tutors")
  def updateTutors(roles: Roles) = {
        val r = RolesRepo.updateTutors(uuid, roles)
        ChatThreadsRepo.updateParticipantsInSupportThreads(uuid, ChatThreadType.TUTORING)
        r
  }.requiring(isPlatformAdmin, AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get

  @GET
  @Produces(Array(RolesTO.TYPE))
  @Path("tutors")
  def updateTutors(@QueryParam("bind") bindMode:String) = {
        RolesRepo.getTutorsForCourseClass(uuid, bindMode)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get

}

object CourseClassResource {
  def apply(uuid: String) = new CourseClassResource(uuid)
}
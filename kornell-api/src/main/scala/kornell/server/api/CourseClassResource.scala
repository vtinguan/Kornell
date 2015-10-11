package kornell.server.api

import scala.collection.JavaConverters._
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.ChatThreadType
import kornell.core.entity.CourseClass
import kornell.core.entity.RoleCategory
import kornell.core.entity.RoleType
import kornell.core.entity.Roles
import kornell.core.error.exception.EntityConflictException
import kornell.core.error.exception.EntityNotFoundException
import kornell.core.error.exception.UnauthorizedAccessException
import kornell.core.to.LibraryFilesTO
import kornell.core.to.RolesTO
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.ChatThreadsRepo
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.jdbc.repository.RolesRepo
import kornell.server.repository.LibraryFilesRepository
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional
import kornell.core.to.CourseClassTO
import kornell.server.jdbc.repository.CourseClassesRepo


class CourseClassResource(uuid: String) {

  @GET
  @Path("to")
  @Produces(Array(CourseClassTO.TYPE))
  def getTO(implicit @Context sc: SecurityContext) =
    AuthRepo().withPerson { person =>
       	CourseClassesRepo.getCourseClassTO(person.getInstitutionUUID, uuid)
    }


  @PUT
  @Consumes(Array(CourseClass.TYPE))
  @Produces(Array(CourseClass.TYPE))
  def update(courseClass: CourseClass) = AuthRepo().withPerson { p =>
    val roles = RolesRepo.getUserRoles(p.getUUID, RoleCategory.BIND_DEFAULT).getRoleTOs
    if (!(RoleCategory.isPlatformAdmin(roles, courseClass.getInstitutionUUID) ||
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
    
    val roles = RolesRepo.getUserRoles(p.getUUID, RoleCategory.BIND_DEFAULT).getRoleTOs
    val institutionUUID = CourseClassRepo(uuid).get.getInstitutionUUID
    if (!(RoleCategory.isPlatformAdmin(roles, institutionUUID) ||
      RoleCategory.isInstitutionAdmin(roles, institutionUUID)))
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
        ChatThreadsRepo.updateParticipantsInThreads(uuid, person.getInstitutionUUID, ChatThreadType.SUPPORT)
        ChatThreadsRepo.updateParticipantsInThreads(uuid, person.getInstitutionUUID, ChatThreadType.INSTITUTION_SUPPORT)
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
        RolesRepo.getUsersWithRoleForCourseClass(uuid, bindMode, RoleType.courseClassAdmin)
      }
    }
  
  @PUT
  @Consumes(Array(Roles.TYPE))
  @Produces(Array(Roles.TYPE))
  @Path("tutors")
  def updateTutors(roles: Roles) = 
  AuthRepo().withPerson { person =>
    {
        val r = RolesRepo.updateTutors(uuid, roles)
        ChatThreadsRepo.updateParticipantsInThreads(uuid, person.getInstitutionUUID, ChatThreadType.TUTORING)
        r
    }
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get

  @GET
  @Produces(Array(RolesTO.TYPE))
  @Path("tutors")
  def getTutors(@QueryParam("bind") bindMode:String) = {
        RolesRepo.getUsersWithRoleForCourseClass(uuid, bindMode, RoleType.tutor)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get

   @PUT
  @Consumes(Array(Roles.TYPE))
  @Produces(Array(Roles.TYPE))
  @Path("observers")
  def updateObservers(roles: Roles) = {
        RolesRepo.updateObservers(uuid, roles)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get

  @GET
  @Produces(Array(RolesTO.TYPE))
  @Path("observers")
  def getObservers(@QueryParam("bind") bindMode:String) = {
        RolesRepo.getUsersWithRoleForCourseClass(uuid, bindMode, RoleType.observer)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get
}

object CourseClassResource {
  def apply(uuid: String) = new CourseClassResource(uuid)
}
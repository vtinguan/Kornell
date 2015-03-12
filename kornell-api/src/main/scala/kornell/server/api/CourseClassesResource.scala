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
import kornell.server.util.Errors._
import kornell.server.repository.Entities
import javax.ws.rs.POST
import kornell.server.repository.LibraryFilesRepository
import javax.inject.Inject
import kornell.server.jdbc.repository.CourseClassRepo
import javax.enterprise.inject.Instance
import kornell.server.jdbc.repository.AuthRepo
import kornell.core.entity.RegistrationEnrollmentType
import kornell.core.entity.CourseClassState
import java.util.Date
import java.math.BigDecimal
import kornell.server.auth.Authorizator
import kornell.server.util.AccessDeniedErr
import kornell.server.jdbc.repository.PersonRepo

@Path("courseClasses")
class CourseClassesResource @Inject() (
  val auth:Authorizator,
  val authRepo: AuthRepo,
  val personRepo: PersonRepo,
  val courseClassesRepo: CourseClassesRepo,
  val courseClassResourceBean: Instance[CourseClassResource]) {

  def this() = this(null, null, null, null,null)

  def create(uuid: String = null,
    name: String = null,
    courseVersionUUID: String = null,
    institutionUUID: String = null,
    requiredScore: BigDecimal = null,
    publicClass: Boolean = false,
    overrideEnrollments: Boolean = false,
    invisible: Boolean = false,
    maxEnrollments: Integer = null,
    createdAt: Date = null,
    createdBy: String = null,
    state: CourseClassState = null,
    registrationEnrollmentType: RegistrationEnrollmentType = null,
    institutionRegistrationPrefix: String = null):CourseClass =
    create(Entities.newCourseClass(uuid, name,
      courseVersionUUID, institutionUUID,
      requiredScore, publicClass,
      overrideEnrollments,
      invisible, maxEnrollments,
      createdAt, createdBy,
      state,
      registrationEnrollmentType,
      institutionRegistrationPrefix))

  @POST
  @Consumes(Array(CourseClass.TYPE))
  @Produces(Array(CourseClass.TYPE))
  def create(courseClass: CourseClass) = {
    courseClassesRepo.create(courseClass)
  }.requiring(auth.isPlatformAdmin, AccessDeniedErr())
    .or(auth.isInstitutionAdmin(courseClass.getInstitutionUUID), AccessDeniedErr())
    .get

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String): CourseClassResource = courseClassResourceBean.get.withUUID(uuid)

  @GET
  @Produces(Array(CourseClassesTO.TYPE))
  def getClasses(implicit @Context sc: SecurityContext, @QueryParam("institutionUUID") institutionUUID: String) =
    authRepo.withPerson { person =>
      {
        if (institutionUUID != null) {
          courseClassesRepo.byPersonAndInstitution(person.getUUID, institutionUUID)
        }
      }
    }

  @GET
  @Produces(Array(CourseClassesTO.TYPE))
  @Path("administrated")
  def getAdministratedClasses(@QueryParam("courseVersionUUID") courseVersionUUID: String) = {
    	  val person = personRepo.withUUID(auth.getAuthenticatedPersonUUID).get
    	  val roles = authRepo.userRoles
          courseClassesRepo.administratedByPersonOnInstitution(person, person.getInstitutionUUID, courseVersionUUID, roles.toList)
      }
}


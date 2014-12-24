package kornell.server.api

import scala.collection.JavaConverters._
import javax.ws.rs._
import javax.ws.rs.core._
import kornell.core.lom._
import kornell.core.to._
import kornell.server.jdbc.SQL._
import kornell.server.dev.util.ContentsParser
import kornell.server.repository.s3.S3
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.CoursesRepo
import kornell.server.jdbc.repository.CourseRepo
import kornell.core.entity.Course
import kornell.server.util.Conditional.toConditional
import kornell.server.util.RequirementNotMet
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.util.AccessDeniedErr
import javax.inject.Inject
import javax.enterprise.inject.Instance
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.auth.Authorizator

@Path("courses")
class CoursesResource @Inject() (
  val auth:Authorizator,
  val authRepo: AuthRepo,
  val courseResourceBean: Instance[CourseResource],
  val peopleRepo: PeopleRepo,
  val coursesRepo:CoursesRepo) {

  def this() = this(null,null,null,null,null)

  @Path("{uuid}")
  def getCourse(@PathParam("uuid") uuid: String) = courseResourceBean.get().withUUID(uuid)

  @GET
  @Produces(Array(CoursesTO.TYPE))
  def getCourses =
    authRepo.withPerson { person =>
      {
        coursesRepo.byInstitution(person.getInstitutionUUID)
      }
    }

  @POST
  @Produces(Array(Course.TYPE))
  @Consumes(Array(Course.TYPE))
  def create(course: Course) = {
    coursesRepo.create(course)
  }.requiring(auth.isPlatformAdmin, RequirementNotMet)
    .or(auth.isInstitutionAdmin(peopleRepo.byUUID(auth.getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get
}

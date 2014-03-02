package kornell.server.api

import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.EnrollmentsRepo
import javax.ws.rs.PathParam
import javax.ws.rs.PUT
import kornell.core.entity.Enrollment
import javax.ws.rs.GET
import kornell.server.jdbc.repository.AuthRepo
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import kornell.core.lom.Contents
import kornell.core.entity.Registrations
import kornell.core.entity.Enrollments
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.RegistrationsRepo
import javax.servlet.http.HttpServletRequest
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.jdbc.repository.EnrollmentRepo
import scala.math.BigDecimal

@Produces(Array(Enrollment.TYPE))
class EnrollmentResource(uuid: String) {
  lazy val enrollment = get
  lazy val enrollmentRepo = EnrollmentRepo(uuid)

  def get = enrollmentRepo.get

  @GET
  def first = enrollmentRepo.first

  @PUT
  @Produces(Array("text/plain"))
  @Path("acceptTerms")
  def acceptTerms(implicit @Context sc: SecurityContext) = AuthRepo.withPerson { p =>
    RegistrationsRepo(p.getUUID, uuid).acceptTerms
  }

  @PUT
  @Produces(Array("text/plain"))
  @Consumes(Array(Enrollment.TYPE))
  def update(implicit @Context sc: SecurityContext, enrollment: Enrollment) = AuthRepo.withPerson { p =>
    //TODO: Security: restrict to own enrollments
    EnrollmentsRepo().update(enrollment)
  }

  @Path("actoms/{actomKey}")
  def actom(@PathParam("actomKey") actomKey: String) = ActomResource(uuid, actomKey)

  @GET
  @Path("contents")
  @Produces(Array(Contents.TYPE))
  def contents(implicit @Context sc: SecurityContext): Option[Contents] = first map { e =>
    CourseClassResource(e.getCourseClassUUID()).getLatestContents(sc)
  }

  @GET
  @Path("approved")
  @Produces(Array("application/boolean"))
  def approved = {
    val courseClass = CourseClassRepo(enrollment.getCourseClassUUID()).get
    val reqScore = courseClass.getRequiredScore
    reqScore == null || {
      val grades = enrollmentRepo.findGrades
      val approved = grades forall { BigDecimal(_) > reqScore }
      approved
    }
  }

}
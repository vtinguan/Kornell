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

@Produces(Array(Enrollment.TYPE))
class EnrollmentResource(uuid: String) {

  @GET
  def get = EnrollmentsRepo.byUUID(uuid)

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
    EnrollmentsRepo.update(enrollment)
  }

  @Path("actoms/{actomKey}")
  def actom(@PathParam("actomKey") actomKey: String) = ActomResource(uuid, actomKey)

  @GET
  @Path("contents")
  @Produces(Array(Contents.TYPE))
  def contents(implicit @Context sc: SecurityContext):Option[Contents] = get.map { e =>
   CourseClassResource(e.getCourseClassUUID()).getLatestContents(sc)
  }
}
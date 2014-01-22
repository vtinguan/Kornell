package kornell.server.api

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.Enrollment
import kornell.server.repository.jdbc.Auth
import kornell.server.repository.jdbc.Enrollments
import kornell.server.repository.jdbc.Registrations
import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper
import javax.ws.rs.PathParam

@Produces(Array(Enrollment.TYPE))
class EnrollmentResource(uuid: String) {

  @GET
  def get = Enrollments.byUUID(uuid)

  @PUT
  @Produces(Array("text/plain"))
  @Path("acceptTerms")
  def acceptTerms(implicit @Context sc: SecurityContext) = Auth.withPerson { p =>
    Registrations(p.getUUID, uuid).acceptTerms
  }

  @PUT
  @Produces(Array("text/plain"))
  @Consumes(Array(Enrollment.TYPE))
  def update(implicit @Context sc: SecurityContext, enrollment: Enrollment) = Auth.withPerson { p =>
    Enrollments.update(enrollment)
  }

  @Path("actoms/{actomKey}")
  def actom(@PathParam("actomKey") actomKey: String) = ActomResource(uuid,actomKey)

}
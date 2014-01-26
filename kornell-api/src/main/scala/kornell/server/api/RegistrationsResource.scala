package kornell.server.api

import javax.ws.rs.Produces
import javax.ws.rs.core.SecurityContext
import javax.ws.rs.GET
import kornell.server.jdbc.repository.AuthRepo
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import kornell.core.entity.Registrations
import kornell.server.jdbc.repository.RegistrationsRepo
import kornell.core.to.RegistrationsTO

@Path("registrations")
@Produces(Array(RegistrationsTO.TYPE))
class RegistrationsResource {
  @GET
  def get(implicit @Context sc: SecurityContext) =
    AuthRepo.withPerson { implicit person =>
      RegistrationsRepo.unsigned
    }
}

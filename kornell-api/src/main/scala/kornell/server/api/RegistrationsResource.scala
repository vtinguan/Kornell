package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.repository.Entities
import kornell.server.repository.TOs
import kornell.server.repository.jdbc.Auth
import kornell.server.repository.jdbc.Registrations
import kornell.core.to.RegistrationsTO
import kornell.server.repository.TOs

@Path("registrations")
@Produces(Array(RegistrationsTO.TYPE))
class RegistrationsResource {
  @GET
  def get(implicit @Context sc: SecurityContext) =
    Auth.withPerson { implicit person =>
      Registrations.unsigned
    }
}

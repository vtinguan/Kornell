package kornell.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.repository.Beans
import kornell.repository.TOs
import kornell.repository.jdbc.Auth
import kornell.repository.jdbc.Registrations
import kornell.core.shared.to.RegistrationsTO

@Path("registrations")
@Produces(Array(RegistrationsTO.TYPE))
class RegistrationsResource extends Resource with Beans with TOs {

  @GET
  def get(implicit @Context sc: SecurityContext) =
    Auth.withPerson { implicit person =>
      Registrations.unsigned
    }
}

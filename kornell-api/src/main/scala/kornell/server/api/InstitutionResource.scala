package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.repository.jdbc.Auth
import kornell.server.repository.jdbc.Institutions
import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper
import kornell.core.entity.Institution
import kornell.server.repository.jdbc.Registrations

@Produces(Array(Institution.TYPE))
//TODO:Refactor to use /institutions/{uuid} and /institutions?name={name}
class InstitutionResource(uuid: String) {
  @GET
  def get = Institutions.byUUID(uuid)
  
  @PUT
  @Produces(Array("text/plain"))
  def acceptTerms(implicit @Context sc: SecurityContext) = Auth.withPerson{ p =>
    Registrations(p.getUUID, uuid).acceptTerms
  }
  
}
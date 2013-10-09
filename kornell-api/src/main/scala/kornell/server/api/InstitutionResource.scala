package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.repository.jdbc.Auth
import kornell.server.repository.jdbc.Institutions
import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper
import kornell.core.shared.data.Institution

@Produces(Array(Institution.TYPE))
class InstitutionResource(institutionUUID: String) {
  @GET
  def get = Institutions.byUUID(institutionUUID)

  @PUT
  @Produces(Array("text/plain"))
  def acceptTerms(implicit @Context sc: SecurityContext) =
    Auth.withPerson { person =>
      sql"""update Registration
      	 set termsAcceptedOn=now()
      	 where person_uuid=${person.getUUID}
      	   and institution_uuid=$institutionUUID
      	   """.executeUpdate
    }

}
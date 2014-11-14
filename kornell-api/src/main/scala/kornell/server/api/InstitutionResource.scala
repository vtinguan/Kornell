package kornell.server.api

import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.Institution
import javax.ws.rs.PUT
import javax.ws.rs.GET
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.jdbc.repository.AuthRepo
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import kornell.server.jdbc.repository.PersonRepo
import kornell.core.to.InstitutionRegistrationPrefixesTO


@Produces(Array(Institution.TYPE))
class InstitutionResource(uuid: String) {
  @GET
  def get = InstitutionsRepo.byUUID(uuid)
  
  @PUT
  @Produces(Array("text/plain"))
  @Path("acceptTerms")
  def acceptTerms(implicit @Context sc: SecurityContext) = AuthRepo().withPerson{ p =>
    PersonRepo(p.getUUID).acceptTerms
  }
  
  @PUT
  @Produces(Array("text/plain"))
  @Consumes(Array(Institution.TYPE))
  def update(implicit @Context sc: SecurityContext, institution: Institution) = AuthRepo().withPerson{ p =>
    InstitutionsRepo.update(institution)
  }
  
  @GET
  @Produces(Array(InstitutionRegistrationPrefixesTO.TYPE))
  @Path("registrationPrefixes")
  def getRegistrationPrefixes(implicit @Context sc: SecurityContext) = AuthRepo().withPerson{ p =>
    InstitutionsRepo.getRegistrationPrefixes(uuid)
  }
  
}
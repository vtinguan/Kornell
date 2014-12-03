package kornell.server.api

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import kornell.core.entity.Institution
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.util.Conditional.toConditional
import kornell.server.util.RequirementNotMet
import kornell.core.to.InstitutionRegistrationPrefixesTO
import kornell.server.jdbc.repository.InstitutionRepo


class InstitutionResource(uuid: String) {
  
  @GET
  @Produces(Array(Institution.TYPE))
  def get =  {
    InstitutionRepo(uuid).get
   }.requiring(isPlatformAdmin, RequirementNotMet).get
  
  @PUT
  @Consumes(Array(Institution.TYPE))
  @Produces(Array(Institution.TYPE))
  def update(institution: Institution) = {
    InstitutionRepo(uuid).update(institution)
  }.requiring(isPlatformAdmin, RequirementNotMet).get
  
  @GET
  @Produces(Array(InstitutionRegistrationPrefixesTO.TYPE))
  @Path("registrationPrefixes")
  def getRegistrationPrefixes() = {
    InstitutionRepo(uuid).getRegistrationPrefixes
  }.requiring(isPlatformAdmin, RequirementNotMet).get
  
}

object InstitutionResource {
    def apply(uuid: String) = new InstitutionResource(uuid)
}
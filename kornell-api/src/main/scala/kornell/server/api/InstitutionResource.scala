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


@Produces(Array(Institution.TYPE))
class InstitutionResource(uuid: String) {
  def this() = this(null)
  
  @GET
  def get =  {
    InstitutionsRepo.byUUID(uuid)
   }.requiring(isPlatformAdmin, RequirementNotMet).get
  
  @PUT
  @Consumes(Array(Institution.TYPE))
  def update(institution: Institution) = {
    InstitutionsRepo.update(institution)
  }.requiring(isPlatformAdmin, RequirementNotMet).get
  
  @POST
  @Consumes(Array(Institution.TYPE))
  def create(institution: Institution) = {
    InstitutionsRepo.create(institution)
  }.requiring(isPlatformAdmin, RequirementNotMet).get
  
  @GET
  @Produces(Array(InstitutionRegistrationPrefixesTO.TYPE))
  @Path("registrationPrefixes")
  def getRegistrationPrefixes() = {
    InstitutionsRepo.getRegistrationPrefixes(uuid)
  }.requiring(isPlatformAdmin, RequirementNotMet).get
  
}

object InstitutionResource {
    def apply() = new InstitutionResource()
    def apply(uuid: String) = new InstitutionResource(uuid)
}
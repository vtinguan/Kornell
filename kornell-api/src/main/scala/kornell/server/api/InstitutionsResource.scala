package kornell.server.api
import kornell.core.entity.Institution
import javax.ws.rs._
import kornell.server.jdbc.repository.InstitutionsRepo

@Path("institutions")
@Produces(Array(Institution.TYPE))
class InstitutionsResource {
  
  @Path("{uuid}")
  def get(@PathParam("uuid") uuid:String):InstitutionResource = new InstitutionResource(uuid) 
  
  @GET
  def getByName(@QueryParam("name") name:String) = InstitutionsRepo.byName(name)

}
package kornell.server.api
import kornell.core.entity.Institution
import javax.ws.rs._

@Path("institutions")
@Produces(Array(Institution.TYPE))
class InstitutionsResource {
  
  @Path("{institutionName}")
  def get(@PathParam("institutionName") institutionName:String):InstitutionResource = new InstitutionResource(institutionName) 

}
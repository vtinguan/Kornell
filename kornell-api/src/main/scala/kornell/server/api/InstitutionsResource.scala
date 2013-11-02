package kornell.server.api
import kornell.core.entity.Institution
import javax.ws.rs._

@Path("institutions")
@Produces(Array(Institution.TYPE))
class InstitutionsResource {
  
  @Path("{uuid}")
  def get(@PathParam("uuid") uuid:String):InstitutionResource = new InstitutionResource(uuid) 

}
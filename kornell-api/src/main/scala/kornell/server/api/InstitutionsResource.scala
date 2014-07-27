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
  def getBy(@QueryParam("name") name:String, @QueryParam("hostName") hostName:String) = 
    if(name != null)
      InstitutionsRepo.byName(name)
    else
      InstitutionsRepo.byHostName(hostName)

}
package kornell.server.api
import kornell.core.entity.Institution
import javax.ws.rs._
import kornell.server.jdbc.repository.InstitutionsRepo
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.Context
import kornell.server.util.Conditional.toConditional
import kornell.server.util.RequirementNotMet

@Path("institutions")
class InstitutionsResource {
  
  @Path("{uuid}")
  def get(@PathParam("uuid") uuid:String):InstitutionResource = new InstitutionResource(uuid) 
  
  @GET
  @Produces(Array(Institution.TYPE))
  def getBy(@Context resp: HttpServletResponse,
      @QueryParam("name") name:String, @QueryParam("hostName") hostName:String) = 
    {
	    if(name != null)
	      InstitutionsRepo.byName(name)
	    else
	      InstitutionsRepo.byHostName(hostName)
    } match {
      case Some(institution) => institution
      case None => resp.sendError(HttpServletResponse.SC_NO_CONTENT, "Institution not found.")
    }
    
  @POST
  @Produces(Array(Institution.TYPE))
  @Consumes(Array(Institution.TYPE))
  def create(institution: Institution) = {
    InstitutionsRepo.create(institution)
  }.requiring(isPlatformAdmin, RequirementNotMet).get

}

object InstitutionsResource {
  def apply() = new InstitutionsResource()
}
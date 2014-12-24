package kornell.server.api
import kornell.core.entity.Institution
import javax.ws.rs._
import kornell.server.jdbc.repository.InstitutionsRepo
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.Context
import kornell.server.util.Conditional.toConditional
import kornell.server.util.RequirementNotMet
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Instance
import javax.inject.Inject
import kornell.server.auth.Authorizator

@ApplicationScoped
@Path("institutions")
class InstitutionsResource @Inject() (
  val auth:Authorizator,
  val ittsRepo:InstitutionsRepo,
  val institutionResourceBean:Instance[InstitutionResource]
  ) {
  
  def this() = this(null,null,null)
  
  @Path("{uuid}")
  def get(@PathParam("uuid") uuid:String):InstitutionResource =
    institutionResourceBean.get.withUUID(uuid)
  
  @GET
  @Produces(Array(Institution.TYPE))
  def getBy(@Context resp: HttpServletResponse,
      @QueryParam("name") name:String, @QueryParam("hostName") hostName:String) = 
    {
	    if(name != null)
	      ittsRepo.byName(name)
	    else
	      ittsRepo.byHostName(hostName)
    } match {
      case Some(institution) => institution
      case None => resp.sendError(HttpServletResponse.SC_NO_CONTENT, "Institution not found.")
    }
    
  @POST
  @Produces(Array(Institution.TYPE))
  @Consumes(Array(Institution.TYPE))
  def create(institution: Institution) = {
    ittsRepo.create(institution)
  }.requiring(auth.isPlatformAdmin, RequirementNotMet).get

}
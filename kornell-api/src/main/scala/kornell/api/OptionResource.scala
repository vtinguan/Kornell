package kornell.api

import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.inject.Inject


@Path("option")
@Produces(Array("text/plain"))
class OptionResource @Inject() (val svc:OptionService) {
  def this() = this(null)
  
  @GET
  def impl = svc.doTheTrick.getOrElse("Not Today little one")
	
}
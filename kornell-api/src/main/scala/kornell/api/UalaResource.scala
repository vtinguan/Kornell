package kornell.api

import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType._
import javax.ws.rs.GET

@Path("uala")
@Produces(Array(TEXT_PLAIN))
class UalaResource {
	@GET
	def get = "UALA!!!"
}
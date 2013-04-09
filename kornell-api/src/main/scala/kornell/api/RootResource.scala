package kornell.api

import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.GET

@Path("")
class RootResource {
	@Produces(Array("text/plain"))
	@GET
	def get = "Welcome to Kornell API\n"
}
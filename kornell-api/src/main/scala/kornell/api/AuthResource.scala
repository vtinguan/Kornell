package kornell.api

import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response.Status.FORBIDDEN
import javax.ws.rs.core.Response.ok
import javax.ws.rs.core.Response.status
import javax.ws.rs.FormParam
import javax.ws.rs.GET

@Path("auth")
class AuthResource {
  @GET
  def get() = ok build
}
package kornell.api

import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response.Status.FORBIDDEN
import javax.ws.rs.core.Response.ok
import javax.ws.rs.core.Response.status
import javax.ws.rs.FormParam

//TODO: Make sure this only happens over SSL
@Path("auth")
class AuthResource {
  @POST 
  @Path("checkPassword")
  def checkPassword(@FormParam("username") username:String, 
		  			@FormParam("password") password: String) =
    (if (password == "uala") ok
     else status(FORBIDDEN)) build
}
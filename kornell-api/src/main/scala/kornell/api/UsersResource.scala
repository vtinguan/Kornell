package kornell.api

import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType._
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import kornell.data.UserInfo
import javax.ws.rs.GET

@Path("users")
class UsersResource {
  
  
  @Path("{userId}")
  def getUser(@PathParam("userId") userId:String) = new UserResource(userId) 
  

}
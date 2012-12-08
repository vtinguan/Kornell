package kornell.api

import javax.ws.rs._
import javax.ws.rs.core.MediaType._
import kornell.data.UserInfo
import scala.reflect.BeanProperty
import javax.ws.rs.core.Response._
import javax.ws.rs.core.Response.Status._

@Produces(Array(APPLICATION_JSON))
class UserResource(@BeanProperty var userId:String) {
  @GET
  def getUserInfo = 
    if (userId == "ftal") new UserInfo("Fulano de Tal")
    else null
    
   @GET
   @Path("checkPassword")
   def checkPassword(password:String) =
     (if (password == "uala") ok
      else status(UNAUTHORIZED)
     	   header("WWW-Authenticate", "Basic realm=Kornell")) build
	
}

package kornell.api

import javax.ws.rs._
import javax.ws.rs.core.MediaType._
import scala.reflect.BeanProperty
import javax.ws.rs.core.Response._
import javax.ws.rs.core.Response.Status._
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import javax.persistence.EntityManager
import javax.inject.Inject
import javax.enterprise.context.RequestScoped
import kornell.repository.slick.plain.Persons
import kornell.core.shared.to.UserInfoTO
import kornell.repository.TOs

@Produces(Array(UserInfoTO.TYPE))
@Path("user")
@RequestScoped
class UserResource extends TOs{
  
  @GET
  def get(@Context sc: SecurityContext):Option[UserInfoTO] = {
    val username = sc.getUserPrincipal.getName
    val p = Persons.byUsername(username)    
    if (p.isDefined){
    	val user = newUserInfoTO
    	user.setPerson(p.get)
    	user.setSigningNeeded(true)
    	Some(user)
    } else None
  }
}

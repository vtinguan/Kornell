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
import kornell.repository.jdbc.Auth
import kornell.repository.jdbc.Registrations

@Produces(Array(UserInfoTO.TYPE))
@Path("user")
class UserResource extends Resource with TOs{

  @GET
  def get(implicit @Context sc: SecurityContext):Option[UserInfoTO] =
    Auth.withPerson { p =>
    	val user = newUserInfoTO
    	user.setPerson(p)
    	val signingNeeded = Registrations.signingNeeded(p)
    	user.setSigningNeeded(signingNeeded)
    	Option(user)
    }
  
}

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

@Produces(Array("application/vnd.kornell.v1.person+json"))
@Path("user")
@RequestScoped
class UserResource {
  
  @GET
  def get(@Context sc: SecurityContext) = {
    val username = sc.getUserPrincipal.getName
    val p = Persons.byUsername(username)
    p
  }

}

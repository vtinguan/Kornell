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

@Produces(Array("application/vnd.kornell.v1.person+json"))
@Path("user")
@RequestScoped
class UserResource @Inject() (val em: EntityManager) {
  def this() = this(null)
  
  @GET
  def get(@Context sc: SecurityContext) = {
    val username = sc.getUserPrincipal.getName

    em.createNamedQuery("person.byUsername")
      .setParameter("username", username)
      .setMaxResults(1)
      .getSingleResult
  }

}

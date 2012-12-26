package kornell.api

import javax.ws.rs._
import javax.ws.rs.core.MediaType._
import kornell.data.UserInfo
import scala.reflect.BeanProperty
import javax.ws.rs.core.Response._
import javax.ws.rs.core.Response.Status._
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import javax.persistence.EntityManager
import javax.inject.Inject

@Produces(Array("application/vnd.kornell.v1.person+json"))
@Path("user")
class UserResource(@BeanProperty var username: String) {

  @Inject
  var em: EntityManager = _

  def this() = this(null)

  @GET
  def get(@Context sc: SecurityContext) = {
    if (username == null) username = sc.getUserPrincipal.getName

    em.createNamedQuery("person.byUsername")
      .setParameter("username", username)
      .setMaxResults(1)
      .getSingleResult
  }

}

package kornell.server.api

import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.Person
import javax.ws.rs.PUT
import javax.ws.rs.GET
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.jdbc.repository.AuthRepo
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import kornell.server.jdbc.repository.PersonRepo


@Produces(Array(Person.TYPE))
class PersonResource(uuid: String) {
  @GET
  def get = PersonRepo(uuid)
  
}
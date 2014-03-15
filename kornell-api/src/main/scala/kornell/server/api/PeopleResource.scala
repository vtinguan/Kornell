package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import kornell.server.jdbc.repository.PeopleRepo
import kornell.core.entity.People

@Path("people")
@Produces(Array(People.TYPE))
class PeopleResource {
  
  @Path("{uuid}")
  def get(@PathParam("uuid") uuid:String):PersonResource = new PersonResource(uuid) 
  
  @GET
  def findBySearchTerm(@QueryParam("search") search:String) = PeopleRepo.findBySearchTerm(search)

}
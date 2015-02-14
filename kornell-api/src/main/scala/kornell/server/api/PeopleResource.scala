package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import kornell.server.jdbc.repository.PeopleRepo
import kornell.core.entity.People
import kornell.server.jdbc.repository.AuthRepo
import kornell.core.error.exception.UnauthorizedAccessException
import kornell.core.to.PeopleTO

@Path("people")
@Produces(Array(People.TYPE))
class PeopleResource() {
  
  @Path("{uuid}")
  def get(@PathParam("uuid") uuid:String):PersonResource = new PersonResource(uuid) 
  
  @GET
  @Produces(Array(PeopleTO.TYPE))
  def findBySearchTerm(@QueryParam("institutionUUID") institutionUUID:String,
      @QueryParam("search") search:String) = PeopleRepo.findBySearchTerm(institutionUUID, search)
}

object PeopleResource{
  def apply() = new PeopleResource()
}
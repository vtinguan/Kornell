package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import kornell.server.jdbc.repository.PeopleRepo
import kornell.core.entity.People
import kornell.server.jdbc.repository.AuthRepo
import kornell.core.error.exception.ServerErrorException
import kornell.core.error.exception.UnauthorizedAccessException

@Path("people")
@Produces(Array(People.TYPE))
class PeopleResource() {
  
  @Path("{uuid}")
  def get(@PathParam("uuid") uuid:String):PersonResource = new PersonResource(uuid) 
  
  @GET
  def findBySearchTerm(@QueryParam("institutionUUID") institutionUUID:String,
      @QueryParam("search") search:String) = PeopleRepo.findBySearchTerm(institutionUUID, search)
     
  @Path("throw")
  def throwException = {
    throw new UnauthorizedAccessException("test")
  }
}

object PeopleResource{
  def apply() = new PeopleResource()
}
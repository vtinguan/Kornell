package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import kornell.server.jdbc.repository.PeopleRepo
import kornell.core.entity.People
import kornell.server.jdbc.repository.AuthRepo
import javax.inject.Inject
import javax.enterprise.inject.Instance

@Path("people")
@Produces(Array(People.TYPE))
class PeopleResource @Inject()(
	val peopleRepo:PeopleRepo,
	val personResourceBean:Instance[PersonResource]
) {
  
  def this() = this(null,null)
  
  @Path("{uuid}")
  def get(@PathParam("uuid") uuid:String):PersonResource = personResourceBean.get.withUUID(uuid) 
  
  @GET
  def findBySearchTerm(@QueryParam("institutionUUID") institutionUUID:String,
      @QueryParam("search") search:String) = peopleRepo.findBySearchTerm(institutionUUID, search)
}
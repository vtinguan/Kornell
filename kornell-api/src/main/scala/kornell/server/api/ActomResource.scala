package kornell.server.api

import java.sql.ResultSet
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.PathParam
import javax.ws.rs.PUT
import javax.ws.rs.GET
import javax.ws.rs.Path
import kornell.server.jdbc.SQL._

class ActomResource(enrollmentUUID: String, actomKey: String) {
  implicit def toString(rs: ResultSet): String = rs.getString("entryValue")

  @GET
  def get() = actomKey

  @Path("entries/{entryKey}")
  @Produces(Array("text/plain"))
  @GET
  def getValue(@PathParam("entryKey") entryKey: String) = sql"""
  	select * from ActomEntries
  	where enrollment_uuid = $enrollmentUUID 
  	and actomKey = $actomKey
  	and entryKey = $entryKey
  """.get[String]

  @Path("entries/{entryKey}")
  @Produces(Array("text/plain"))
  @Consumes(Array("text/plain"))
  @PUT
  def putValue(@PathParam("entryKey") entryKey: String, entryValue:String) = sql"""
  	insert into ActomEntries (uuid, enrollment_uuid, actomKey, entryKey, entryValue) 
  	values (${randomUUID}, ${enrollmentUUID} , ${actomKey}, ${entryKey}, ${entryValue})
  	on duplicate key update entryValue = ${entryValue}
  """.executeUpdate

}

object ActomResource {
  def apply(enrollmentUUID: String, actomKey: String) = new ActomResource(enrollmentUUID, actomKey);
}
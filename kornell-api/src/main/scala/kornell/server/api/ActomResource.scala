package kornell.server.api

import javax.ws.rs._
import java.sql.ResultSet
import kornell.server.repository.jdbc.SQLInterpolation._
import kornell.core.util.UUID._

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
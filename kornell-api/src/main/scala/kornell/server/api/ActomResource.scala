package kornell.server.api

import java.sql.ResultSet
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.PathParam
import javax.ws.rs.PUT
import javax.ws.rs.GET
import javax.ws.rs.Path
import kornell.server.jdbc.SQL._
import kornell.core.entity.ActomEntries
import scala.collection.JavaConversions._
import javax.ws.rs.core.Response
import kornell.server.repository.Entities
import java.util.HashMap
import kornell.server.cep.EnrollmentCEP
import kornell.server.jdbc.repository.ActomEntriesRepo

class ActomResource(enrollmentUUID: String, actomKey: String) {
  implicit def toString(rs: ResultSet): String = rs.getString("entryValue")

  @GET
  def get() = actomKey

  @Path("entries/{entryKey}")
  @Produces(Array("text/plain"))
  @GET
  def getValue(@PathParam("entryKey") entryKey: String) = 
    ActomEntriesRepo.getValue(enrollmentUUID, actomKey, entryKey)

  @Path("entries/{entryKey}")
  @Produces(Array("text/plain"))
  @Consumes(Array("text/plain"))
  @PUT
  def putValue(@PathParam("entryKey") entryKey: String, entryValue: String) = {
    sql"""
  	insert into ActomEntries (uuid, enrollment_uuid, actomKey, entryKey, entryValue) 
  	values (${randomUUID}, ${enrollmentUUID} , ${actomKey}, ${entryKey}, ${entryValue})
  	on duplicate key update entryValue = ${entryValue}
  """.executeUpdate  

  }

  @Path("entries")
  @Consumes(Array(ActomEntries.TYPE))
  @Produces(Array(ActomEntries.TYPE))
  @PUT
  def putEntries(entries: ActomEntries) = if (entries != null) {
    for ((key, value) <- entries.getEntries()) putValue(key, value)
    //TODO: Recalculate progress only on progress changed
    EnrollmentCEP.onProgress(enrollmentUUID)
    entries
  }

  @Path("entries")
  @Produces(Array(ActomEntries.TYPE))
  @GET
  def getEntries(): ActomEntries = {
    val entries = Entities.newActomEntries(enrollmentUUID, actomKey, new HashMap[String, String])
    sql"""
  	select * from ActomEntries 
  	where enrollment_uuid=${enrollmentUUID}
  	  and actomKey=${actomKey}""".foreach { rs =>
      entries.getEntries().put(rs.getString("entryKey"), rs.getString("entryValue"))
    }
    entries
  }
}

object ActomResource {
  def apply(enrollmentUUID: String, actomKey: String) = new ActomResource(enrollmentUUID, actomKey);
}
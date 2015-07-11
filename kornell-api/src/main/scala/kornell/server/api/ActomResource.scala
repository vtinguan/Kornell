package kornell.server.api

import java.sql.ResultSet
import java.util.HashMap
import scala.collection.JavaConversions._
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import kornell.core.entity.ActomEntries
import kornell.server.ep.EnrollmentSEP
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.ActomEntriesRepo
import kornell.server.repository.Entities
import kornell.server.util.ServerTime
import kornell.core.scorm12.rte.RTE
import kornell.core.scorm12.rte.DMElement
import kornell.server.scorm12.SCORM12
import kornell.server.jdbc.repository.AuthRepo

class  ActomResource(enrollmentUUID: String, actomURL: String) {
  implicit def toString(rs: ResultSet): String = rs.getString("entryValue")

  @GET
  def get() = actomKey

  val actomKey = if (actomURL.contains("?"))
    actomURL.substring(0, actomURL.indexOf("?"))
  else
    actomURL

  @Path("entries/{entryKey}")
  @Produces(Array("text/plain"))
  @GET
  def getValue(@PathParam("entryKey") entryKey: String) =
    ActomEntriesRepo.getValue(enrollmentUUID, actomKey, entryKey)

  @Path("entries/{entryKey}")
  @Produces(Array("text/plain"))
  @Consumes(Array("text/plain"))
  @PUT
  def putValue(@PathParam("entryKey") entryKey: String, entryValue: String, modifiedAt: String) = {
    updateEventModel(entryKey, entryValue, modifiedAt)
    updateQueryModel(entryKey, entryValue)
  }

  def updateEventModel(entryKey: String, entryValue: String, modifiedAt: String) = {
    val currentValue = getValue(entryKey)
    if (entryValue != currentValue)
      sql"""
  		insert into ActomEntryChangedEvent (uuid, enrollment_uuid, actomKey, entryKey, entryValue, ingestedAt) 
  		values (${randomUUID}, ${enrollmentUUID} , ${actomKey}, ${entryKey}, ${entryValue}, ${modifiedAt})
  	  """.executeUpdate
  }

  def updateQueryModel(entryKey: String, entryValue: String) = sql"""
  	insert into ActomEntries (uuid, enrollment_uuid, actomKey, entryKey, entryValue) 
  	values (${randomUUID}, ${enrollmentUUID} , ${actomKey}, ${entryKey}, ${entryValue})
  	on duplicate key update entryValue = ${entryValue}
  """.executeUpdate

  @Path("entries")
  @Consumes(Array(ActomEntries.TYPE))
  @Produces(Array(ActomEntries.TYPE))
  @PUT
  def putEntries(entries: ActomEntries) = {
    val modifiedAt = entries.getLastModifiedAt()
    val actomEntries = entries.getEntries
    for ((key, value) <- actomEntries) putValue(key, value, modifiedAt)
    
    val hasProgress = containsProgress(actomEntries)
    if (hasProgress)
      EnrollmentSEP.onProgress(enrollmentUUID)

    val hasAssessment = containsAssessment(actomEntries)
    if (hasAssessment) {
      EnrollmentSEP.onAssessment(enrollmentUUID);
    }
    entries
  }

  def containsProgress(entries: java.util.Map[String, String]) =
      entries.containsKey("cmi.core.lesson_status") ||
      entries.containsKey("cmi.core.lesson_location") ||
      entries.containsKey("cmi.core.suspend_data")

  def containsAssessment(entries: java.util.Map[String, String]) =
    entries.containsKey("cmi.core.score.raw") || 
    entries.containsKey("cmi.suspend_data")
    
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
    initialize(entries)
  }
  
  
  def initialize(aentries:ActomEntries):ActomEntries = AuthRepo().withPerson { person =>
    val entries = aentries.getEntries()    
    val initdEntries = SCORM12.dataModel.initialize(entries,person)    
    aentries.setEntries(initdEntries)
    aentries
  }    
}

object ActomResource {
  def apply(enrollmentUUID: String, actomKey: String) = new ActomResource(enrollmentUUID, actomKey);
}
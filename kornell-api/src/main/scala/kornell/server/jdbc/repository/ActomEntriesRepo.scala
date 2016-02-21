package kornell.server.jdbc.repository
import kornell.server.jdbc.SQL._

object ActomEntriesRepo {
  def getValue(enrollmentUUID: String, actomKey: String, entryKey: String) = {
    val value = sql"""
  	select * from ActomEntries
  	where enrollment_uuid = $enrollmentUUID 
  	and actomKey like $actomKey
  	and entryKey = $entryKey
  """.first[String] { _.getString("entryValue") }
    value
  }
  
  def getValues(enrollmentUUID: String, actomKey: String, entryKey: String) = {
    val value = sql"""
  	select * from ActomEntries
  	where enrollment_uuid = $enrollmentUUID 
  	and actomKey like $actomKey
  	and entryKey = $entryKey
  """.map[String] { _.getString("entryValue") }
    value
  }
}
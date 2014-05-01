package kornell.server.jdbc.repository
import kornell.server.jdbc.SQL._

object ActomEntriesRepo {
  def getValue(enrollmentUUID:String,actomKey:String,entryKey:String) = sql"""
  	select * from ActomEntries
  	where enrollment_uuid = $enrollmentUUID 
  	and actomKey = $actomKey
  	and entryKey = $entryKey
  """.first map { _.getString("entryValue") } 
}
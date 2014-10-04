package kornell.server.jdbc.repository
import kornell.server.jdbc.SQL._
import scala.collection.mutable.HashMap

object ActomEntriesRepo {
  def getValue(enrollmentUUID:String,actomKey:String,entryKey:String) = sql"""
  	select * from ActomEntries
  	where enrollment_uuid = $enrollmentUUID 
  	and actomKey = $actomKey
  	and entryKey = $entryKey
  """.first[String]{ _.getString("entryValue") }
  
  def getValues(enrollmentUUID:String,
      actomKey:String,
      altActomKey:String):Map[String,String] = {
	val values = HashMap.empty[String,String]
	sql"""
	  	select * from ActomEntries
	  	where enrollment_uuid = $enrollmentUUID 
	  	and (actomKey = ${actomKey} OR actomKey = ${altActomKey})
	  """.foreach { rs =>
	  	  values += (rs.getString("entryKey") -> rs.getString("entryValue"))
	  	}
	values.toMap
  }
}
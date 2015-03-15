package kornell.server.dev

import kornell.server.jdbc.SQL._
import java.text.DateFormat
import java.text.SimpleDateFormat
import kornell.server.jdbc.DataSources
import java.sql.Connection

object ZipEventsTableFix extends App {

  val df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS")
  
  def zipEntry(enrollmentUUID: String, actomKey: String, entryKey: String) = {
     val conn = DataSources.connectionFactory.get()
     val stmt = conn.prepareStatement("delete from ActomEntryChangedEvent where uuid=?")
     var reds = 0
     var last_value:String = null
	 val entries = sql"""
	  select *
	  from ActomEntryChangedEvent
	  where enrollment_uuid=$enrollmentUUID
	   and actomKey=$actomKey
	   and entryKey=$entryKey
	  order by ingestedAt
    """.foreach { rs => 
       count += 1
       val uuid = rs.getString("uuid");
       val entryKey = rs.getString("entryValue")
       val entryValue = rs.getString("entryValue")
       val entryTime  = rs.getTimestamp("ingestedAt")
       val entryTimeS = df.format(entryTime)
       val isRedundant = (entryValue == last_value)
       if (isRedundant) {
         reds += 1
         stmt.setString(1, uuid)
         stmt.addBatch()
       }
       //println(s"[${if(isRedundant)"R"else"XXX";}][$entryTimeS][$entryKey]=[$entryValue]")
       last_value = entryValue
     }
     println(s"*** Removing [$reds] redundant records from ActomEntryChangedEvent [$enrollmentUUID, $actomKey, $entryKey]")
     if (reds > 0) stmt.executeUpdate     
     stmt.close
     conn.close
  }

  def zipActom(enrollmentUUID: String, actomKey: String) = {
    println(s"Zipping [$enrollmentUUID][$actomKey]")
    val entryKeys = sql"""
	 select distinct(entryKey)
	 from ActomEntryChangedEvent
	 where enrollment_uuid=$enrollmentUUID
	   and actomKey=$actomKey
	 """.map[String]
    entryKeys foreach { key => zipEntry(enrollmentUUID, actomKey, key) }
  }

  def zipEnrollment(enrollmentUUID: String) = {
    println(s"** Zipping [${enrollmentUUID}]")
    println(s"** Count [${count}]")
    val actoms = sql"""
    select distinct(actomKey)
    from ActomEntryChangedEvent
    where enrollment_uuid=${enrollmentUUID}
    """.map[String]
    actoms foreach { actomKey => zipActom(enrollmentUUID, actomKey) }
  }

  var count = 0
  val enrollments = sql"""select distinct(enrollment_uuid)
  	from ActomEntryChangedEvent""".map[String]
  println(s"* Zipping ${enrollments.size} enrollments")
  //God bless parallel colletions #scala
  enrollments.par foreach zipEnrollment

}
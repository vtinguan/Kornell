package kornell.server.util

import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Date
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTime

object ServerTime {
  val fmt = ISODateTimeFormat.dateTime
  

  def now():String = fmt.print(new DateTime)
  
  def todayStart():String = fmt.print(new DateTime().withTimeAtStartOfDay)
  
  def todayEnd():String = fmt.print(new DateTime().plusDays(1).withTimeAtStartOfDay().minusMillis(1))
}
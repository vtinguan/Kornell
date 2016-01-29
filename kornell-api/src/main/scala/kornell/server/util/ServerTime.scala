package kornell.server.util

import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.format.DateTimeFormat

object ServerTime {
  val fmt = ISODateTimeFormat.dateTime
  
  def now():String = fmt.print(DateTime.now)
  
  def todayStart():String = fmt.print(DateTime.now.withTimeAtStartOfDay)
  
  def todayEnd():String = fmt.print(DateTime.now.plusDays(1).withTimeAtStartOfDay.minusMillis(1))
  
  def adjustTimezoneOffset(offset: Int): String = {
    fmt.print(DateTime.now.minusMinutes(offset + ServerTime.getCurrentTimeZoneOffset))
  }
  
  def adjustTimezoneOffset(ds: String, offset: Int): String = {
    if(ds == null)
      ds
    else {
      try {
    	fmt.print(fmt.parseDateTime(ds).minusMinutes(offset + ServerTime.getCurrentTimeZoneOffset))
      } catch {
        case iae: IllegalArgumentException =>  
	      try {
	    	fmt.print(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(ds).minusMinutes(offset + ServerTime.getCurrentTimeZoneOffset))
	      } catch {
	        case iae: IllegalArgumentException =>  fmt.print(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.0").parseDateTime(ds).minusMinutes(offset + ServerTime.getCurrentTimeZoneOffset))
	      }
      }
    }
  }
  
  def getCurrentTimeZoneOffset():Int = {
    val tz = DateTimeZone.getDefault
    val instant = DateTime.now.getMillis
    val offsetInMilliseconds = tz.getOffset(instant)
    val hours = TimeUnit.MILLISECONDS.toMinutes(offsetInMilliseconds)
    hours.toInt
  }
  
}
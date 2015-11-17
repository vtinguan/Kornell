package kornell.server.util

import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.format.DateTimeFormat

import java.util.logging.Logger

object ServerTime {
  
  val log = Logger.getLogger(getClass.getName)
  
  val fmt = ISODateTimeFormat.dateTime
  
  def now():String = fmt.print(DateTime.now)
  
  def todayStart():String = fmt.print(DateTime.now.withTimeAtStartOfDay)
  
  def todayEnd():String = fmt.print(DateTime.now.plusDays(1).withTimeAtStartOfDay.minusMillis(1))
  
  def adjustTimezoneOffset(offset: Int): String = {
    fmt.print(DateTime.now.minusMinutes(offset + ServerTime.getCurrentTimeZoneOffset))
  }
  
  def adjustTimezoneOffsetDate(ds: String, offset: Int): DateTime = {
    //@TODO unify all the formats across all entities from all databases 
    if(ds == null)
      null
    else {
      try {
    	fmt.parseDateTime(ds).minusMinutes(offset + 0)
      } catch {
        case iae: IllegalArgumentException =>  
	      try {
		    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.0").parseDateTime(ds).minusMinutes(offset + ServerTime.getCurrentTimeZoneOffset)
	      } catch {
	      	case iae: IllegalArgumentException =>  
		      try {
		    	DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(ds).minusMinutes(offset + ServerTime.getCurrentTimeZoneOffset)
		      } catch {
		        case iae: IllegalArgumentException => DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(ds).minusMinutes(offset + ServerTime.getCurrentTimeZoneOffset)
		      }
	      }
      }
    }
  }
  
  def adjustTimezoneOffset(ds: String, offset: Int): String = {
    val dateStr = fmt.print(adjustTimezoneOffsetDate(ds, offset))
    log.info("[INFO] Date Adjusted from: " + ds + " to " + dateStr)
    dateStr
  }
  
  def getCurrentTimeZoneOffset():Int = {
    val tz = DateTimeZone.getDefault
    val instant = DateTime.now.getMillis
    val offsetInMilliseconds = tz.getOffset(instant)
    val hours = TimeUnit.MILLISECONDS.toMinutes(offsetInMilliseconds)
    hours.toInt
  }
  
}
package kornell.server.util

import kornell.server.jdbc.repository.InstitutionRepo
import kornell.server.jdbc.repository.PersonRepo
import java.util.Date
import org.joda.time.DateTimeZone
import org.joda.time.DateTime
import kornell.server.jdbc.SQL._

class DateConverter(personUUID: String) {
  
  val institutionTimezone = sql"""select i.timeZone from Person p left join Institution i on p.institutionUUID = i.uuid where p.uuid = ${personUUID}""".first[String]
    
  def dateToInstitutionTimezone(date: Date) = {
    Option(date) match {
      case Some(s) => {
        val timezone = DateTimeZone.forID(institutionTimezone.get)
        new DateTime(s).toDateTime(timezone).toDate
      }
      case None => null
    }
  }
}

object DateConverter {
  def apply(personUUID: String) = new DateConverter(personUUID)
}
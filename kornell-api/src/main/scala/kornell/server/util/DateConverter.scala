package kornell.server.util

import java.util.Date
import org.joda.time.DateTimeZone
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.jdbc.repository.InstitutionRepo

class DateConverter(personUUID: String) {    
  def dateToInstitutionTimezone(date: Date) = {
    Option(date) match {
      case Some(s) => {
        val st = s.getTime
        new Date(st - DateTimeZone.forID(PeopleRepo.getTimezoneByUUID(personUUID).get).getStandardOffset(st))
      }
      case None => null
    }
  }
  
  def dateToInstitutionTimezone(date: Date, institutionUUID: String) = {
    Option(date) match {
      case Some(s) => {
        val st = s.getTime
        new Date(st - DateTimeZone.forID(InstitutionRepo(institutionUUID).get.getTimeZone).getStandardOffset(st))
      }
      case None => null
    }
  }
}

object DateConverter {  	
  def apply() = new DateConverter(null)
  def apply(personUUID: String) = new DateConverter(personUUID)
}
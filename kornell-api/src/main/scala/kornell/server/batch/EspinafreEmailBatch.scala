package kornell.server.batch

import javax.ejb.Singleton
import javax.ejb.Schedule
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.util.EmailService
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.core.entity.InstitutionType

@Singleton
class EspinafreEmailBatch {
  
  //3pm Brasilia time
  @Schedule(minute="58", hour="23", persistent=false)
  def sendEspinafreEmailReminder() = {
    val institution = InstitutionsRepo.byType(InstitutionType.DASHBOARD).get
    EnrollmentsRepo.getEspinafreEmailList.foreach(
        person => EmailService.sendEmailEspinafreReminder(person, institution))
  }
}
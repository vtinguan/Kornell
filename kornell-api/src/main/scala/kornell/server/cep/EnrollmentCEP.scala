package kornell.server.cep

import kornell.server.jdbc.repository.EnrollmentRepo
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.server.jdbc.repository.CourseVersionRepo
import kornell.core.entity.ContentSpec._

object EnrollmentCEP {
  //TODO: Consider new query (join) or Slick
  //TODO: ASYNC / NOTIFY
  def onProgress(enrollmentUUID: String) = 
    EnrollmentRepo(enrollmentUUID).updateProgress    

}
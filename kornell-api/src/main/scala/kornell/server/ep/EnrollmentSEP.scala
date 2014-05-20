package kornell.server.ep

import kornell.server.jdbc.repository.EnrollmentRepo
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.server.jdbc.repository.CourseVersionRepo
import kornell.core.entity.ContentSpec._

/**
 * Simple Event Processing
 */
object EnrollmentSEP {

  def onProgress(enrollmentUUID: String) = {
    EnrollmentRepo(enrollmentUUID).updateProgress
  }
    
  def onAssessment(enrollmentUUID: String) = 
    EnrollmentRepo(enrollmentUUID).updateAssessment
    

}
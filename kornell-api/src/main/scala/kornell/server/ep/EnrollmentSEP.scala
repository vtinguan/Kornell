package kornell.server.ep

import kornell.server.jdbc.repository.EnrollmentRepo
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.server.jdbc.repository.CourseVersionRepo
import kornell.core.entity.ContentSpec._
import javax.enterprise.context.ApplicationScoped
import kornell.server.jdbc.repository.EnrollmentRepo
import javax.inject.Inject

/**
 * Simple Event Processing
 */
@ApplicationScoped
class EnrollmentSEP @Inject() (enrollmentRepo:EnrollmentRepo) {

  def this() = this(null)
  
  def onProgress(enrollmentUUID: String) = 
    enrollmentRepo.updateProgress(enrollmentUUID)
    
  def onAssessment(enrollmentUUID: String) = 
    enrollmentRepo.updateAssessment(enrollmentUUID)
    

}
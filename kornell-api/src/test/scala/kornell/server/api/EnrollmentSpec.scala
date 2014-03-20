package kornell.server.api

import java.util.ArrayList
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import kornell.core.entity.EnrollmentState
import kornell.core.to.EnrollmentRequestTO
import kornell.core.to.RegistrationRequestTO
import kornell.server.repository.Entities
import kornell.server.repository.TOs
import kornell.server.repository.TOs
import kornell.server.test.UnitSpec
import scala.collection.JavaConverters._
import kornell.server.repository.service.RegistrationEnrollmentService
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.CourseClassesRepo

@RunWith(classOf[JUnitRunner])
class EnrollmentSpec extends UnitSpec {
  val userResource = new UserResource
  val institution = Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false)
  
  
  
  "The platformAdmin" should "be able to register and enroll one student" in {
    
		val course = Entities.newCourse(randUUID, randStr, randStr, randStr, randStr)
		val courseVersion = Entities.newCourseVersion(randUUID, randStr, course.getUUID, randStr, null, randStr)
		val courseClass = Entities.newCourseClass(randUUID, randStr, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, 1000)
		val dean = Entities.newPerson(randUUID, randStr, null, randStr, null, null, null, null, null, null, null, null, null, null, null, null, null)
  
    val fullName = randName
    val email = randEmail
    //CoursesRepo.c
    CourseClassesRepo.create(courseClass)
    
    val enrollments = new ArrayList[EnrollmentRequestTO]; 
    enrollments.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, email, null))
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(enrollments)    
    RegistrationEnrollmentService.deanRequestEnrollments(enrollmentRequestsTO, dean)
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    
    assert(enrollmentsCreated.getEnrollments.size == enrollments.size && { 
      enrollmentsCreated.getEnrollments.asScala exists(e => e.getPerson.getFullName.equals(fullName))
    })
  }
  
  
  "A registration/enrollment" should "be created only once if the email is the same" in {
    
		val course = Entities.newCourse(randUUID, randStr, randStr, randStr, randStr)
		val courseVersion = Entities.newCourseVersion(randUUID, randStr, course.getUUID, randStr, null, randStr)
		val courseClass = Entities.newCourseClass(randUUID, randStr, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, 1000)
		val dean = Entities.newPerson(randUUID, randStr, null, randStr, null, null, null, null, null, null, null, null, null, null, null, null, null)
		
    val fullName = randName
    val email = randEmail
    //CoursesRepo.c
    CourseClassesRepo.create(courseClass)
    
    val enrollments = new ArrayList[EnrollmentRequestTO]; 
    enrollments.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, email, null))
    enrollments.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, email, null))
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(enrollments)    
    RegistrationEnrollmentService.deanRequestEnrollments(enrollmentRequestsTO, dean)
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)

    assert(enrollmentsCreated.getEnrollments.size == 1)
  }
  
  
  "A registration/enrollment" should "be created only once if the cpf is the same" in {
    
		val course = Entities.newCourse(randUUID, randStr, randStr, randStr, randStr)
		val courseVersion = Entities.newCourseVersion(randUUID, randStr, course.getUUID, randStr, null, randStr)
		val courseClass = Entities.newCourseClass(randUUID, randStr, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, 1000)
		val dean = Entities.newPerson(randUUID, randStr, null, randStr, null, null, null, null, null, null, null, null, null, null, null, null, null)
		
    val fullName = randName
    val cpf = randStr
    //CoursesRepo.c
    CourseClassesRepo.create(courseClass)
    
    val enrollments = new ArrayList[EnrollmentRequestTO]; 
    enrollments.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, null, cpf))
    enrollments.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, null, cpf))
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(enrollments)    
    RegistrationEnrollmentService.deanRequestEnrollments(enrollmentRequestsTO, dean)
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    
    assert(enrollmentsCreated.getEnrollments.size == 1)
  }
  
}
package kornell.server.api

import java.util.ArrayList
import java.util.Date
import scala.collection.JavaConverters.asScalaBufferConverter
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import kornell.core.entity.CourseClass
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.core.entity.RoleType
import kornell.core.to.EnrollmentRequestTO
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.server.jdbc.repository.CoursesRepo
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.repository.Entities
import kornell.server.repository.TOs
import kornell.server.test.UnitSpec
import org.scalatest.junit.JUnitRunner
import kornell.server.jdbc.repository.CourseClassesRepo


@RunWith(classOf[JUnitRunner])
class EnrollmentsSpec extends UnitSpec with BeforeAndAfter{
  val userResource = new UserResource
  val courseClassesResource = new CourseClassesResource
  val enrollmentsResource = new EnrollmentsResource
  val institution = InstitutionsRepo.create(Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false))
	val course = CoursesRepo.create(Entities.newCourse(randUUID, randStr, randStr, randStr, randStr))
	val courseVersion = CourseVersionsRepo.create(Entities.newCourseVersion(randUUID, randStr, course.getUUID, randUUID, new Date, randStr))
	val className = randStr
	val classUUID = randUUID
	val courseClass = Entities.newCourseClass(classUUID, className, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, 23451)
	val courseClass2 = Entities.newCourseClass(randUUID, randStr, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, 23451)
	val courseClass3 = Entities.newCourseClass(randUUID, randStr, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, 23451)
	val fullName = randName
  val email = randEmail
  val cpf = randStr
  val platformAdminCPF = randStr
  val institutionAdminCPF = randStr
  val notAnAdminCPF = randStr
  val mockHttpServletResponse = new MockHttpServletResponse(0, "")
  
  
  val platformAdmin = {
    val platformAdmin = PeopleRepo.createPersonCPF(platformAdminCPF, randName)
    PersonRepo(platformAdmin.getUUID).setPassword(platformAdmin.getCPF, platformAdmin.getCPF).registerOn(institution.getUUID)
    sql"""
    	insert into Role (uuid, username, role, institution_uuid, course_class_uuid)
    	values (${randUUID}, ${platformAdmin.getCPF}, 
    	${RoleType.platformAdmin.toString}, 
    	${null}, 
    	${null} )
	    """.executeUpdate
	  platformAdmin
  }
  
  val platformAdminSecurityContext = new MockSecurityContext(platformAdmin.getCPF)
  
  val institutionAdmin = {
    val institutionAdmin = PeopleRepo.createPersonCPF(institutionAdminCPF, randName)
    PersonRepo(institutionAdmin.getUUID).setPassword(institutionAdmin.getCPF, institutionAdmin.getCPF).registerOn(institution.getUUID)
    
    sql"""
    	insert into Role (uuid, username, role, institution_uuid, course_class_uuid)
    	values (${randUUID}, ${institutionAdmin.getCPF}, 
    	${RoleType.institutionAdmin.toString}, 
    	${institution.getUUID}, 
    	${null} )
	    """.executeUpdate
	  institutionAdmin
  }
  
  val institutionAdminSecurityContext = new MockSecurityContext(institutionAdmin.getCPF)
  
  val notAnAdmin = {
    val notAnAdmin = PeopleRepo.createPersonCPF(notAnAdminCPF, randName)
    PersonRepo(notAnAdmin.getUUID).setPassword(notAnAdmin.getCPF, notAnAdmin.getCPF).registerOn(institution.getUUID)
	  notAnAdmin
  }
  
  val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(randUUID, null, courseClass.getUUID, notAnAdmin.getUUID, null, "", EnrollmentState.requested))
  val enrollmentResource = new EnrollmentResource(enrollment.getUUID)
  
  val notAnAdminSecurityContext = new MockSecurityContext(notAnAdmin.getCPF)
   
  
  
  before {
    mockHttpServletResponse.sendError(0, "")
  }

  after {
  }
  
  "The platformAdmin" should "be able to create a class" in {
    val courseClassNew = courseClassesResource.create(platformAdminSecurityContext, mockHttpServletResponse, courseClass).asInstanceOf[CourseClass]
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 1 && mockHttpServletResponse.getStatus == 0 && courseClassNew != null && courseClassNew.getCourseVersionUUID == courseVersion.getUUID)
  } 
  
  "The platformAdmin" should "not be able to create a class with the same uuid" in {
    courseClass.setName(randStr)
    courseClassesResource.create(platformAdminSecurityContext, mockHttpServletResponse, courseClass)
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 1 && mockHttpServletResponse.getStatus != 0)
  }
  
  "The platformAdmin" should "not be able to create a class with the same name" in {
    courseClass.setUUID(randUUID)
    courseClass.setName(className)
    courseClassesResource.create(platformAdminSecurityContext, mockHttpServletResponse, courseClass)
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 1 && mockHttpServletResponse.getStatus != 0)
  }
  
  "The institutionAdmin" should "be able to create a class" in {
    val courseClass2New = courseClassesResource.create(institutionAdminSecurityContext, mockHttpServletResponse, courseClass2).asInstanceOf[CourseClass]
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 2 && mockHttpServletResponse.getStatus == 0 && courseClass2New != null && courseClass2New.getCourseVersionUUID == courseVersion.getUUID)
  }
  
  "A user that's not a platform or institutionAdmin" should "not be able to create a class" in {
    courseClassesResource.create(notAnAdminSecurityContext, mockHttpServletResponse, courseClass3)
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 2 && mockHttpServletResponse.getStatus != 0)
  }
  
  "The platformAdmin" should "be able to register and enroll one participant with the email" in {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, email, null))
    enrollmentsResource.putEnrollments(platformAdminSecurityContext, mockHttpServletResponse, enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == enrollmentRequestsTO.getEnrollmentRequests.size && { 
      enrollmentsCreated.getEnrollments.asScala exists(e => e.getPerson.getFullName.equals(fullName))
    })
  }  
  
  "The platformAdmin" should "be able to register and enroll one participant with the cpf" in {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, null, cpf))
    enrollmentsResource.putEnrollments(platformAdminSecurityContext, mockHttpServletResponse, enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == 2)
  }
  
  "The platformAdmin" should "not be able to register participants with duplicate emails or cpfs" in {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, email, null))
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, fullName, null, cpf))
    enrollmentsResource.putEnrollments(platformAdminSecurityContext, mockHttpServletResponse, enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == 2)
  }
  
  "The institutionAdmin" should "be able to register and enroll one participant with the email" in {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, "institutionAdmin"+fullName, "institutionAdmin"+email, null))
    enrollmentsResource.putEnrollments(institutionAdminSecurityContext, mockHttpServletResponse, enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == 3 && { 
      enrollmentsCreated.getEnrollments.asScala exists(e => e.getPerson.getFullName.equals("institutionAdmin"+fullName))
    })
  }  
  
  "A user that's not a platform or institutionAdmin" should "not be able to register and enroll one participant" in {
    val enrollmentRequestsTO = TOs.newEnrollmentRequestsTO(new ArrayList[EnrollmentRequestTO])    
    enrollmentRequestsTO.getEnrollmentRequests.add(TOs.newEnrollmentRequestTO(institution.getUUID, courseClass.getUUID, "notAnAdmin"+fullName, "notAnAdmin"+email, null))
    enrollmentsResource.putEnrollments(notAnAdminSecurityContext, mockHttpServletResponse, enrollmentRequestsTO)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == 3 && !{ 
      enrollmentsCreated.getEnrollments.asScala exists(e => e.getPerson.getFullName.equals("notAnAdmin"+fullName))
    	} && mockHttpServletResponse.getStatus != 0)
  }  
  
  "A user" should "be able to request enrollment to a class" in {
    val enrollment = Entities.newEnrollment(randUUID, null, courseClass.getUUID, notAnAdmin.getUUID, null, null, EnrollmentState.requested)
    enrollmentsResource.create(notAnAdminSecurityContext, enrollment)
    
    val enrollmentsCreated = EnrollmentsRepo.byCourseClass(courseClass.getUUID)
    assert(enrollmentsCreated.getEnrollments.size == 4 && { 
      enrollmentsCreated.getEnrollments.asScala exists(e => e.getPerson.getFullName.equals(notAnAdmin.getFullName))
    	})
  }  
  
  "The platformAdmin" should "be able to update an enrollment" in {
    val x = "NEW NOTES"
    enrollment.setNotes(x)
    val newEnrollment = enrollmentResource.update(platformAdminSecurityContext, mockHttpServletResponse, enrollment).asInstanceOf[Enrollment]
    assert(x.equals(newEnrollment.getNotes))
  }
  
  "The institutionAdmin" should "be able to update an enrollment" in {
    val x = "NEW NOTES2"
    enrollment.setNotes(x)
    val newEnrollment = enrollmentResource.update(institutionAdminSecurityContext, mockHttpServletResponse, enrollment).asInstanceOf[Enrollment]
    assert(x.equals(newEnrollment.getNotes))
  }
  
  "A user" should "be able to update his own enrollment" in {
    val x = "NEW NOTES3"
    enrollment.setNotes(x)
    val newEnrollment = enrollmentResource.update(notAnAdminSecurityContext, mockHttpServletResponse, enrollment).asInstanceOf[Enrollment]
    assert(x.equals(newEnrollment.getNotes))
  }
  
  "A user" should "not be able to update an enrollment that doesn't belong to him" in {
    val enrollment = EnrollmentsRepo.create(Entities.newEnrollment(randUUID, null, courseClass.getUUID, institutionAdmin.getUUID, null, "", EnrollmentState.requested))
    val x = "NEW NOTES4"
    enrollment.setNotes(x)
    enrollmentResource.update(notAnAdminSecurityContext, mockHttpServletResponse, enrollment)
    assert(mockHttpServletResponse.getStatus != 0)
  } 
  
  //TODO find a better way to do this, maybe use another database
  //either way, seeing this made me see how many constraints are missing on the database
  "This test" should "cleanup its own mess" in {
    sql""" delete from Enrollment where person_uuid in (select uuid from Person where email like '%[_test_]%' or cpf like '%[_test_]%') """.executeUpdate
    sql""" delete from Role where username in (select username from Password where person_uuid in (select uuid from Person where email like '%[_test_]%' or cpf like '%[_test_]%')); """.executeUpdate
    sql""" delete from Password where person_uuid in (select uuid from Person where email like '%[_test_]%' or cpf like '%[_test_]%'); """.executeUpdate
    sql""" delete from Person where email like '%[_test_]%' or cpf like '%[_test_]%'; """.executeUpdate
    sql""" delete from CourseClass where uuid like '[_test_]%'; """.executeUpdate
    sql""" delete from CourseVersion where uuid like '[_test_]%'; """.executeUpdate
    sql""" delete from Course where uuid like '[_test_]%'; """.executeUpdate
    sql""" delete from Institution where uuid like '[_test_]%' limit 1000; """.executeUpdate
    sql""" delete from Registration where person_uuid not in (select uuid from Person); """.executeUpdate
    sql""" delete from EnrollmentStateChanged where person_uuid not in (select uuid from Person); """.executeUpdate
  } 
  
}
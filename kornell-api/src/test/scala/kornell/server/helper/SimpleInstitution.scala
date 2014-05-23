package kornell.server.helper

import org.scalatest.Suite
import org.scalatest.SuiteMixin
import javax.ws.rs.Path
import javax.ws.rs.Produces
import kornell.core.entity.Institution
import kornell.core.entity.Course
import kornell.core.entity.CourseClass
import kornell.core.entity.CourseVersion
import kornell.core.entity.Enrollment
import kornell.core.entity.Person
import kornell.server.api.CourseClassesResource
import kornell.server.api.EnrollmentResource
import kornell.server.api.EnrollmentsResource
import kornell.server.helper.MockHttpServletResponse
import kornell.server.helper.MockSecurityContext
import kornell.server.api.UserResource
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.repository.Entities
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.CoursesRepo
import kornell.core.entity.RoleType
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.core.entity.EnrollmentState
import java.util.Date
import org.scalatest.BeforeAndAfter

trait SimpleInstitution extends SuiteMixin with Generator with BeforeAndAfter{ this: Suite =>

  val userResource = new UserResource
  val courseClassesResource = new CourseClassesResource
  val enrollmentsResource = new EnrollmentsResource
	val className = randStr
	val classUUID = randUUID
	val fullName = randName
  val email = randEmail
  val cpf = randStr
  val platformAdminCPF = randStr
  val institutionAdminCPF = randStr
  val notAnAdminCPF = randStr
  
  var mockHttpServletResponse: MockHttpServletResponse = null
  
	var course: Course = null
	var courseVersion: CourseVersion = null
	var courseClass: CourseClass = null
	var courseClass2: CourseClass = null
	var courseClass3: CourseClass = null
  
  var institution: Institution = null
  
  var platformAdmin: Person = null
  var platformAdminSecurityContext: MockSecurityContext = null
  var institutionAdmin: Person = null
  var institutionAdminSecurityContext: MockSecurityContext = null
  var notAnAdmin: Person = null
  var notAnAdminSecurityContext: MockSecurityContext = null
  
  
  abstract override def withFixture(test: NoArgTest) = {
    
    institution = InstitutionsRepo.create(Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false, null))
	  course = CoursesRepo.create(Entities.newCourse(randUUID, randStr, randStr, randStr, randStr))
	  courseVersion = CourseVersionsRepo.create(Entities.newCourseVersion(randUUID, randStr, course.getUUID, randUUID, new Date, randStr, "KNL"))
	  courseClass = Entities.newCourseClass(classUUID, className, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, 23451)
	  courseClass2 = Entities.newCourseClass(randUUID, randStr, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, 23451)
	  courseClass3 = Entities.newCourseClass(randUUID, randStr, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, 23451)
    
	  platformAdmin = {
	    val platformAdmin = PeopleRepo.createPersonCPF(platformAdminCPF, randName)
	    PersonRepo(platformAdmin.getUUID).setPassword(platformAdmin.getCPF, platformAdmin.getCPF).registerOn(institution.getUUID)
	    sql"""
	    	insert into Role (uuid, username, role, institution_uuid, course_class_uuid)
	    	values (${randUUID}, ${platformAdmin.getCPF}, 
	    	${RoleType.platformAdmin.toString}, 
	    	${null}, 
	    	${null})
		    """.executeUpdate
		  platformAdmin
	  }
	  
	  platformAdminSecurityContext = new MockSecurityContext(platformAdmin.getCPF)
	  
	  institutionAdmin = {
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
	  
	  institutionAdminSecurityContext = new MockSecurityContext(institutionAdmin.getCPF)
	  
	  notAnAdmin = {
	    val notAnAdmin = PeopleRepo.createPersonCPF(notAnAdminCPF, randName)
	    PersonRepo(notAnAdmin.getUUID).setPassword(notAnAdmin.getCPF, notAnAdmin.getCPF).registerOn(institution.getUUID)
		  notAnAdmin
	  }
	  
	  notAnAdminSecurityContext = new MockSecurityContext(notAnAdmin.getCPF)
	  
	  mockHttpServletResponse = new MockHttpServletResponse(0, null)
    
    try super.withFixture(test) // To be stackable, must call super.withFixture
    
	  //TODO find a better way to do this, maybe use another database
	  //either way, seeing this made me see how many constraints are missing on the database
    finally {
	    sql""" delete from Enrollment where person_uuid in (select uuid from Person where email like '%[_test_]%' or cpf like '%[_test_]%'); """.executeUpdate
	    sql""" delete from Role where username in (select username from Password where person_uuid in (select uuid from Person where email like '%[_test_]%' or cpf like '%[_test_]%')); """.executeUpdate
	    sql""" delete from Password where person_uuid in (select uuid from Person where email like '%[_test_]%' or cpf like '%[_test_]%'); """.executeUpdate
	    sql""" delete from Person where email like '%[_test_]%' or cpf like '%[_test_]%'; """.executeUpdate
	    sql""" delete from CourseClass where uuid like '[_test_]%'; """.executeUpdate
	    sql""" delete from CourseVersion where uuid like '[_test_]%'; """.executeUpdate
	    sql""" delete from Course where uuid like '[_test_]%'; """.executeUpdate
	    sql""" delete from Institution where uuid like '[_test_]%'; """.executeUpdate
	    sql""" delete from Registration where person_uuid not in (select uuid from Person); """.executeUpdate
	    sql""" delete from EnrollmentStateChanged where person_uuid not in (select uuid from Person); """.executeUpdate
    }
  }
}
package kornell.server.helper

import java.util.Date

import org.scalatest.BeforeAndAfter
import org.scalatest.Suite
import org.scalatest.SuiteMixin

import kornell.core.entity.Course
import kornell.core.entity.CourseClass
import kornell.core.entity.CourseClassState
import kornell.core.entity.CourseVersion
import kornell.core.entity.Institution
import kornell.core.entity.Person
import kornell.core.entity.RoleType
import kornell.server.api.CourseClassesResource
import kornell.server.api.EnrollmentsResource
import kornell.server.api.UserResource
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.server.jdbc.repository.CoursesRepo
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.repository.Entities

trait SimpleInstitution extends SuiteMixin with Generator with BeforeAndAfter{ this: Suite =>
	
  val userResource = UserResource()
  val courseClassesResource =  CourseClassesResource()
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
    
    institution = InstitutionsRepo.create(Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, randURL, false, null, ""))
	  course = CoursesRepo.create(Entities.newCourse(randUUID, randStr, randStr, randStr, randStr))
	  courseVersion = CourseVersionsRepo.create(Entities.newCourseVersion(randUUID, randStr, course.getUUID, randUUID, new Date, randStr, "KNL", false))
	  courseClass = Entities.newCourseClass(classUUID, className, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, 23451, new Date(), null, CourseClassState.active, false)
	  courseClass2 = Entities.newCourseClass(randUUID, randStr, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, 23451, new Date(), null, CourseClassState.active, false)
	  courseClass3 = Entities.newCourseClass(randUUID, randStr, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, 23451, new Date(), null, CourseClassState.active, false)
    
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
    
	  
  }
}

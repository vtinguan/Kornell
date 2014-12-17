package kornell.server.api

import org.junit.runner.RunWith
import kornell.core.entity.CourseClass
import kornell.server.helper.SimpleInstitution
import kornell.server.test.UnitSpec
import org.scalatest.junit.JUnitRunner
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.server.helper.GenPlatformAdmin
import kornell.server.helper.GenCourseClass
import kornell.server.helper.GenInstitutionAdmin
import kornell.core.util.StringUtils
import kornell.server.repository.Entities
import kornell.core.entity.RegistrationEnrollmentType
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
 

@RunWith(classOf[JUnitRunner])
class TODO  {/* CourseClassesSpec extends UnitSpec 
	with GenPlatformAdmin
	with GenInstitutionAdmin
	with GenCourseClass { 
 
  "The platformAdmin" should "be able to create a class" in asPlatformAdmin {
  	newCourseClassEmail.getUUID.size should be > 0
  }
  
  "The institutionAdmin" should "be able to create a class" in asInstitutionAdmin {
  	newCourseClassEmail.getUUID.size should be > 0
  }
  
  "A person" should "not be able to create a class" in asPerson {
  	try {
  		newCourseClassEmail
  	} catch {
  	  case ise:IllegalStateException => assert(true)
  	  case default:Throwable =>fail()
  	}
  }
  
  "The platformAdmin" should "not be able to create a class with the same uuid" in asPlatformAdmin {
    val courseClass = newCourseClassEmail
    try {
      val ccr:CourseClassesResource = ???
    ccr.create(Entities.newCourseClass(uuid = courseClass.getUUID,
        courseVersionUUID = courseVersionUUID,
        institutionUUID = institutionUUID,
        registrationEnrollmentType = RegistrationEnrollmentType.email))
    } catch {
      case jdbc:MySQLIntegrityConstraintViolationException => assert(jdbc.getMessage.contains("PRIMARY"))
      case default:Throwable => fail()
    }
  }
  
  "The platformAdmin" should "not be able to create a class with the same name and courseVersion" in asPlatformAdmin {
    //Create valid course
          val ccr:CourseClassesResource = ???

    val courseClass = ccr.create(Entities.newCourseClass(name = randName,
        courseVersionUUID = courseVersionUUID,
        institutionUUID = institutionUUID,
        registrationEnrollmentType = RegistrationEnrollmentType.email))
        
    try {
    	ccr.create(Entities.newCourseClass(name = courseClass.getName,
			courseVersionUUID = courseClass.getCourseVersionUUID(),
			institutionUUID = institutionUUID,
			registrationEnrollmentType = RegistrationEnrollmentType.email))
    } catch {
      case iae:IllegalArgumentException => assert(iae.getMessage.contains(courseClass.getName))
      case default:Throwable => fail()
    }
  }
*/  
}

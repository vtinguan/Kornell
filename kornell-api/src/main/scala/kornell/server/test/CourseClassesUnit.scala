package kornell.server.test

import org.junit.runner.RunWith
import org.jboss.arquillian.junit.Arquillian
 

@RunWith(classOf[Arquillian])
class  CourseClassesSuite  { 
 /*
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

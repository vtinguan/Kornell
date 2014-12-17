package kornell.server.api

import org.junit.runner.RunWith
import kornell.server.helper.GenInstitutionAdmin
import kornell.server.test.UnitSpec
import org.scalatest.junit.JUnitRunner
import kornell.server.helper.GenPlatformAdmin
import kornell.server.repository.Entities
import kornell.server.util.RequirementNotMet

@RunWith(classOf[JUnitRunner])
class CourseSpec  {/*extends UnitSpec 
    with GenPlatformAdmin
    with GenInstitutionAdmin{
 
  val coursesResource:CoursesResource = ???
  
  "The platformAdmin" should "be able to create and get a course" in asPlatformAdmin {
    val courseCode = randStr(5)
    val course = coursesResource.create(Entities.newCourse(randUUID, courseCode, null, null, null))
    
    assert(courseCode == coursesResource.getCourse(course.getUUID).get.getCode)
  }
  
  "The institutionAdmin" should "be able to create and get a course" in asInstitutionAdmin {
    val courseCode = randStr(5)
    val course = coursesResource.create(Entities.newCourse(randUUID, courseCode, null, null, null))
    
    assert(courseCode == coursesResource.getCourse(course.getUUID).get.getCode)
  }
  
  "A person" should "not be able to create a course" in asPerson {
    val courseCode = randStr(5)
    try {
        val course = coursesResource.create(Entities.newCourse(randUUID, courseCode, null, null, null))
        throw new Throwable
    } catch {
      case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
      case default:Throwable => fail() 
    }
  }
  
  "A person" should "not be able to get a course" in asInstitutionAdmin {
    val courseCode = randStr(5)
    val course = coursesResource.create(Entities.newCourse(randUUID, courseCode, null, null, null))
    
    asPerson {
        try {
            assert(courseCode == coursesResource.getCourse(course.getUUID).get.getCode)
            throw new Throwable
        } catch {
            case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
            case default:Throwable => fail() 
        }
    }
  }

  "The platformAdmin" should "be able to update a course" in asPlatformAdmin {
    val courseCode = randStr(5)
    val course = coursesResource.create(Entities.newCourse(randUUID, courseCode, null, null, null))
    course.setCode("test update")
    
    val updatedCourse = coursesResource.getCourse(course.getUUID).update(course)
    
    assert("test update" == coursesResource.getCourse(course.getUUID).get.getCode)
  }
  
  "The institutionAdmin" should "be able to update a course" in asInstitutionAdmin {
    val courseCode = randStr(5)
    val course = coursesResource.create(Entities.newCourse(randUUID, courseCode, null, null, null))
    course.setCode("test update1")
    
    val updatedCourse = coursesResource.getCourse(course.getUUID).update(course)
    
    assert("test update1" == coursesResource.getCourse(course.getUUID).get.getCode)
  }
  
  "A person" should "not be able to update a course" in asInstitutionAdmin {
    val courseCode = randStr(5)
    val course = coursesResource.create(Entities.newCourse(randUUID, courseCode, null, null, null))
    course.setCode("test update1")
    asPerson {
      try {
          val updatedCourse = coursesResource.getCourse(course.getUUID).update(course)
          throw new Throwable
      } catch {
        case ise:IllegalStateException => assert(ise.getCause.eq(RequirementNotMet))
            case default:Throwable => fail() 
      }
    }
  }
  
  */
}


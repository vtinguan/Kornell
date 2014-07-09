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
 

@RunWith(classOf[JUnitRunner])
class CourseClassesSpec extends UnitSpec 
	with GenPlatformAdmin
	with GenInstitutionAdmin
	with GenCourseClass { 
  
  "The platformAdmin" should "be able to create a class" in asPlatformAdmin {
  	newCourseClass.getUUID.size should be > 0
  } 
  
  //TODO: "The platformAdmin" should "not be able to create a class with the same uuid" in asPlatformAdmin
  
  //TODO: "The platformAdmin" should "not be able to create a class with the same name" in  asPlatformAdmin

  
  // "The institutionAdmin" should "be able to create a class" 
  
  //TODO: "A user that's not a platform or institutionAdmin" should "not be able to create a class" 
  
}
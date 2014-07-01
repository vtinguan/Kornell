package kornell.server.api

import org.junit.runner.RunWith
import kornell.core.entity.CourseClass
import kornell.server.helper.SimpleInstitution
import kornell.server.test.UnitSpec
import org.scalatest.junit.JUnitRunner
import kornell.server.jdbc.repository.CourseClassesRepo


@RunWith(classOf[JUnitRunner])
class CourseClassesSpec extends UnitSpec with SimpleInstitution {
  
  "The platformAdmin" should "be able to create a class" in {
    val courseClassNew = courseClassesResource.create(platformAdminSecurityContext, courseClass)
    assert(courseClassNew.isPassed)
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 1)
    assert(mockHttpServletResponse.getStatus == 0)
    assert(courseClassNew != null)
    assert(courseClassNew.asInstanceOf[CourseClass].getCourseVersionUUID == courseVersion.getUUID)
  } 
  
  "The platformAdmin" should "not be able to create a class with the same uuid" in {
    assert(courseClassesResource.create(platformAdminSecurityContext, courseClass).isPassed)
    courseClass.setName(randStr)
    courseClassesResource.create(platformAdminSecurityContext, courseClass)
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 1)
    assert(mockHttpServletResponse.getStatus != 0)
  }
  
  "The platformAdmin" should "not be able to create a class with the same name" in {
    courseClassesResource.create(platformAdminSecurityContext, courseClass)
    courseClass.setUUID(randUUID)
    courseClass.setName(className)
    courseClassesResource.create(platformAdminSecurityContext, courseClass)
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 1)
    assert(mockHttpServletResponse.getStatus != 0)
  }
  
  "The institutionAdmin" should "be able to create a class" in {
    val courseClassNew = courseClassesResource.create(institutionAdminSecurityContext, courseClass).asInstanceOf[CourseClass]
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 1)
    assert(mockHttpServletResponse.getStatus == 0)
    assert(courseClassNew != null)
    assert(courseClassNew.getCourseVersionUUID == courseVersion.getUUID)
  }
  
  "A user that's not a platform or institutionAdmin" should "not be able to create a class" in {
    courseClassesResource.create(notAnAdminSecurityContext, courseClass3)
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 0)
    assert(mockHttpServletResponse.getStatus != 0)
  }
  
}
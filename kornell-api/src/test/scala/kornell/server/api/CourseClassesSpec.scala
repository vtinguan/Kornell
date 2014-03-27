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
import kornell.server.helper.SimpleInstitution


@RunWith(classOf[JUnitRunner])
class CourseClassesSpec extends UnitSpec with SimpleInstitution {
  
  "The platformAdmin" should "be able to create a class" in {
    val courseClassNew = courseClassesResource.create(platformAdminSecurityContext, mockHttpServletResponse, courseClass).asInstanceOf[CourseClass]
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 1)
    assert(mockHttpServletResponse.getStatus == 0)
    assert(courseClassNew != null)
    assert(courseClassNew.getCourseVersionUUID == courseVersion.getUUID)
  } 
  
  "The platformAdmin" should "not be able to create a class with the same uuid" in {
    courseClassesResource.create(platformAdminSecurityContext, mockHttpServletResponse, courseClass)
    courseClass.setName(randStr)
    courseClassesResource.create(platformAdminSecurityContext, mockHttpServletResponse, courseClass)
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 1)
    assert(mockHttpServletResponse.getStatus != 0)
  }
  
  "The platformAdmin" should "not be able to create a class with the same name" in {
    courseClassesResource.create(platformAdminSecurityContext, mockHttpServletResponse, courseClass)
    courseClass.setUUID(randUUID)
    courseClass.setName(className)
    courseClassesResource.create(platformAdminSecurityContext, mockHttpServletResponse, courseClass)
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 1)
    assert(mockHttpServletResponse.getStatus != 0)
  }
  
  "The institutionAdmin" should "be able to create a class" in {
    val courseClassNew = courseClassesResource.create(institutionAdminSecurityContext, mockHttpServletResponse, courseClass).asInstanceOf[CourseClass]
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 1)
    assert(mockHttpServletResponse.getStatus == 0)
    assert(courseClassNew != null)
    assert(courseClassNew.getCourseVersionUUID == courseVersion.getUUID)
  }
  
  "A user that's not a platform or institutionAdmin" should "not be able to create a class" in {
    courseClassesResource.create(notAnAdminSecurityContext, mockHttpServletResponse, courseClass3)
    assert(CourseClassesRepo.byInstitution(institution.getUUID).length == 0)
    assert(mockHttpServletResponse.getStatus != 0)
  }
  
}
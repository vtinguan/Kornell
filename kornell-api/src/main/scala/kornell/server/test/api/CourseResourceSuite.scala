package kornell.server.test.api

import org.junit.runner.RunWith
import org.jboss.arquillian.junit.Arquillian
import javax.inject.Inject
import kornell.server.test.Mocks
import org.junit.Test
import kornell.server.test.KornellSuite
import kornell.server.api.CoursesResource
import kornell.server.repository.Entities
import kornell.server.util.Err
import kornell.core.entity.Course

@RunWith(classOf[Arquillian])
class CourseResourceSuite extends KornellSuite {

  @Inject
  var mocks: Mocks = _

  @Inject
  var coursesResource: CoursesResource = _

  @Test def platformAdminCanCreateAndGetACourse = runAs(mocks.platfAdm) {
    val courseCode = randStr(5)
    val course = coursesResource.create(Entities.newCourse(
      uuid = randUUID,
      code = courseCode,
      institutionUUID = mocks.itt.getUUID))
    assert(courseCode == coursesResource.getCourse(course.getUUID).get.getCode)
  }

  @Test def ittAdminShouldCreateCourse = runAs(mocks.ittAdm) {
    val courseCode = randStr(5)
    val course = coursesResource.create(Entities.newCourse(uuid = randUUID,
      code = courseCode,
      institutionUUID = mocks.itt.getUUID))
    assert(courseCode == coursesResource.getCourse(course.getUUID).get.getCode)
  }

  //TODO: That does not sound right :)  
  //TODO: FREQUENT: Expect specific err
  //TODO: FREQUENT: Entities.
  //TODO: TALK: expected =  

  @Test(expected = classOf[Err]) def studentShouldNotCreateACourse = runAs(mocks.student) {
    coursesResource.create(Entities.newCourse(uuid = randUUID,
      code = randStr(5),
      institutionUUID = mocks.itt.getUUID))
  }

  @Test(expected = classOf[Err]) def studentShoudNotGetACourse = {
    var course: Course = null
    runAs(mocks.ittAdm) {
      val courseCode = randStr(5)
      course = coursesResource.create(Entities.newCourse(
        uuid = randUUID,
        code = courseCode,
        institutionUUID = mocks.itt.getUUID))
    }
    runAs(mocks.student) {
      coursesResource.getCourse(course.getUUID).get.getCode
    }
  }

  @Test def platformAdminCanUpdateCourse = runAs(mocks.platfAdm) {
    val courseCode = randStr(5)
    val course = coursesResource.create(Entities.newCourse(
      uuid = randUUID,
      code = courseCode,
      institutionUUID = mocks.itt.getUUID))
    val code = randStr
    course.setCode(code)
    val updatedCourse = coursesResource.getCourse(course.getUUID).update(course)
    assert(code == coursesResource.getCourse(course.getUUID).get.getCode)
  }

  @Test def ittAdminCanUpdateCourse = runAs(mocks.platfAdm) {
    val courseCode = randStr(5)
    val course = coursesResource.create(Entities.newCourse(
      uuid = randUUID,
      code = courseCode,
      institutionUUID = mocks.ittAdm.getUUID))
    val code = randStr
    course.setCode(code)
    val updatedCourse = coursesResource.getCourse(course.getUUID).update(course)
    assert(code == coursesResource.getCourse(course.getUUID).get.getCode)
  }

  @Test(expected=classOf[Err]) def someoneCantUpdateCourse = {
    var course:Course = null
    runAs(mocks.ittAdm) {
      val courseCode = randStr(5)
      course = coursesResource.create(Entities.newCourse(
          uuid = randUUID, 
          code = courseCode,
          institutionUUID = mocks.itt.getUUID))
    }

    runAs(mocks.student) {
      course.setCode(randStr)      
      val updatedCourse = coursesResource.getCourse(course.getUUID).update(course)      
    }
  }

}
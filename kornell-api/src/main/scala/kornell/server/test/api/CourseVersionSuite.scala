package kornell.server.test.api

import org.junit.runner.RunWith
import org.jboss.arquillian.junit.Arquillian
import kornell.server.test.Mocks
import javax.inject.Inject
import kornell.server.api.CourseVersionsResource
import org.junit.Test
import kornell.server.test.KornellSuite
import kornell.server.util.Err
import kornell.server.util.Err
import kornell.server.repository.Entities

@RunWith(classOf[Arquillian])
class CourseVersionSuite extends KornellSuite {

  @Inject var mocks: Mocks = _
  @Inject var courseVersionsResource: CourseVersionsResource = _

  @Test def platformAdminCanGetCourseVersion = runAs(mocks.platfAdm) {
    val courseVersionUUID = mocks.courseVersion.getUUID
    val courseVersionTO = courseVersionsResource.getCourseVersion(courseVersionUUID).get
    assert(courseVersionTO.getCourseVersion.getUUID == courseVersionUUID)
  }

  @Test def institutionAdminCanGetCourseVersion = runAs(mocks.ittAdm) {
    val courseVersionUUID = mocks.courseVersion.getUUID
    val courseVersionTO = courseVersionsResource.getCourseVersion(courseVersionUUID).get
    assert(courseVersionTO.getCourseVersion.getUUID == courseVersionUUID)
  }

  //TODO: Check specific err
  @Test(expected = classOf[Err]) def someoneShouldNotGetCourseVersion = {
    val createdCourseVersionUUID = mocks.courseVersion.getUUID()
    runAs(mocks.student) {
      courseVersionsResource.getCourseVersion(createdCourseVersionUUID).get
    }
  }

  @Test(expected = classOf[Err]) def studentCanNotCreateCourseVersion = runAs(mocks.student) {
    courseVersionsResource.create(Entities.newCourseVersion(repositoryUUID = mocks.cStore.getUUID, courseUUID = mocks.course.getUUID))
  }

  @Test def platformAdminCanUpdateCourseVersion = runAs(mocks.platfAdm) {
    val name = randStr
    val updatedCourseVersion = courseVersionsResource
      .getCourseVersion(mocks.courseVersion.getUUID).update(
        Entities.newCourseVersion(uuid = mocks.courseVersion.getUUID,
          repositoryUUID = mocks.cStore.getUUID,
          courseUUID = mocks.course.getUUID, name = name))
    assert(name == updatedCourseVersion.getName)
  }

  @Test def institutionAdminCanUpdateCourseVersion = runAs(mocks.ittAdm) {
    val name = randStr
    val updatedCourseVersion = courseVersionsResource
      .getCourseVersion(mocks.course.getUUID)
      .update(Entities.newCourseVersion(uuid = mocks.courseVersion.getUUID, repositoryUUID = mocks.cStore.getUUID,
        courseUUID = mocks.course.getUUID, name = name))
    assert(name == updatedCourseVersion.getName)
  }

  @Test(expected = classOf[Err]) def someoneCantUpdateCourseVersion = runAs(mocks.student) {
    val name = randStr
    runAs(mocks.student) {
      courseVersionsResource.getCourseVersion(mocks.courseVersion.getUUID)
        .update(Entities.newCourseVersion(uuid = mocks.courseVersion.getUUID,
          repositoryUUID = mocks.cStore.getUUID, courseUUID = mocks.course.getUUID, name = name))
    }
  }
}
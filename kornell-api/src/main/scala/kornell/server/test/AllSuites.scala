package kornell.server.test

import org.junit.runner.RunWith
import org.jboss.arquillian.junit.Arquillian
import org.junit.Test
import javax.inject.Inject
import kornell.server.util.Errors
import kornell.server.util.Err
import kornell.server.util.AccessDeniedErr
import kornell.server.api.CourseClassesResource
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException
import kornell.core.entity.RegistrationEnrollmentType
import kornell.server.repository.Entities

@RunWith(classOf[Arquillian])
class CourseClassesSuite extends KornellSuite {

  @Inject var ccsr: CourseClassesResource = _

  @Inject var mocks: Mocks = _

  @Test def PlatAdmCanCreateClass = runAs(mocks.platfAdm) {
    assert(mocks.newCourseClassEmail.getUUID.size > 0)
  }

  @Test def IttAdminCanCreatClass = runAs(mocks.ittAdm) {
    assert(mocks.newCourseClassEmail.getUUID.size > 0)
  }

  @Test(expected = classOf[Err])
  def studentCanNotCreatClass = runAs(mocks.student) {
    assert(mocks.newCourseClassEmail.getUUID.size > 0)
  }

  def platAdminCantCreatClassWithSameUUID = runAs(mocks.platfAdm) {
    try {
      val courseClass = mocks.newCourseClassEmail
      ccsr.create(uuid = courseClass.getUUID,
        courseVersionUUID = mocks.courseVersion.getUUID,
        institutionUUID = mocks.itt.getUUID,
        registrationEnrollmentType = RegistrationEnrollmentType.email)
    } catch {
      case jdbc: MySQLIntegrityConstraintViolationException => assert(jdbc.getMessage.contains("PRIMARY"))
      case default: Throwable => fail()
    }
  }

  def platAdmCaNotCreateClassWithSameNameCourseVersio = runAs(mocks.platfAdm) {
    val courseClass = ccsr.create(Entities.newCourseClass(name = randName,
      courseVersionUUID = mocks.courseVersion.getUUID,
      institutionUUID = mocks.itt.getUUID,
      registrationEnrollmentType = RegistrationEnrollmentType.email))

    try {
      ccsr.create(Entities.newCourseClass(name = courseClass.getName,
        courseVersionUUID = courseClass.getCourseVersionUUID(),
        institutionUUID = mocks.itt.getUUID,
        registrationEnrollmentType = RegistrationEnrollmentType.email))
    } catch {
      case iae: IllegalArgumentException => assert(iae.getMessage.contains(courseClass.getName))
      case default: Throwable => fail()
    }
  }

}

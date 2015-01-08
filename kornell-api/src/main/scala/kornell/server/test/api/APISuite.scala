package kornell.server.test.api

import org.junit.runner.RunWith
import org.junit.runners.Suite.SuiteClasses
import org.junit.runners.Suite
import kornell.server.test.api._

@RunWith(classOf[Suite])
@SuiteClasses(Array(
		classOf[CourseResourceSuite]
		, classOf[CourseVersionResourceSuite]
		, classOf[EnrollmentsResourceSuite]
		, classOf[InstitutionResourceSuite]
		, classOf[RegistrationResourceSuite]))
class APISuite {

}
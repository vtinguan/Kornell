package kornell.server.test

import org.junit.runner.RunWith
import org.junit.runners.Suite.SuiteClasses
import org.junit.runners.Suite
import kornell.server.test.api.CourseSuite

@RunWith(classOf[Suite])
@SuiteClasses(Array(classOf[EnrollmentSuite], 
					classOf[CourseClassesSuite],
					classOf[CourseSuite]))
class AllSuites extends KornellSuite {
}
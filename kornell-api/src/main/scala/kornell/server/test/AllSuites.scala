package kornell.server.test

import org.junit.runner.RunWith
import org.junit.runners.Suite.SuiteClasses
import org.junit.runners.Suite
import kornell.server.test.api.CourseSuite
import kornell.server.test.api.CourseVersionSuite
import kornell.server.test.api.EnrollmentResourceSuite

@RunWith(classOf[Suite])
@SuiteClasses(Array(classOf[EnrollmentSuite], 
					classOf[CourseClassesSuite],
					classOf[CourseSuite],
					classOf[CourseVersionSuite],
					classOf[EnrollmentResourceSuite]))
class AllSuites extends KornellSuite {
}
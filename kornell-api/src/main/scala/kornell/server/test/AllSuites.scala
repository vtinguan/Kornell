package kornell.server.test

import org.junit.runner.RunWith
import org.junit.runners.Suite.SuiteClasses
import org.junit.runners.Suite
import kornell.server.util.ConditionalSuite
import kornell.server.test.api.APISuite

@RunWith(classOf[Suite])
@SuiteClasses(Array(classOf[EnrollmentSuite] 
					,classOf[CourseClassesSuite]
					,classOf[APISuite]
					,classOf[ConditionalSuite]					
//					,classOf[SingleCourseExSuite]
))
class AllSuites {}
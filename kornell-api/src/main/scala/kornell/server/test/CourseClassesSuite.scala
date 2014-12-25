package kornell.server.test

import org.junit.runner.RunWith
import org.jboss.arquillian.junit.Arquillian
import org.junit.Test
import javax.inject.Inject
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(classOf[Suite])
@SuiteClasses(Array(classOf[EnrollmentSuite],classOf[CourseClassesSuite])) 
class AllSuites  extends KornellSuite {
  }

package kornell.server.test

import kornell.server.test.util.Generator
import org.scalatest.junit.JUnitSuite

trait KornellSuite extends JUnitSuite
  with PrivilegeEscalation
  with Generator
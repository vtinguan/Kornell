package kornell.server.test

import org.scalatest.junit.JUnitSuite

trait KornellSuite extends JUnitSuite
  with PrivilegeEscalation
  with Generator
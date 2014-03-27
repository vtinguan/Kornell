package kornell.server.helper

import org.scalatest._
import kornell.server.repository.Entities
import javax.ws.rs.Path
import javax.ws.rs.Produces
import kornell.server.api.EnrollmentResource
import kornell.server.api.EnrollmentsResource
import kornell.server.helper.MockHttpServletResponse
import kornell.server.helper.MockSecurityContext
import kornell.server.api.UserResource
import kornell.core.util.UUID
import scala.util.Random

trait Generator { 
  def chars = Stream continually {Random.nextPrintableChar}
  def randStr(length: Int): String = chars take length mkString
  def randStr: String = "[_test_]"+randStr(42)
  def randUUID: String = "[_test_]"+UUID.randomUUID.toString.substring(8)
  def randURL() = s"[_test_]https://${randStr}"
  def randEmail = s"[_test_]${randStr(2)}@${randStr(5)}.com"
  def randName = randStr + " " + randStr
}
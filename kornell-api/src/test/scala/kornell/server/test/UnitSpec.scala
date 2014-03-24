package kornell.server.test

import org.scalatest.Matchers
import org.scalatest.Inside
import org.scalatest.OptionValues
import org.scalatest.FlatSpec
import scala.util.Random
import java.util.UUID
import kornell.server.jdbc.Migration

class UnitSpec extends FlatSpec with Matchers with OptionValues with Inside {
  
  System.setProperty("TEST_MODE", "true")
  //create database kornell_tests
  System.setProperty("JDBC_CONNECTION_STRING", "jdbc:mysql:///kornell_tests")
  //grant all on kornell_tests.* to kornell@'localhost' identified by '42kornell_tests42'; 
  System.setProperty("JDBC_USERNAME", "kornell")
  System.setProperty("JDBC_PASSWORD", "42kornell_tests42")

  Migration()
  
  def chars = Stream continually {Random.nextPrintableChar}
  def randStr(length: Int): String = chars take length mkString
  def randStr: String = "[_test_]"+randStr(42)
  def randUUID: String = "[_test_]"+UUID.randomUUID.toString.substring(8)
  def randURL() = s"[_test_]https://${randStr}"
  def randEmail = s"[_test_]${randStr(2)}@${randStr(5)}.com"
  def randName = randStr + " " + randStr
}

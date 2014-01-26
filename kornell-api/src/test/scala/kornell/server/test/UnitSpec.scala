package kornell.server.test

import org.scalatest.Matchers
import org.scalatest.Inside
import org.scalatest.OptionValues
import org.scalatest.FlatSpec
import scala.util.Random
import java.util.UUID
import kornell.server.jdbc.Migration

class UnitSpec extends FlatSpec with Matchers with OptionValues with Inside {
  //create database kornell_tests
  System.setProperty("JDBC_CONNECTION_STRING", "jdbc:mysql:///kornell_tests")
  //grant all on kornell_tests.* to kornell@'localhost' identified by '42kornell_tests42'; 
  System.setProperty("JDBC_USERNAME", "kornell")
  System.setProperty("JDBC_PASSWORD", "42kornell_tests42")

  Migration()
  
  def chars = Stream continually {Random.nextPrintableChar}
  def randStr(length: Int): String = chars take length mkString
  def randStr: String = randStr(42)
  def randName = randStr + " " + randStr
  def randUUID: String = UUID.randomUUID.toString
  def randURL() = s"https://${randStr}"
  def randEmail = s"${randStr(10)}@${randStr(5)}.com"
}

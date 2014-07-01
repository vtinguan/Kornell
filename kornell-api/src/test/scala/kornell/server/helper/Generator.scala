package kornell.server.helper

import scala.util.Random

import kornell.core.util.UUID

trait Generator { 
  def chars = Stream continually {Random.nextPrintableChar}
  def randStr(length: Int): String = chars take length mkString
  def randStr: String = "[_test_]"+randStr(42)
  def randUUID: String = "[_test_]"+UUID.randomUUID.toString.substring(8)
  def randURL() = s"[_test_]https://${randStr}"
  def randEmail = s"[_test_]${randStr(2)}@${randStr(5)}.com"
  def randName = randStr + " " + randStr
}
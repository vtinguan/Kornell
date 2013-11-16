package kornell.server.util

import scala.util.Random

object Randoms {
  def randomString = Stream
    .continually { Random.nextPrintableChar }
    .filter { _.isLetter }
    .take(10)
    .mkString
}
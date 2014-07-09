package kornell.server.helper

import scala.util.Random

object Playground extends App {
	def around[T](t : => T):T = t
	
	around({println("Uala")})
}
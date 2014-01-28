package kornell.server.dev

import kornell.core.util.UUID

object Sandbox extends App {

  val duvidoso:Option[Int] = Option(1)

  val cond1 = if (duvidoso.isDefined)
    1 + duvidoso.get
  else
    0
    
  println(cond1)
    
  val cond2 = duvidoso match {
    case Some(x) => 1 + x
    case None => 2
  }
  
  println(cond2)
  
  println( duvidoso map {_+1} )
  
    println(UUID.random)
    println(UUID.random)
    println(UUID.random)
    println(UUID.random)
    println(UUID.random)
  
}
package kornell.repository.slick.plain

import java.util.Date


object TrySlick extends App  {

  println("Hello Slick")

  Persons.create( "Fulano " + new Date)
  Persons.foreach(println(_))

}
package kornell.server.dev.util

import kornell.server.jdbc.repository.PersonRepo
import kornell.server.jdbc.repository.PeopleRepo
import scala.util.Random



object ProduceUser extends App {
  def randomString = Stream
    .continually { Random.nextPrintableChar }
    .filter { _.isLetter }
    .take(10)
    .mkString
  
  val fullName = "Fulano de Tal"
  val username = randomString
  val password = randomString
  val institution_uuid = "00a4966d-5442-4a44-9490-ef36f133a259";
    
  val p: PersonRepo = PeopleRepo.createPerson(username, fullName)
  p.setPassword(username, password) 
		  .registerOn(institution_uuid)
 
  println(" Full Name: "+fullName)
  println("  Username: "+username)
  println("  Password: "+password)
}
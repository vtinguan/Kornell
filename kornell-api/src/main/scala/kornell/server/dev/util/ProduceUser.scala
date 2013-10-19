package kornell.server.dev.util

import kornell.server.repository.jdbc.People
import kornell.server.repository.jdbc.Auth
import scala.util.Random
import scala.collection.Seq
import kornell.server.repository.jdbc.Institutions
import kornell.core.shared.data.Registration
import kornell.server.repository.jdbc.PersonRepository


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
  val course_uuid = "d9aaa03a-f225-48b9-8cc9-15495606ac46";
    
  val p: PersonRepository = People().createPerson(fullName)
  p.setPassword(username, password) 
		  .registerOn(institution_uuid)
		  .enrollOn(course_uuid, p.get().get.getUUID())
 
  println(" Full Name: "+fullName)
  println("  Username: "+username)
  println("  Password: "+password)
}
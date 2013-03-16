package kornell.dev

import kornell.repository.Beans
import kornell.repository.slick.plain.Courses
import kornell.repository.slick.plain.Persons
import kornell.repository.slick.plain.Auth
import java.util.Date

trait CleanDB extends Toolkit {
  respawnDB
}

trait BasicData {
  val fulano = Persons.create("Fulano de Tal")
}

trait AuthData extends BasicData {  
  val principal = Auth.createUser(fulano.getUUID, "fulano", "detal", List("user"))
}

trait CoursesData extends BasicData {
    val cBeatles = Courses.create(
    "The Beatles", "beatles", """
    |Learn all about the beatles and their wonderful tunes!""",
    "http://the_beatles.s3.amazonaws.com/")
    
  val eBeatles = Courses.createEnrollment(new Date,cBeatles.getUUID(),fulano.getUUID(),"0.90")
  
    
}

object SampleDataGenerator extends App
  with Beans
  with CleanDB
  with AuthData
  with CoursesData
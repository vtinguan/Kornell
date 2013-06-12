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
    val cFloyd = Courses.create(
    "Pink Floyd", "floyd", """
    |All that is now, and all that is gone, and all that's to come, and everything under the sun is in tune but the sun is eclipsed by the moon.""",
    "https://s3-sa-east-1.amazonaws.com/content-pinkfloyd/")
    
  val eBeatles = Courses.createEnrollment(new Date,cFloyd.getUUID(),fulano.getUUID(),"0.90")
  
    
}

object SampleDataGenerator extends App
  with Beans
  //with CleanDB (TODO: CleanDB is not portable as FileVisitor order is not predictable)
  with AuthData
  with CoursesData
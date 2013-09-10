package kornell.server.dev.data

import java.util.Date
import kornell.server.repository.Beans
import kornell.server.repository.jdbc.Institutions
import kornell.server.repository.slick.plain.Auth
import kornell.server.repository.slick.plain.Courses
import kornell.server.repository.slick.plain.Persons

trait CleanDB extends Toolkit{
  respawnDB 
} 

object SampleDataGenerator extends App with CleanDB {
  val persons = new PersonData
  val auth = new AuthData(persons)
  val institutions = new InstitutionsData(persons)
  val courses = new CoursesData(persons) 
}
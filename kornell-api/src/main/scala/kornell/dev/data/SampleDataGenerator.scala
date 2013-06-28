package kornell.dev.data

import java.util.Date
import kornell.repository.Beans
import kornell.repository.jdbc.Institutions
import kornell.repository.slick.plain.Auth
import kornell.repository.slick.plain.Courses
import kornell.repository.slick.plain.Persons

trait CleanDB extends Toolkit{
  respawnDB 
}

object SampleDataGenerator extends App with CleanDB{
  val persons = new PersonData
  val auth = new AuthData(persons)
  val institutions = new InstitutionsData(persons)
  val courses = new CoursesData(persons) 
}
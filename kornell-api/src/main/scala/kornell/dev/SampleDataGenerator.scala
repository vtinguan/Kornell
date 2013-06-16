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
    val cFloyd1 = Courses.create(
    "Pink Floyd1", "floyd1", """
    |All that is now, and all that is gone, and all that's to come, and everything under the sun is in tune but the sun is eclipsed by the moon.""",
    "https://s3-sa-east-1.amazonaws.com/content-pinkfloyd/")
    
    val eBeatles1 = Courses.createEnrollment(new Date,cFloyd1.getUUID(),fulano.getUUID(),"0.90")
  
	val cFloyd2 = Courses.create(
	"Pink Floyd2", "floyd2", """
	|All that is now, and all that is gone, and all that's to come, and everything under the sun is in tune but the sun is eclipsed by the moon.""",
	"https://s3-sa-east-1.amazonaws.com/content-pinkfloyd/")
    
	val eBeatles2 = Courses.createEnrollment(new Date,cFloyd2.getUUID(),fulano.getUUID(),"0.90")
  
	val cFloyd4 = Courses.create(
	"Pink Floyd4", "floyd4", """
	|All that is now, and all that is gone, and all that's to come, and everything under the sun is in tune but the sun is eclipsed by the moon.""",
	"https://s3-sa-east-1.amazonaws.com/content-pinkfloyd/")
    
	val eBeatles4 = Courses.createEnrollment(new Date,cFloyd4.getUUID(),fulano.getUUID(),"0.00")
  
	val cFloyd5 = Courses.create(
	"Pink Floyd5", "floyd5", """
	|All that is now, and all that is gone, and all that's to come, and everything under the sun is in tune but the sun is eclipsed by the moon.""",
	"https://s3-sa-east-1.amazonaws.com/content-pinkfloyd/")
    
	val eBeatles5 = Courses.createEnrollment(new Date,cFloyd5.getUUID(),fulano.getUUID(),"0.00")
  
	val cFloyd7 = Courses.create(
	"Pink Floyd7", "floyd7", """
	|All that is now, and all that is gone, and all that's to come, and everything under the sun is in tune but the sun is eclipsed by the moon.""",
	"https://s3-sa-east-1.amazonaws.com/content-pinkfloyd/")
    
	val eBeatles7 = Courses.createEnrollmentNull(new Date,cFloyd7.getUUID(),fulano.getUUID(),"0.00")
  
	val cFloyd8 = Courses.create(
	"Pink Floyd8", "floyd8", """
	|All that is now, and all that is gone, and all that's to come, and everything under the sun is in tune but the sun is eclipsed by the moon.""",
	"https://s3-sa-east-1.amazonaws.com/content-pinkfloyd/")
    
	val eBeatles8 = Courses.createEnrollmentNull(new Date,cFloyd8.getUUID(),fulano.getUUID(),"0.00")
  
	val cFloyd10 = Courses.create(
	"Pink Floyd10", "floyd10", """
	|All that is now, and all that is gone, and all that's to come, and everything under the sun is in tune but the sun is eclipsed by the moon.""",
	"https://s3-sa-east-1.amazonaws.com/content-pinkfloyd/")
    
	val eBeatles10 = Courses.createEnrollment(new Date,cFloyd10.getUUID(),fulano.getUUID(),"1.00")
  
	val cFloyd11 = Courses.create(
	"Pink Floyd11", "floyd11", """
	|All that is now, and all that is gone, and all that's to come, and everything under the sun is in tune but the sun is eclipsed by the moon.""",
	"https://s3-sa-east-1.amazonaws.com/content-pinkfloyd/")
    
	val eBeatles11 = Courses.createEnrollment(new Date,cFloyd11.getUUID(),fulano.getUUID(),"1.00")
    
}

object SampleDataGenerator extends App
  with Beans
  //with CleanDB (TODO: CleanDB is not portable as FileVisitor order is not predictable)
  with AuthData
  with CoursesData
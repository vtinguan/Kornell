package kornell.dev

import javax.ejb.Singleton
import javax.ejb.Startup
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.annotation.PostConstruct
import kornell.entity.PasswordCredential
import kornell.entity.Principal
import scala.collection.JavaConverters._
import org.jboss.security.auth.spi.Util._
import kornell.entity.Person
import kornell.entity.Course
import java.util.UUID
import kornell.entity.Enrollment
import java.util.Date

@Singleton
@Startup
class SampleDataGenerator @Inject() (val em:EntityManager){
	def this() = this(null)
	
	@PostConstruct
	def generateData = {
	  val fulano = em merge new Person("Fulano de Tal")
	  val principal = em merge new Principal(fulano,"fulano", List("user") asJava)
	  val secret = em merge new PasswordCredential(principal,hash("detal"))
	  for(i <- 1 to 10){
		 var course = new Course
		 course.uuid = UUID.randomUUID.toString
		 course.code = "SCORM SECE "+i
		 course.packageURL = "/content/SCORM2004.4.SECE.1.0.CP/"
		 course.description = 
		 """A SCORM Content Example, copy """+i+"""| 
		 	|Used not so much for learning SCORM, but to test our implementation.
		    |Not meant to be complete or anything, just seemed like a good starting point.
		 	|It is copied multiple times to test scoping of variables.
		 	|""".stripMargin
		 course = em merge course
		 if(i == 1){
		   var enrollment = new Enrollment
		   enrollment.uuid = UUID.randomUUID.toString
		   enrollment.course = course
		   enrollment.person = fulano
		   enrollment.enrolledOn = new Date
		   em merge enrollment
		 }
	  }	     
	  
	} 
	
	def hash(plain:String) = createPasswordHash("SHA-256", "BASE64", null, null, plain)
	
}
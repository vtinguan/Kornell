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
		 val course = new Course
		 course.setUuid(UUID.randomUUID.toString)
		 course.setCode("SCORM SECE "+i);
		 course.setPackageURL("/content/SCORM2004.4.SECE.1.0.CP/")
		 em merge course
	  }	     
	  
	} 
	
	def hash(plain:String) = createPasswordHash("SHA-256", "BASE64", null, null, plain)
	
}
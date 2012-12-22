package kornell.dev

import javax.ejb.Singleton
import javax.ejb.Startup
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.annotation.PostConstruct
import kornell.entity.PasswordCredential
import kornell.entity.Principal
import scala.collection.JavaConverters._
import org.jboss.security.auth.spi.Util

@Singleton
@Startup
class SampleDataGenerator {
	@Inject 
	var em:EntityManager = null;
	
	@PostConstruct
	def generateData = {
	  var fulano = em.merge(new Principal("fulano", List("user") asJava ))
	  var secret = em.merge(new PasswordCredential(fulano,hash("detal")))
	} 
	
	def hash(plain:String) = Util.createPasswordHash("SHA-256", "BASE64", null, null, plain);
	
}
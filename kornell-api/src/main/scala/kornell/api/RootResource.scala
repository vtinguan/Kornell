package kornell.api

import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.GET
import java.util.Properties

@Path("")
class RootResource {
	@Produces(Array("text/plain"))
	@GET
	def get = {
	  val version = new Properties();
	  version.load(getClass().getClassLoader().getResourceAsStream("version.properties")); 
	  s"""|Welcome to Kornell API AWS Band Tec\n
	  |Built on ${version.getProperty("built.on","development environment")}
	  |
	  """.stripMargin 
	}
}
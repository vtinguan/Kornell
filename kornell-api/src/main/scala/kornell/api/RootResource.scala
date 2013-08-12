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
	  //TODO: Say X.XX time ago
	  val properties = Option(getClass().getClassLoader().getResourceAsStream("version.properties"))
	  if(properties.isDefined){
		  version.load(properties.get);
	  }
	  s"""|Welcome to Kornell API\n
	  |CI Verification
	  |Built on ${version.getProperty("built.on","development environment")}
	  |
	  """.stripMargin 
	}
}
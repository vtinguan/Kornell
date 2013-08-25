package kornell.server.api

import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.GET
import java.util.Properties
import kornell.server.repository.jdbc.SQLInterpolation._

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
	  val two = sql"select 1+1".map {rs => rs.getInt(1)}.head
	  
	  s"""|Welcome to Kornell API\n
	  |
	  |Built on ${version.getProperty("built.on","development environment")}
	  |MySQL thinks that 1+1=$two
	  """.stripMargin
	  
	}
}
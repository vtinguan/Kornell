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
	  //TODO: Say how long ago too
	  val properties = Option(getClass().getClassLoader().getResourceAsStream("version.properties"))
	  if(properties.isDefined)
		  version.load(properties.get);	  
	  val two = try 
	  	sql"select 1+1".map {rs => rs.getInt(1)}.head
	  	catch {case e:Exception => e.getMessage}
	  
	  s"""|Welcome to Kornell API\n
	  |
	  |Built on ${version.getProperty("built.on","development environment")}
	  |According to MySQL, 1+1 is $two
	  |
	  """.stripMargin
	  
	}
}
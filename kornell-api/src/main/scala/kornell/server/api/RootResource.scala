package kornell.server.api

import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.GET
import java.util.Properties
import kornell.server.jdbc.SQL._
import kornell.server.util.Settings

@Path("")
class RootResource {
  val buildDescription = Settings.get("build.number").getOrElse("development")

  @Produces(Array("text/plain"))
  @GET
  def get = 
    s"""|Welcome to Kornell API\n  
	  |
	  |build #$buildDescription"""
    .stripMargin
}
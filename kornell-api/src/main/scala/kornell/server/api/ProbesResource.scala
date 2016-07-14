package kornell.server.api

import scala.concurrent.ExecutionContext.Implicits.global
import javax.ws.rs._
import kornell.server.util.Settings
import javax.ws.rs.core.Context
import javax.servlet.http.HttpServletRequest
import scala.collection.JavaConversions._

@Path("/probes")
@Produces(Array("text/plain"))
class ProbesResource {
  
  @Path("system")
  @GET
  def system = {
    val buf =  new StringBuilder
    buf.append("# Properties\n")
    val props = System.getProperties
    val names = props.propertyNames
    while (names.hasMoreElements){
      val name = names.nextElement.toString()
      val value = props.getProperty(name)
      buf.append(s"${name} = ${value}\n")
    }
    buf.append("\n# Environment\n")
    val env = System.getenv
    env.foreach { kv =>
      val name = kv._1
      val value = if (name.toLowerCase().contains("password"))
        "********"
      else kv._2
      buf.append(s"${name} = ${value}\n")
    }
    buf.toString
  }
  
  @Path("settings")
  @GET
  def settings = Settings
                  .values
                  .mkString("\n")

  
  @Path("headers")
  @GET
  def headers(@Context req:HttpServletRequest) = {
    val buf:StringBuilder = new StringBuilder
    val headerNames = req.getHeaderNames
    while (headerNames.hasMoreElements){
      val headerName = headerNames.nextElement
      val headerValue = req.getHeader(headerName)
      buf.append(s"$headerName = $headerValue \n")
    }
    buf.toString
  }
  
  @Path("throws")
  @GET
  def throws = throw new RuntimeException
}

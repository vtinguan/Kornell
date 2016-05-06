package kornell.server.dev

import scala.concurrent.ExecutionContext.Implicits.global
import javax.ws.rs._
import kornell.server.util.Settings
import javax.ws.rs.core.Context
import javax.servlet.http.HttpServletRequest

@Path("/probes")
@Produces(Array("text/plain"))
class ProbesResource {
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
}
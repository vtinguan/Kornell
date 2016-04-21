package kornell.server.dev

import scala.concurrent.ExecutionContext.Implicits.global
import javax.ws.rs._
import kornell.server.util.Settings

@Path("/probes")
@Produces(Array("text/plain"))
class ProbesResource {
  @Path("settings")
  @GET
  def settings = Settings
                  .values
                  .mkString("\n")
}
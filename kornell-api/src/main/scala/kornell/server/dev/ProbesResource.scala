package kornell.server.dev

import scala.concurrent.ExecutionContext.Implicits.global

import javax.ws.rs._
import kornell.server.util.Conditional._

@Path("/probes")
@Produces(Array("text/plain"))
class ProbesResource {

}
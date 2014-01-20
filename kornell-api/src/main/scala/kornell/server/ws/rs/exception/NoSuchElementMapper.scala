package kornell.server.ws.rs.exception

import javax.ws.rs.ext._
import javax.ws.rs.core._
import java.util.NoSuchElementException

@Provider
class NoSuchElementMapper extends ExceptionMapper[NoSuchElementException] {
  override def toResponse(nse: NoSuchElementException): Response = 
    Response.status(404).build()
}
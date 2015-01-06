package kornell.server.ws.rs.exception

import javax.ws.rs.ext._
import javax.ws.rs.core._
import java.util.NoSuchElementException
import java.util.logging.Logger
import java.util.logging.Level

@Provider
class NoSuchElementMapper extends ExceptionMapper[NoSuchElementException] {
  val logger: Logger = Logger.getLogger(classOf[NoSuchElementException].getName)

  override def toResponse(nse: NoSuchElementException): Response = {
    logger.log(Level.WARNING, nse.getMessage, nse)
    Response.status(404).build
  }
}
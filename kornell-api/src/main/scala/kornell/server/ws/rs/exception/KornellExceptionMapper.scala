package kornell.server.ws.rs.exception

import javax.ws.rs.ext.Provider
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.core.Response
import kornell.server.util.KornellErr
import java.util.logging.Logger
import java.util.logging.Level

@Provider
class KornellExceptionMapper extends ExceptionMapper[KornellErr] {
  val logger: Logger = Logger.getLogger(classOf[KornellExceptionMapper].getName)

  override def toResponse(ke: KornellErr): Response = {
    logger.log(Level.WARNING, ke.getMessage, ke)
    Response
      .status(500)
      .entity(ke.toJSON)
      .build()
  }
}
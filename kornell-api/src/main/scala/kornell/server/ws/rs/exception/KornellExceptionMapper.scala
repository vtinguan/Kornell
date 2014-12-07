package kornell.server.ws.rs.exception

import javax.ws.rs.ext.Provider
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.core.Response
import kornell.server.util.KornellErr

@Provider
class KornellExceptionMapper extends ExceptionMapper[KornellErr] {
  override def toResponse(ke: KornellErr): Response = 
    Response
    	.status(500)
    	.entity(ke.toJSON)
    	.build()
}
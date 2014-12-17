package kornell.server.ws.rs.exception

import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider
import java.lang.IllegalArgumentException

@Provider
class IllegalArgumentMapper extends ExceptionMapper[IllegalArgumentException] {
    override def toResponse(iae: IllegalArgumentException): Response = 
        Response.status(409).entity(iae.getMessage).build
}
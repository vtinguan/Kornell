package kornell.server.ws.rs.exception

import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider
import java.lang.IllegalArgumentException
import java.util.logging.Logger
import java.util.logging.Level

@Provider
class IllegalArgumentMapper extends ExceptionMapper[IllegalArgumentException] {
    val logger:Logger = Logger.getLogger(classOf[IllegalArgumentMapper].getName)
    
    override def toResponse(iae: IllegalArgumentException): Response = {
    	logger.log(Level.WARNING, iae.getMessage, iae)
        Response.status(409).entity(iae.getMessage).build
    }
}
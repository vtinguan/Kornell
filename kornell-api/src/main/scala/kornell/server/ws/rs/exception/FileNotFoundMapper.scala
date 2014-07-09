package kornell.server.ws.rs.exception

import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider
import java.io.FileNotFoundException
import javax.ws.rs.core.Response

@Provider
class FileNotFoundMapper extends ExceptionMapper[FileNotFoundException] {
  override def toResponse(fnfe: FileNotFoundException): Response = 
    Response.status(404).entity(fnfe.getMessage).build
}


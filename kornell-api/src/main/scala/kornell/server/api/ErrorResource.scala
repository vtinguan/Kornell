package kornell.server.api

import javax.ws.rs.Path
import java.io.FileNotFoundException
import javax.ws.rs.Produces
import javax.ws.rs.GET
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import java.util.HashMap
import kornell.core.error.ErrorFactory
import kornell.server.util.FileNotFoundErr
import kornell.server.util.Conditional._

@Path("errors")
class ErrorResource {
  
  @GET
  @Path("notFound")
  @Produces(Array("application/vnd.kornell.error+json"))
  def notFound = {
    println ("Executed!!!")
  }.requiring ( false, FileNotFoundErr("/some/path/works"))
   .get
}
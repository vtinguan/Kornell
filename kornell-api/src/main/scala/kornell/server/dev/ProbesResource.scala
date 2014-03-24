package kornell.server.dev

import javax.ws.rs._
import kornell.server.util.EmailSender
import java.util.Date

@Path("/probes")
@Produces(Array("text/plain"))
class ProbesResource {
  @GET
  @Path("sendEmail")
  def sendEmail = {
    val msg = "Email sent at " + new Date(); 
    EmailSender.sendEmailSync("Hello from Kornell",
      "cdf@craftware.com.br",
      "jfaerman@gmail.com",
      "jfaerman@gmail.com",
      msg,
      null)
    msg
  }
}
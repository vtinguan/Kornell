package kornell.server.dev

import javax.ws.rs._
import kornell.server.util.EmailSender
import java.util.Date

@Path("/probes")
@Produces(Array("text/plain"))
class ProbeResource {
  @GET
  @Path("sendEmail")
  def sendEmail = {
    EmailSender.sendEmail("Hello from Kornell",
      "cdf@craftware.com.br",
      "jfaerman@gmail.com",
      "Email sent " + new Date(),
      null)
    "Email Sent!"
  }
}
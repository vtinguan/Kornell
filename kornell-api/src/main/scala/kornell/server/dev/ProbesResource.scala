package kornell.server.dev

import javax.ws.rs._
import kornell.server.util.EmailSender
import java.util.Date
import java.io.ByteArrayInputStream
import kornell.server.repository.s3.S3
import kornell.core.util.UUID
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.util.Conditional._
import kornell.server.repository.Entities
import kornell.core.entity.ActomEntries
import kornell.server.util.RequirementNotMet

@Path("/probes")
@Produces(Array("text/plain"))
class ProbesResource {
  case class IntegerHolder(x: Int)

  @GET
  @Path("triple")
  @Produces(Array(ActomEntries.TYPE))
  def triple(@QueryParam("x") x: Int) = {
    Entities.newActomEntries("TRIPLE", "" + 3 * x, null)
  }.requiring({x > 10}, RequirementNotMet)

  @GET
  @Path("putToS3Sync")
  def putToS3Sync = {
    val bs = new ByteArrayInputStream("Some Nice Certificate Bytes ".getBytes())
    val filename = UUID.random + ".txt"

    S3.certificates.put(filename,
      bs,
      "text-plain",
      "Content-Disposition: attachment; filename=\"certificado.txt\"",
      Map("certificatedata" -> "09/01/1980", "requestedby" -> "Fulano"))

    s"See your file at: /usercontent/certificates/$filename\n"
  }

  @GET
  @Path("putToS3Async")
  def putToS3Async = Future {
    putToS3Sync
  } onSuccess {
    case s => println("send an email or something...")
  }

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
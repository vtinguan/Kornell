package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.mail.Transport
import javax.mail.MessagingException
import java.util.Properties
import javax.mail.Session
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.internet.MimeMessage
import javax.mail.internet.InternetAddress
import kornell.server.util.EmailSender

@Path("/email")
class EmailResource {

	@GET
	@Path("/welcome/{userUuid}")
    @Produces(Array("text/plain"))
	def get(@PathParam("userUuid") userUuid:String) = {
		EmailSender.sendEmail()
	}
}
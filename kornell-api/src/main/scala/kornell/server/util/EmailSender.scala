package kornell.server.util

import java.util.Properties
import javax.mail.internet.MimeMessage
import javax.mail.Session
import javax.mail.Transport
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Message
import javax.mail.internet.InternetAddress

object EmailSender {
	def sendEmail(){
	  
	  	val username: String = "strabstertest@gmail.com"
		val password: String = "strabstertest1"
 
		val props: Properties = new Properties()
		props.put("mail.smtp.host", "smtp.gmail.com")
		props.put("mail.smtp.socketFactory.port", "465")
		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory")
		props.put("mail.smtp.auth", "true")
		props.put("mail.smtp.port", "465")
        // avoid hang by setting timeout; 60 seconds
        props.put("mail.smtp.timeout", "30000")
        props.put("mail.smtp.connectiontimeout", "30000")
 
		val session: Session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			override def getPasswordAuthentication() = {
				new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
			val message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username))
			message.setRecipients(Message.RecipientType.TO, "tcfaria@gmail.com")
			message.setSubject("Direitero")
			message.setText("Belessa Kapião!")
 
			Transport.send(message)
 
			System.out.println("Done");
		 
		} catch {
		    case e: MessagingException => throw new RuntimeException(e) 
		}
	}
}
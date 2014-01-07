package kornell.server.util

import java.io.File
import java.util.Date
import java.util.Properties
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Multipart
import javax.mail.Part
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.Authenticator

object EmailSender {
  
  def sendEmail(subject: String, from: String, to: String, body: String, imgFile: File): Unit = {
    try {
      val message = new MimeMessage(getEmailSession);
      message.setFrom(new InternetAddress(from))
      message.setRecipients(Message.RecipientType.TO, to)
      message.setSentDate(new Date())
      message.setSubject(subject, "UTF-8")
      
      // creates message part
      val messageBodyPart: MimeBodyPart = new MimeBodyPart()
      messageBodyPart.setContent(body, "text/html; charset=utf-8")
      
      // creates multi-part
      val multipart: Multipart = new MimeMultipart()
      multipart.addBodyPart(messageBodyPart)

      val imagePartLogo: MimeBodyPart = new MimeBodyPart()
      imagePartLogo.setHeader("Content-ID", "<logo>")
      imagePartLogo.setDisposition(Part.INLINE)
      imagePartLogo.attachFile(imgFile)

      multipart.addBodyPart(imagePartLogo)

      message.setContent(multipart)

      Transport.send(message)

      System.out.println("Email sent!")

    } catch {
      case e: MessagingException => throw new RuntimeException(e)
    }
  } 

  private def getEmailSession = {

    val username = "eduvem.email.test@gmail.com"
    val password = "eduvemtest123"

    val props = new Properties()
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.host", "smtp.gmail.com")
    props.put("mail.smtp.port", "465")
    props.put("mail.smtp.socketFactory.port", "465")
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
    // avoid hang by setting timeout: 30 seconds
    props.put("mail.smtp.timeout", "30000")
    props.put("mail.smtp.connectiontimeout", "30000")

    val session: Session = Session.getInstance(props,
      new Authenticator {
        override def getPasswordAuthentication = new PasswordAuthentication(username, password)
      });
    session
  }
}

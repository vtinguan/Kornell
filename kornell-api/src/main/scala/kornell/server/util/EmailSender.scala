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
import kornell.core.util.StringUtils

object EmailSender {

  def sendEmail(subject: String,
    from: String,
    to: String,
    body: String,
    imgFile: File): Unit =
    sendEmail(subject,
      from,
      to,
      Settings.get("REPLY_TO").getOrElse(from),
      body,
      imgFile)

  def sendEmail(subject: String,
    from: String,
    to: String,
    replyTo: String,
    body: String,
    imgFile: File): Unit =
    getEmailSession match {
      case Some(session) => {
        val message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from))
        message.setRecipients(Message.RecipientType.TO, to)
        message.setSentDate(new Date())
        message.setSubject(subject, "UTF-8")
        message.setReplyTo(Array(new InternetAddress(replyTo)))
        // creates message part
        val messageBodyPart: MimeBodyPart = new MimeBodyPart()
        messageBodyPart.setContent(body, "text/html; charset=utf-8")

        // creates multi-part
        val multipart: Multipart = new MimeMultipart()
        multipart.addBodyPart(messageBodyPart)

        if (imgFile != null) {
          val imagePartLogo: MimeBodyPart = new MimeBodyPart()
          imagePartLogo.setHeader("Content-ID", "<logo>")
          imagePartLogo.setDisposition(Part.INLINE)
          imagePartLogo.attachFile(imgFile)
          multipart.addBodyPart(imagePartLogo)
        }

        message.setContent(multipart)

        val transport = session.getTransport
        val username = smtp.get.username
        val password = smtp.get.password
        transport.connect(username, password)
        transport.sendMessage(message, Array(new InternetAddress(to)))

        System.out.println("Email sent!")
      }
      case None => System.err.println(s"No SMTP configured. If it was, a email would have been sent to $to")
    }

  private def getEmailSession = smtp.map { cfg =>
    val props = new Properties()
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.host", cfg.host)
    props.put("mail.smtp.port", cfg.port)
    props.put("mail.smtp.ssl.enable", "true");

    props.put("mail.transport.protocol", "smtp");
    /*
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.starttls.required", "true");
    */

    Session.getDefaultInstance(props);
  }

  lazy val SMTP_HOST = Settings.get("SMTP_HOST")
  lazy val SMTP_PORT = Settings.get("SMTP_PORT").get
  lazy val SMTP_USERNAME = Settings.get("SMTP_USERNAME").get
  lazy val SMTP_PASSWORD = Settings.get("SMTP_PASSWORD").get

  lazy val smtp: Option[SMTPConfig] = environmentCfg

  lazy val environmentCfg: Option[SMTPConfig] = SMTP_HOST.map { host =>
    new SMTPConfig(host, SMTP_PORT, SMTP_USERNAME, SMTP_PASSWORD)
  }

}

case class SMTPConfig(host: String, port: String, username: String, password: String)
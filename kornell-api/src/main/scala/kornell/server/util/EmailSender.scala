package kornell.server.util

import java.util.Properties
import javax.mail.internet.MimeMessage
import javax.mail.Session
import javax.mail.Transport
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Message
import javax.mail.internet.InternetAddress
import kornell.core.shared.data.Person
import javax.mail.internet.MimeBodyPart
import javax.mail.Part
import javax.mail.Multipart
import javax.mail.internet.MimeMultipart
import javax.activation.DataHandler
import java.io.InputStream
import javax.activation.DataSource
import javax.mail.util.ByteArrayDataSource
import org.apache.commons.io.IOUtils
import javax.activation.FileDataSource
import java.io.File
import java.util.HashMap
import java.util.Date
import kornell.core.shared.data.Institution
import org.apache.commons.io.FileUtils
import java.net.URL

object EmailSender {
	def sendEmail(person: Person, institution: Institution, confirmationLink: String){
	  
	  	val username: String = "strabstertest11@gmail.com"
		val password: String = "strabstertest111"
 
		val props: Properties = new Properties()
		props.put("mail.smtp.host", "smtp.gmail.com")
		props.put("mail.smtp.socketFactory.port", "465")
		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory")
		props.put("mail.smtp.auth", "true")
		props.put("mail.smtp.port", "465")
        // avoid hang by setting timeout: 30 seconds
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
			message.setFrom(new InternetAddress(institution.getName().toLowerCase()+"@eduvem.com.br"))
			message.setRecipients(Message.RecipientType.TO, person.getEmail())
			val sex: String = if ("F".equals(person.getSex())) "a" else "o"
			  
			message.setSubject("Bem-vind" + sex + " à Universidade Virtual " + institution.getName() + "!" )
			message.setSentDate(new Date())
			
			val body: String = """
				<div style="width: 700px;margin: 0 auto;padding: 20px 50px;border: 1px solid #CACACA;color: #444444;font-size: 18px;font-family: Helvetica,Arial,sans-serif;-webkit-box-shadow: 14px 14px 5px #AFAFAF;-moz-box-shadow: 14px 14px 5px #AFAFAF;box-shadow: 14px 14px 5px #AFAFAF;margin-bottom: 30px;border-radius: 10px;">
		            <h2 style="font-size: 18px;font-weight: normal;">Ol&aacute;, <b>""" + person.getFullName() + """</b></h2>
		            <h1 style="font-size: 25px;margin: 35px 0px;">Bem-vind""" + sex + """ &agrave; Universidade Virtual """ + institution.getName() + """.</h1>
		            <p style="margin-bottom: 50px;">Por favor, confirme seu cadastro para ativarmos a sua conta.</p>
		            <div style="width: 300px;margin: 0 auto;margin-bottom: 50px;text-align: center;display: block;height: auto;">
		            	<a href=" """ + confirmationLink + """" " target="_blank" style="text-decoration: none;">
		                	<div style="display: block;width: 220px;height: 30px;line-height: 28px;margin-right: 30px;text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.5);font-size: 14px;font-weight: bold;color: #e9e9e9;border-radius: 5px;display: block;border: 0px;background: #9b020a;background: -moz-linear-gradient(top, #9b020a 0%, #4b0105 100%);background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #9b020a), color-stop(100%, #4b0105));background: -webkit-linear-gradient(top, #9b020a 0%, #4b0105 100%);background: -o-linear-gradient(top, #9b020a 0%, #4b0105 100%);background: -ms-linear-gradient(top, #9b020a 0%, #4b0105 100%);background: linear-gradient(to bottom, #9b020a 0%, #4b0105 100%);filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='@colorStart', endColorstr='@colorEnd',GradientType=0 );margin: 0 auto;">Confirmar agora</div>
		            	</a>
		            </div>
		            <h2 style="margin-top: 50px;margin-bottom: 25px;font-size: 18px;font-weight: normal;">Depois da ativa&ccedil;&atilde;o voc&ecirc;  poder&aacute; acessar os cursos dispon&iacute;veis para voc&ecirc;, assim como todos os recursos deste ambiente.</h2>
		            <h2 style="margin-bottom: 25px;font-size: 18px;font-weight: normal;">Aproveite para trocar experi&ecirc;ncias e ampliar o seu conhecimento.</h2>
		            <h2 style="margin-bottom: 25px;font-size: 18px;font-weight: normal;">Cordialmente,</h2>
		            <h2 style="margin-bottom: 4px;font-size: 18px;font-weight: normal;"><b>Equipe """ + institution.getName() + """</b></h2>
		            <h2 style="margin-top: 0px;margin-bottom: 50px;font-size: 18px;font-weight: normal;">""" + institution.getName().toLowerCase() + """@eduvem.com.br</h2>
		            <img alt="" src="cid:logo" style="width: 300px;margin: 0 auto;display: block;">
		        </div>
			  """
 
	        // creates message part
	        val messageBodyPart: MimeBodyPart = new MimeBodyPart()
	        messageBodyPart.setContent(body, "text/html; charset=utf-8")
	 
	        // creates multi-part
	        val multipart: Multipart = new MimeMultipart()
	        multipart.addBodyPart(messageBodyPart)
	        
	        val imagePartLogo: MimeBodyPart = new MimeBodyPart()
            imagePartLogo.setHeader("Content-ID", "<logo>")
            imagePartLogo.setDisposition(Part.INLINE)
            
            val logoImageName: String = "logo300x80.png"
			val tDir: String = System.getProperty("java.io.tmpdir")
			val path: String = tDir + institution.getName() + "-" + logoImageName
		    val imgFile: File = new File(path)
	        if(!imgFile.exists()){
	            val url: URL = new URL(institution.getAssetsURL() + logoImageName)
				FileUtils.copyURLToFile(url, imgFile)
            }
			imagePartLogo.attachFile(imgFile)
			
			multipart.addBodyPart(imagePartLogo) 
	        
	        message.setContent(multipart)
	 
	        Transport.send(message)
 
			System.out.println("Email sent!")
		 
		} catch {
		    case e: MessagingException => throw new RuntimeException(e) 
		}
	}
}
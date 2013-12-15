package kornell.server.util

import java.io.File
import java.net.URL
import org.apache.commons.io.FileUtils
import kornell.core.entity.Institution
import kornell.core.entity.Person
import kornell.server.util.EmailSender
import java.util.UUID

object EmailService {
  def sendEmailConfirmation(person: Person, institution: Institution) {
    val sex = person.getSex() match { 
      case "F" => "a"
      case "M" => "o"
      case  _  => "o(a)"
  	}
    val subject = "Bem-vind" + sex + " à Universidade Virtual " + institution.getName() + "!"
    val from = getFromEmail(institution)
    val to = person.getEmail
    val body = wrapBody("""
    		<h2 style="font-size: 18px;font-weight: normal;">Ol&aacute;, <b>""" + person.getFullName() + """</b></h2>
    		<h1 style="font-size: 25px;margin: 35px 0px;">Bem-vind""" + sex + """ &agrave; Universidade Virtual """ + institution.getName() + """.</h1>
    		<p>Por favor, confirme seu cadastro para ativarmos a sua conta.</p> """ +
    		getActionButton(institution.getBaseURL()+"#vitrine:", "Confirmar agora") + """
    		<h2 style="margin-top: 50px;margin-bottom: 25px;font-size: 18px;font-weight: normal;">Depois da ativa&ccedil;&atilde;o voc&ecirc;  poder&aacute; acessar os cursos dispon&iacute;veis para voc&ecirc;, assim como todos os recursos deste ambiente.</h2>
    		<h2 style="margin-bottom: 25px;font-size: 18px;font-weight: normal;">Aproveite para trocar experi&ecirc;ncias e ampliar o seu conhecimento.</h2>""" +
    		getSignature(institution, from))
			
    val imgFile = getInstitutionLogoImage(institution)
    EmailSender.sendEmail(subject, from, to, body, imgFile)
  }
  
  def sendEmailRequestPasswordChange(person: Person, institution: Institution, requestPasswordChangeUUID: String) {
    val subject = "Você requisitou uma nova senha da Universidade Virtual " + institution.getName()
    val from = getFromEmail(institution)
    val to = person.getEmail
    val actionLink = institution.getBaseURL() + "#vitrine:" + requestPasswordChangeUUID
    val body = wrapBody("""
    		<p style="margin-bottom: 50px;">Ol&aacute;, <b>""" + person.getFullName() + """</b></p>
    		<p>Voc&ecirc; recentemente fez uma requisi&ccedil;&atilde;o de altera&ccedil;&atilde;o de senha da Universidade Virtual """+ institution.getName() +"""</p>
    		<p>Clique no bot&atilde;o abaixo para fazer a altera&ccedil;&atilde;o da senha.</p> """ +
    		getActionButton(actionLink, "Alterar senha") + """
    		<p>Caso n&atilde;o tenha requisitado esta mudan&ccedil;a, favor ignorar esta mensagem.</p>""" +
    		getSignature(institution, from))
			
    val imgFile = getInstitutionLogoImage(institution)
    EmailSender.sendEmail(subject, from, to, body, imgFile)
  }

  private def getFromEmail(institution: kornell.core.entity.Institution) = 
    institution.getName().toLowerCase() + "@eduvem.com.br"
  
  
  private def wrapBody(bodyText: String) = 
    """
    	<div style="width: 700px;margin: 0 auto;padding: 20px 50px;border: 1px solid #CACACA;color: #444444;font-size: 18px;font-family: Helvetica,Arial,sans-serif;-webkit-box-shadow: 14px 14px 5px #AFAFAF;-moz-box-shadow: 14px 14px 5px #AFAFAF;box-shadow: 14px 14px 5px #AFAFAF;margin-bottom: 30px;border-radius: 10px;">""" +
			bodyText + """
		</div>
    """
  
  private def getSignature(institution: Institution, from: String): String = 
    """
	  	<p>Cordialmente,</p>
	  	<p><b>Equipe """ + institution.getName() + """</b></p>
	  	<p>""" + from + """</p>
	  	<img alt="" src="cid:logo" style="width: 300px;height: 80px;margin: 0 auto;display: block;">
	"""

  private def getInstitutionLogoImage(institution: Institution): java.io.File = {
    val logoImageName: String = "logo300x80.png"
    val tDir: String = System.getProperty("java.io.tmpdir")
    val path: String = tDir + institution.getName() + "-" + logoImageName
    val imgFile: File = new File(path)
    if (!imgFile.exists()) {
      val url: URL = new URL(institution.getAssetsURL() + logoImageName)
      FileUtils.copyURLToFile(url, imgFile)
    }
    imgFile
  }

  def getActionButton(confirmationLink: String, actionButtonText: String) = 
    """
		<div style="width: 300px;margin: 0 auto;margin-bottom: 50px;margin-top: 50px;text-align: center;display: block;height: auto;">
			<a href="""" + confirmationLink + """" target="_blank" style="text-decoration: none;">
				<div style="display: block;width: 220px;height: 30px;line-height: 28px;margin-right: 30px;text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.5);font-size: 14px;font-weight: bold;color: #e9e9e9;border-radius: 5px;display: block;border: 0px;background: #9b020a;background: -moz-linear-gradient(top, #9b020a 0%, #4b0105 100%);background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #9b020a), color-stop(100%, #4b0105));background: -webkit-linear-gradient(top, #9b020a 0%, #4b0105 100%);background: -o-linear-gradient(top, #9b020a 0%, #4b0105 100%);background: -ms-linear-gradient(top, #9b020a 0%, #4b0105 100%);background: linear-gradient(to bottom, #9b020a 0%, #4b0105 100%);filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='@colorStart', endColorstr='@colorEnd',GradientType=0 );margin: 0 auto;">""" + actionButtonText + """</div>
			</a>
		</div>
	"""
}
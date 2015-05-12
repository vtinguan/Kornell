package kornell.server.util

import java.io.File
import java.net.URL
import org.apache.commons.io.FileUtils
import kornell.core.entity.Institution
import kornell.core.entity.Person
import java.util.UUID
import kornell.core.entity.Course
import kornell.core.entity.PersonCategory
import kornell.core.entity.CourseClass
import  kornell.core.util.StringUtils._
import kornell.server.jdbc.repository.PersonRepo
import kornell.core.entity.Enrollment

object EmailService {
  
  def sendEmailBatchEnrollment(person: Person, institution: Institution, courseClass: CourseClass) = {
    val subject = "A geração de matrículas foi concluída."
    val from = getFromEmail(institution)
    val to = person.getEmail
    val body = wrapBody("""
    		<p>Ol&aacute;, <b>""" + person.getFullName + """</b></p>
    		<p>&nbsp;</p>
    		<p>A geração de matrículas na turma """ + courseClass.getName + """ foi concluída.</p> """ +
    		getActionButton(institution.getBaseURL+"#a.courseClass:"+courseClass.getUUID, "Ir para a turma") + """
    		<p>&nbsp;</p>""" +
    		getSignature(institution, from))
			
    val imgFile = getInstitutionLogoImage(institution)
    EmailSender.sendEmail(subject, from, to, body, imgFile)
  }
  
  def sendEmailConfirmation(person: Person, institution: Institution) = {
    val subject = "Bem-vind" + PersonCategory.getSexSuffix(person) + " à " + institution.getFullName + "!"
    val from = getFromEmail(institution)
    val to = person.getEmail
    val body = wrapBody("""
    		<p>Ol&aacute;, <b>""" + person.getFullName + """</b></p>
    		<p>&nbsp;</p>
    		<p>Bem-vind""" + PersonCategory.getSexSuffix(person) + """ &agrave; """ + institution.getFullName + """.</p>
    		<p>Por favor, confirme seu cadastro para ativarmos a sua conta.</p> """ +
    		getActionButton(institution.getBaseURL+"#vitrine:", "Confirmar agora") + """
    		<p>Depois da ativa&ccedil;&atilde;o voc&ecirc;  poder&aacute; acessar os cursos dispon&iacute;veis para voc&ecirc;, assim como todos os recursos deste ambiente.</p>
    		<p>Aproveite para trocar experi&ecirc;ncias e ampliar o seu conhecimento.</p>
    		<p>&nbsp;</p>""" +
    		getSignature(institution, from))
			
    val imgFile = getInstitutionLogoImage(institution)
    EmailSender.sendEmail(subject, from, to, body, imgFile)
  }
  
  def sendEmailRequestPasswordChange(person: Person, institution: Institution, requestPasswordChangeUUID: String) = {
    val subject = "Você requisitou uma nova senha da " + institution.getFullName
    val from = getFromEmail(institution)
    val to = person.getEmail
    val actionLink = institution.getBaseURL+"#vitrine:" + requestPasswordChangeUUID
    val body = wrapBody("""
    		<p>Ol&aacute;, <b>""" + person.getFullName + """</b></p>
    		<p>&nbsp;</p>
    		<p>Voc&ecirc; recentemente fez uma requisi&ccedil;&atilde;o de altera&ccedil;&atilde;o de senha da """+ institution.getFullName +""".</p>
    		<p>Clique no bot&atilde;o abaixo para fazer a altera&ccedil;&atilde;o da senha.</p> """ +
    		getActionButton(actionLink, "Alterar senha") + """
    		<p>Caso n&atilde;o tenha requisitado esta mudan&ccedil;a, favor ignorar esta mensagem.</p>
    		<p>&nbsp;</p>""" +
    		getSignature(institution, from))
			
    val imgFile = getInstitutionLogoImage(institution)
    EmailSender.sendEmail(subject, from, to, body, imgFile)
  }
  
  //TODO: Consider ASYNC
  def sendEmailEnrolled(person: Person, institution: Institution, course: Course, enrollment: Enrollment) = {
    val subject = "Você foi matriculado no curso " + course.getTitle
    val from = getFromEmail(institution)
    val to = person.getEmail
    val hasPassword = PersonRepo(person.getUUID).hasPassword(institution.getUUID)
    val hasPasswordStr = if(hasPassword) {
      ""
    } else {
      "<p>Caso seja seu primeiro acesso, cadastre-se primeiro no sistema utilizando este email: " + to + "</p>"
    }
    val actionLink = if(hasPassword) {
      institution.getBaseURL + "#classroom:" + enrollment.getUUID
    } else {
      institution.getBaseURL + "#vitrine:" + person.getEmail
    }
    val body = wrapBody("""
    		<p>Ol&aacute;, <b>""" + person.getFullName + """</b></p>
    		<p>&nbsp;</p>
    		<p>Voc&ecirc; foi matriculad""" + PersonCategory.getSexSuffix(person) + """ no curso """+ course.getTitle +""" oferecido pela """+ institution.getFullName +""".</p> 
    		<p>Clique no bot&atilde;o abaixo para ir ao curso.</p> """ +
    		getActionButton(actionLink, "Acessar o Curso") + 
    		hasPasswordStr +
    		"""<p>&nbsp;</p>""" +
    		getSignature(institution, from))
			
    val imgFile = getInstitutionLogoImage(institution)
    EmailSender.sendEmail(subject, from, to, body, imgFile)
  }
  
  private def getFromEmail(institution: kornell.core.entity.Institution):String = EmailSender.SMTP_FROM
  
  
  private def wrapBody(bodyText: String) = 
    """
    	<div style="width: 700px;margin: 0 auto;padding: 20px 50px;border: 1px solid #CACACA;color: #444444;font-size: 18px;font-family: Helvetica,Arial,sans-serif;-webkit-box-shadow: 14px 14px 5px #AFAFAF;-moz-box-shadow: 14px 14px 5px #AFAFAF;box-shadow: 14px 14px 5px #AFAFAF;margin-bottom: 30px;border-radius: 10px;">""" +
			bodyText + """
		</div>
    """
  
  private def getSignature(institution: Institution, from: String): String = 
    """
	  	<p>Cordialmente,</p>
	  	<p><b>Equipe """ + institution.getFullName + """</b></p>
	  	<img alt="" src="cid:logo" style="width: 300px;height: 80px;margin: 0 auto;display: block;">
	"""

  private def getInstitutionLogoImage(institution: Institution): java.io.File = {
    val logoImageName: String = "logo300x80.png"
    val tDir: String = System.getProperty("java.io.tmpdir")
    val path: String = tDir + institution.getFullName + "-" + logoImageName
    val imgFile: File = new File(path)
    
    val purgeTime = System.currentTimeMillis - (1 * 24 * 60 * 60 * 1000) //one day
    if(imgFile.lastModified < purgeTime && !imgFile.delete)
      System.err.println("Unable to delete file: " + imgFile)
      
    if (!imgFile.exists) {
      //TODO: Use ContentStore API
      val url = new URL(mkurl(institution.getAssetsURL,logoImageName))
      FileUtils.copyURLToFile(url, imgFile)
    }
    imgFile
  }

  def getActionButton(actionLink: String, actionButtonText: String) = 
    """
		<div style="width: 300px;margin: 0 auto;margin-bottom: 50px;margin-top: 50px;text-align: center;display: block;height: auto;">
			<a href="""" + actionLink + """" target="_blank" style="text-decoration: none;">
				<div style="display: block;width: 220px;height: 30px;line-height: 28px;margin-right: 30px;text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.5);font-size: 14px;font-weight: bold;color: #e9e9e9;border-radius: 5px;display: block;border: 0px;background: #9b020a;background: -moz-linear-gradient(top, #9b020a 0%, #4b0105 100%);background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #9b020a), color-stop(100%, #4b0105));background: -webkit-linear-gradient(top, #9b020a 0%, #4b0105 100%);background: -o-linear-gradient(top, #9b020a 0%, #4b0105 100%);background: -ms-linear-gradient(top, #9b020a 0%, #4b0105 100%);background: linear-gradient(to bottom, #9b020a 0%, #4b0105 100%);filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='@colorStart', endColorstr='@colorEnd',GradientType=0 );margin: 0 auto;">""" + actionButtonText + """</div>
			</a>
		</div>
	"""
}
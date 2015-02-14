package kornell.server.api

import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.RoleCategory
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.server.report.ReportCertificateGenerator
import kornell.server.repository.s3.S3
import kornell.server.report.ReportCourseClassGenerator
import kornell.server.report.ReportGenerator
import kornell.server.report.ReportInstitutionBillingGenerator
import kornell.server.jdbc.repository.InstitutionRepo
import kornell.server.jdbc.repository.CourseClassRepo
import java.text.SimpleDateFormat
import kornell.core.error.exception.ServerErrorException

@Path("/report")
class ReportResource {

  @GET
  @Path("/certificate/{userUUID}/{courseClassUUID}")
  @Produces(Array("application/pdf"))
  def get( /*implicit @Context sc:SecurityContext,*/
    @Context resp: HttpServletResponse,
    @PathParam("userUUID") userUUID: String,
    @PathParam("courseClassUUID") courseClassUUID: String) = /*Auth.withPerson{ person =>*/ {

    resp.addHeader("Content-disposition", "attachment; filename=Certificado.pdf")
    ReportCertificateGenerator.generateCertificate(userUUID, courseClassUUID)
  }

  @GET
  @Path("/certificate")
  def get(implicit @Context sc: SecurityContext,
    @QueryParam("courseClassUUID") courseClassUUID: String) = AuthRepo().withPerson { p =>
    val courseClass = CourseClassesRepo(courseClassUUID).get
    val roles = AuthRepo().getUserRoles
    if (!(RoleCategory.isPlatformAdmin(roles) ||
      RoleCategory.isInstitutionAdmin(roles, courseClass.getInstitutionUUID) ||
      RoleCategory.isCourseClassAdmin(roles, courseClass.getUUID)))
    	throw new UnauthorizedAccessException("unauthorizedAccessReport")
    else {
      try {
        val certificateInformationTOsByCourseClass = ReportCertificateGenerator.getCertificateInformationTOsByCourseClass(courseClassUUID)
        if (certificateInformationTOsByCourseClass.length == 0) {
          throw new ServerErrorException("errorGeneratingReport")
        } else {
          var filename = p.getUUID + courseClassUUID + ".pdf"
          S3.certificates.delete(filename)
          val report = ReportCertificateGenerator.generateCertificate(certificateInformationTOsByCourseClass)
          val bs = new ByteArrayInputStream(report)
          S3.certificates.put(filename,
            bs,
            "application/pdf",
            "Content-Disposition: attachment; filename=\"" + filename + ".pdf\"",
            Map("certificatedata" -> "09/01/1980", "requestedby" -> p.getFullName()))
          S3.certificates.url(filename)
        }
      } catch {
        case e: Exception =>
          throw new ServerErrorException("errorGeneratingReport", e)
      }
    }
  }

  @GET
  @Path("courseClassCertificateExists")
  def fileExists(implicit @Context sc: SecurityContext,
    @QueryParam("courseClassUUID") courseClassUUID: String) = AuthRepo().withPerson { p =>
    try {
      var filename = p.getUUID + courseClassUUID + ".pdf"
      val url = S3.certificates.url(filename)

      HttpURLConnection.setFollowRedirects(false);
      //HttpURLConnection.setInstanceFollowRedirects(false)
      val con = new URL(url).openConnection.asInstanceOf[HttpURLConnection]
      con.setRequestMethod("HEAD")
      if (con.getResponseCode() == HttpURLConnection.HTTP_OK)
        url
      else
        ""
    } catch {
      case e: Exception => throw new ServerErrorException("errorCheckingCerts", e)
    }
  }

  @GET
  @Path("/courseClassInfo")
  def getCourseClassInfo(@Context resp: HttpServletResponse,
    @QueryParam("courseUUID") courseUUID: String,
    @QueryParam("courseClassUUID") courseClassUUID: String,
    @QueryParam("fileType") fileType: String) = {
	  if(courseUUID != null || courseClassUUID != null){
	    val fType = {
	      if(fileType != null && fileType == "xls")
	        "xls"
	      else
	        "pdf"
	    }
	    val fileName = { 
	    	if(courseUUID != null)
	    	  CourseRepo(courseUUID).get.getTitle
	    	else
	    	  CourseClassRepo(courseClassUUID).get.getName
	    } + " - " + new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())+ "."+fType
	    
	    resp.addHeader("Content-disposition", "attachment; filename=" + fileName)
	    if(fType != null && fType == "xls")
	    	resp.setContentType("application/vnd.ms-excel")
	    else
	    	resp.setContentType("application/pdf")
	    ReportCourseClassGenerator.generateCourseClassReport(courseUUID, courseClassUUID, fType)
	  }
  }

  @GET
  @Path("/institutionBilling")
  def getInstitutionBilling(@Context resp: HttpServletResponse,
    @QueryParam("institutionUUID") institutionUUID: String,
    @QueryParam("periodStart") periodStart: String,
    @QueryParam("periodEnd") periodEnd: String) = {
    val institution = InstitutionRepo(institutionUUID).get
    resp.addHeader("Content-disposition", "attachment; filename=" + institution.getName + " - " + periodStart + ".xls")
    resp.setContentType("application/vnd.ms-excel")
    ReportInstitutionBillingGenerator.generateInstitutionBillingReport(institution, periodStart, periodEnd)
  }

  @GET
  @Path("/clear")
  @Produces(Array("application/pdf"))
  def clearJasperFiles = ReportGenerator.clearJasperFiles

}

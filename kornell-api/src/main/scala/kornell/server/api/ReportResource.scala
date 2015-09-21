package kornell.server.api

import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.RoleCategory
import kornell.core.error.exception.ServerErrorException
import kornell.core.error.exception.UnauthorizedAccessException
import kornell.core.to.SimplePeopleTO
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.server.jdbc.repository.CourseRepo
import kornell.server.jdbc.repository.InstitutionRepo
import kornell.server.report.ReportCertificateGenerator
import kornell.server.report.ReportCourseClassGenerator
import kornell.server.report.ReportGenerator
import kornell.server.report.ReportInstitutionBillingGenerator
import kornell.server.repository.s3.S3
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.PersonRepo

@Path("/report")
class ReportResource {

  @GET
  @Path("/certificate/{userUUID}/{courseClassUUID}")
  @Produces(Array("application/pdf"))
  def get(@Context resp: HttpServletResponse,
    @PathParam("userUUID") userUUID: String,
    @PathParam("courseClassUUID") courseClassUUID: String) = AuthRepo().withPerson{ person => {
	    val roles = AuthRepo().getUserRoles
	    if (!(RoleCategory.isPlatformAdmin(roles, PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID) ||
	      RoleCategory.isInstitutionAdmin(roles, PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID) ||
	      person.getUUID == userUUID))
	      throw new UnauthorizedAccessException("accessDenied")
	    resp.addHeader("Content-disposition", "attachment; filename=Certificado.pdf")
	    ReportCertificateGenerator.generateCertificate(userUUID, courseClassUUID)
	  }
  }

  @PUT
  @Path("/certificate")
  @Consumes(Array(SimplePeopleTO.TYPE))
  def get(@QueryParam("courseClassUUID") courseClassUUID: String, 
    peopleTO: SimplePeopleTO) = AuthRepo().withPerson { p =>
    val courseClass = CourseClassesRepo(courseClassUUID).get
    val roles = AuthRepo().getUserRoles
    if (!(RoleCategory.isPlatformAdmin(roles, courseClass.getInstitutionUUID) ||
      RoleCategory.isInstitutionAdmin(roles, courseClass.getInstitutionUUID) ||
      RoleCategory.isCourseClassAdmin(roles, courseClass.getUUID) ||
      RoleCategory.isCourseClassObserver(roles, courseClass.getUUID)))
    	throw new UnauthorizedAccessException("unauthorizedAccessReport")
    else {
      try {
        var filename = p.getUUID + courseClassUUID + ".pdf"
        //S3.certificates.delete(filename)
        val people = peopleTO.getSimplePeopleTO
        val enrollmentUUIDs = {
          if(people != null && people.size > 0) {
            var enrollmentUUIDsVar = ""
		    for (i <- 0 until people.size) {
		      val person = people.get(i)
		      val enrollmentUUID = EnrollmentsRepo.byCourseClassAndUsername(courseClassUUID, person.getUsername)
		      if(enrollmentUUID.isDefined){
		          if(enrollmentUUIDsVar.length != 0) enrollmentUUIDsVar += ","
		    	  enrollmentUUIDsVar += "'" + enrollmentUUID.get + "'"
		      }
		    }
            enrollmentUUIDsVar
          }
          else null
        }
      
        val certificateInformationTOsByCourseClass = ReportCertificateGenerator.getCertificateInformationTOsByCourseClass(courseClassUUID, enrollmentUUIDs)
        if (certificateInformationTOsByCourseClass.length == 0) {
          throw new ServerErrorException("errorGeneratingReport")
        } else {
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
  def fileExists(@QueryParam("courseClassUUID") courseClassUUID: String) = AuthRepo().withPerson { p =>
    try {
      var filename = p.getUUID + courseClassUUID + ".pdf"
      val url = S3.certificates.url(filename)

      HttpURLConnection.setFollowRedirects(false);
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
    @QueryParam("fileType") fileType: String) = AuthRepo().withPerson{ person => {
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
  }

  @GET
  @Path("/institutionBilling")
  def getInstitutionBilling(@Context resp: HttpServletResponse,
    @QueryParam("institutionUUID") institutionUUID: String,
    @QueryParam("periodStart") periodStart: String,
    @QueryParam("periodEnd") periodEnd: String) = AuthRepo().withPerson{ person => {
	    val roles = AuthRepo().getUserRoles
	    if (!(RoleCategory.isPlatformAdmin(roles, PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID) ||
	      RoleCategory.isInstitutionAdmin(roles, PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID)))
	      throw new UnauthorizedAccessException("accessDenied")
	    val institution = InstitutionRepo(institutionUUID).get
	    resp.addHeader("Content-disposition", "attachment; filename=" + institution.getName + " - " + periodStart + ".xls")
	    resp.setContentType("application/vnd.ms-excel")
	    ReportInstitutionBillingGenerator.generateInstitutionBillingReport(institution, periodStart, periodEnd)
    }
  }

  @GET
  @Path("/clear")
  @Produces(Array("application/pdf"))
  def clearJasperFiles = ReportGenerator.clearJasperFiles

}

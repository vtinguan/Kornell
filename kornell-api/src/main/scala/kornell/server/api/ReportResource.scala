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
import kornell.server.jdbc.repository.CourseClassRepo

@Path("/report")
class ReportResource(
    val authRepo:AuthRepo,
    val courseClassesRepo:CourseClassesRepo) {

  def this() = this(null,null)
  
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
    @Context resp: HttpServletResponse,
    @QueryParam("courseClassUUID") courseClassUUID: String) = authRepo.withPerson { p =>
    val courseClass = courseClassesRepo.byUUID(courseClassUUID).get
    val roles = authRepo.getUserRoles
    if (!(RoleCategory.isPlatformAdmin(roles) ||
      RoleCategory.isInstitutionAdmin(roles, courseClass.getInstitutionUUID) ||
      RoleCategory.isCourseClassAdmin(roles, courseClass.getUUID)))
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized attempt to generate the class' certificates without admin rights.");
    else {
      try {
        val certificateInformationTOsByCourseClass = ReportCertificateGenerator.getCertificateInformationTOsByCourseClass(courseClassUUID)
        if (certificateInformationTOsByCourseClass.length == 0) {
          resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating the report.");
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
          resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating the report.");
      }
    }
  }

  @GET
  @Path("courseClassCertificateExists")
  def fileExists(@Context resp: HttpServletResponse,
    @QueryParam("courseClassUUID") courseClassUUID: String) = authRepo.withPerson { p =>
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
      case e: Exception => ""
    }
  }

  @GET
  @Path("/courseClassInfo")
  def getCourseClassInfo(@Context resp: HttpServletResponse,
    @QueryParam("courseClassUUID") courseClassUUID: String,
    @QueryParam("fileType") fileType: String) = {
    val fType = {
      if(fileType != null && fileType == "xls")
        "xls"
      else
        "pdf"
    }
    resp.addHeader("Content-disposition", "attachment; filename=info."+fType)
    if(fType != null && fType == "xls")
    	resp.setContentType("application/vnd.ms-excel")
    else
    	resp.setContentType("application/pdf")
    ReportCourseClassGenerator.generateCourseClassReport(courseClassUUID, fType)
  }

  @GET
  @Path("/clear")
  @Produces(Array("application/pdf"))
  def clearJasperFiles = ReportGenerator.clearJasperFiles

}

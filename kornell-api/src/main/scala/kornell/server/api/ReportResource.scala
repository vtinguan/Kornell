package kornell.server.api

import javax.servlet.http.HttpServletResponse
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import kornell.server.report.ReportGenerator
import javax.ws.rs.QueryParam
import kornell.server.repository.s3.S3
import kornell.core.util.UUID
import java.io.ByteArrayInputStream
import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.AuthRepo
import scala.collection.JavaConverters._
import kornell.core.entity.RoleCategory
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
import kornell.server.jdbc.repository.CourseClassesRepo
import java.net.HttpURLConnection
import java.net.URL
import kornell.core.util.StringUtils

@Path("/report")
class ReportResource {

	@GET
	@Path("/certificate/{userUUID}/{courseClassUUID}")
	@Produces(Array("application/pdf"))
	def get(/*implicit @Context sc:SecurityContext,*/
	    @Context resp:HttpServletResponse,
	    @PathParam("userUUID") userUUID:String, 
	    @PathParam("courseClassUUID") courseClassUUID:String) = /*Auth.withPerson{ person =>*/ {
	      
	      
	    resp.addHeader("Content-disposition", "attachment; filename=Certificado.pdf")
		ReportGenerator.generateCertificate(userUUID, courseClassUUID)
	}

	@GET
	@Path("/certificate")
	def get(implicit @Context sc:SecurityContext,
	    @Context resp:HttpServletResponse,
	   @QueryParam("courseClassUUID") courseClassUUID:String) = AuthRepo.withPerson { p =>
	  val courseClass = CourseClassesRepo(courseClassUUID).get
    val roles = AuthRepo.getUserRoles
    if (!(RoleCategory.isPlatformAdmin(roles) || 
        RoleCategory.isInstitutionAdmin(roles, courseClass.getInstitutionUUID) ||
        RoleCategory.isCourseClassAdmin(roles, courseClass.getUUID)))
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized attempt to generate the class' certificates without admin rights.");
    else {
			try {
				val certificateInformationTOsByCourseClass = ReportGenerator.getCertificateInformationTOsByCourseClass(courseClassUUID)
				if(certificateInformationTOsByCourseClass.length == 0){
	        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating the report.");
				} else {
					var filename = p.getUUID + courseClassUUID + ".pdf"
					S3.certificates.delete(filename)
					val report = ReportGenerator.generateCertificate(certificateInformationTOsByCourseClass)
			    val bs = new ByteArrayInputStream(report)
			    S3.certificates.put(filename,
			      bs,
			      "application/pdf", 
			      "Content-Disposition: attachment; filename=\""+filename+".pdf\"",
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
  def fileExists(implicit @Context sc:SecurityContext, 
      @Context resp:HttpServletResponse,
	   @QueryParam("courseClassUUID") courseClassUUID:String) = AuthRepo.withPerson { p =>
    try {
			var filename = p.getUUID + courseClassUUID + ".pdf"
	    val url = S3.certificates.url(filename)
      
      HttpURLConnection.setFollowRedirects(false);
      //HttpURLConnection.setInstanceFollowRedirects(false)
      val con = new URL(url).openConnection.asInstanceOf[HttpURLConnection]
      con.setRequestMethod("HEAD")
      if(con.getResponseCode() == HttpURLConnection.HTTP_OK)
        url
      else
        ""
    } catch {
      case e: Exception => ""
    }
  }

	@GET
	@Path("/courseClassInfo")
	def getCourseClassInfo(@Context resp:HttpServletResponse,
	   @QueryParam("courseClassUUID") courseClassUUID:String) = 
			try {
				resp.addHeader("Content-disposition", "attachment; filename=info.pdf")
				ReportGenerator.generateCourseClassReport(courseClassUUID)
      } catch {
        case e: Exception => {
          e.printStackTrace
          resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating the report.");
        }
      }
  
}

package kornell.server.api

import javax.servlet.http.HttpServletResponse
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import kornell.server.util.ReportGenerator

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
}
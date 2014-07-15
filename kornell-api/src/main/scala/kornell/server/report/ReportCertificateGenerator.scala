package kornell.server.report

import java.io.File
import java.net.URL
import java.sql.ResultSet
import java.util.Date
import java.util.HashMap

import org.apache.commons.io.FileUtils

import kornell.core.to.report.CertificateInformationTO
import kornell.core.util.StringUtils.composeURL
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.repository.TOs

object ReportCertificateGenerator {

  implicit def toCertificateInformationTO(rs: ResultSet): CertificateInformationTO =
    TOs.newCertificateInformationTO(
      rs.getString("fullName"),
      rs.getString("cpf"),
      rs.getString("title"),
      rs.getDate("certifiedAt"),
      rs.getString("assetsURL"),
      rs.getString("distributionPrefix"))
      
   def generateCertificate(userUUID: String, courseClassUUID: String): Array[Byte] = {
    generateCertificateReport(sql"""
				select p.fullName, c.title, i.assetsURL, cv.distributionPrefix, p.cpf, e.certifiedAt
	    	from Person p
					join Enrollment e on p.uuid = e.person_uuid
					join CourseClass cc on cc.uuid = e.class_uuid
		    	join CourseVersion cv on cv.uuid = cc.courseVersion_uuid
		    	join Course c on c.uuid = cv.course_uuid
					join S3ContentRepository s on s.uuid = cv.repository_uuid
					join Institution i on i.uuid = cc.institution_uuid
				where e.certifiedAt is not null and 
        	p.uuid = $userUUID and
				  cc.uuid = $courseClassUUID
		    """.map[CertificateInformationTO](toCertificateInformationTO))
  }
  
  def generateCertificate(certificateInformationTOs: List[CertificateInformationTO]): Array[Byte] = {
    generateCertificateReport(certificateInformationTOs)
  }
  
  def generateCertificateByCourseClass(courseClassUUID: String): Array[Byte] = {
    generateCertificateReport(getCertificateInformationTOsByCourseClass(courseClassUUID))
  }
  
  def getCertificateInformationTOsByCourseClass(courseClassUUID: String) = 
    sql"""
				select p.fullName, c.title, i.assetsURL, cv.distributionPrefix, p.cpf, e.certifiedAt
	    	from Person p
				  join Enrollment e on p.uuid = e.person_uuid
				  join CourseClass cc on cc.uuid = e.class_uuid
	    		join CourseVersion cv on cv.uuid = cc.courseVersion_uuid
	    		join Course c on c.uuid = cv.course_uuid
					join S3ContentRepository s on s.uuid = cv.repository_uuid
					join Institution i on i.uuid = cc.institution_uuid
				where e.certifiedAt is not null and 
        	cc.uuid = $courseClassUUID
		    """.map[CertificateInformationTO](toCertificateInformationTO)

  private def generateCertificateReport(certificateData: List[CertificateInformationTO]): Array[Byte] = {
    if(certificateData.length == 0){
    	return null
    }
    val parameters: HashMap[String, Object] = new HashMap()
    val assetsURL: String = composeURL(certificateData.head.getAssetsURL(), certificateData.head.getDistributionPrefix(), "/reports")
    parameters.put("assetsURL", assetsURL + "/")
    val jasperPath = composeURL(assetsURL, "certificate.jasper")
	  
  	//store one jasperfile per courseclass
    val jasperFile: File = new File(System.getProperty("java.io.tmpdir") + "tmp-" + certificateData.head.getCourseTitle() + ".jasper")
    
    
    /*val diff = new Date().getTime - jasperFile.lastModified
    if(diff > 7 * 24 * 60 * 60 * 1000) //delete if older than 7 days
		  jasperFile.delete*/

    if(!jasperFile.exists)
    	FileUtils.copyURLToFile(new URL(jasperPath), jasperFile)
    	
    ReportGenerator.getReportBytes(certificateData, parameters, jasperFile)
    
  }
  
}
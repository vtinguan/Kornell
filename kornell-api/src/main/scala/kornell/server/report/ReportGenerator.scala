package kornell.server.report

import java.util.HashMap
import scala.collection.JavaConverters._
import kornell.server.jdbc.SQL._
import net.sf.jasperreports.engine.JREmptyDataSource
import net.sf.jasperreports.engine.JasperRunManager
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL
import java.sql.ResultSet
import kornell.core.util.StringUtils.composeURL
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.util.JRLoader
import java.util.Date
import kornell.server.repository.TOs
import kornell.core.to.CertificateInformationTO
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.JasperExportManager

object ReportGenerator {

  implicit def toCertificateInformationTO(rs: ResultSet): CertificateInformationTO =
    TOs.newCertificateInformationTO(
      rs.getString("fullName"),
      rs.getString("cpf"),
      rs.getString("title"),
      rs.getDate("enrolledOn"),
      rs.getString("assetsURL"),
      rs.getString("distributionPrefix"))
      
  
  def generateCertificate(userUUID: String, courseClassUUID: String): Array[Byte] = {
    generateReport(sql"""
				select p.fullName, c.title, i.assetsURL, cv.distributionPrefix, p.cpf, e.enrolledOn
	    	from Person p
					join Enrollment e on p.uuid = e.person_uuid
					join CourseClass cc on cc.uuid = e.class_uuid
		    	join CourseVersion cv on cv.uuid = cc.courseVersion_uuid
		    	join Course c on c.uuid = cv.course_uuid
					join S3ContentRepository s on s.uuid = cv.repository_uuid
					join Institution i on i.uuid = cc.institution_uuid
				where p.uuid = $userUUID and
				  cc.uuid = $courseClassUUID
		    """.map[CertificateInformationTO](toCertificateInformationTO))
  }
  
  def generateCertificateByCourseClass(courseClassUUID: String): Array[Byte] = {
    generateReport(sql"""
				select p.fullName, c.title, i.assetsURL, cv.distributionPrefix, p.cpf, e.enrolledOn
	    	from Person p
				  join Enrollment e on p.uuid = e.person_uuid
				  join CourseClass cc on cc.uuid = e.class_uuid
	    		join CourseVersion cv on cv.uuid = cc.courseVersion_uuid
	    		join Course c on c.uuid = cv.course_uuid
					join S3ContentRepository s on s.uuid = cv.repository_uuid
					join Institution i on i.uuid = cc.institution_uuid
				where cc.uuid = $courseClassUUID
		    """.map[CertificateInformationTO](toCertificateInformationTO))
  }

  private def generateReport(certificateData: List[CertificateInformationTO]): Array[Byte] = {
    val collectionDS = new JRBeanCollectionDataSource(certificateData asJava)
    
    //Preparing parameters
    val parameters: HashMap[String, Object] = new HashMap()
    val assetsURL: String = composeURL(certificateData.head.getAssetsURL(), certificateData.head.getDistributionPrefix(), "/reports")
    parameters.put("assetsURL", assetsURL + "/")
    val jasperPath = composeURL(assetsURL, "certificate.jasper")
	  
  	//store one jasperfile per courseclass
    val file: File = new File(System.getProperty("java.io.tmpdir") + "tmp-" + certificateData.head.getCourseTitle() + ".jasper")
    
    val diff = new Date().getTime - file.lastModified;
    //if(diff > 1 * 24 * 60 * 60 * 1000) //delete if older than 1 day
    if(file.exists)
		  file.delete

    if(!file.exists)
    	FileUtils.copyURLToFile(new URL(jasperPath), file)
    
    val jasperReport = JRLoader.loadObject(file).asInstanceOf[JasperReport]
    
    JasperRunManager.runReportToPdf(jasperReport, parameters, collectionDS)
    
    //val jasperPrint = JasperFillManager.fillReportToFile(jasperPath, parameters, collectionDS)
    //JasperExportManager.exportReportToPdfFile(jasperPrint, "C://test.pdf");
  }
  
}
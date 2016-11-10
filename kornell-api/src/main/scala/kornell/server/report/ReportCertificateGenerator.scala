package kornell.server.report

import java.io.File
import java.net.URL
import java.sql.ResultSet
import java.util.HashMap
import org.apache.commons.io.FileUtils
import kornell.core.error.exception.EntityConflictException
import kornell.core.to.report.CertificateInformationTO
import kornell.core.util.StringUtils.composeURL
import kornell.server.jdbc.PreparedStmt
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.repository.TOs
import kornell.server.util.Settings
import kornell.server.util.DateConverter
import kornell.server.authentication.ThreadLocalAuthenticator
import java.util.Date

object ReportCertificateGenerator {

  def newCertificateInformationTO: CertificateInformationTO = new CertificateInformationTO
  def newCertificateInformationTO(personFullName: String, personCPF: String, courseTitle: String, courseClassName: String, institutionName: String, courseClassFinishedDate: Date, assetsURL: String, distributionPrefix: String, courseVersionUUID: String, baseURL: String): CertificateInformationTO = {
    val dateConverter = new DateConverter(ThreadLocalAuthenticator.getAuthenticatedPersonUUID.get)
    val to = newCertificateInformationTO
    to.setPersonFullName(personFullName)
    to.setPersonCPF(personCPF)
    to.setCourseTitle(courseTitle)
    to.setCourseClassName(courseClassName)
    to.setInstitutionName(institutionName)
    to.setAssetsURL(assetsURL)
    to.setDistributionPrefix(distributionPrefix)
    to.setCourseVersionUUID(courseVersionUUID)
    to.setBaseURL(baseURL)
    to.setCourseClassFinishedDate(dateConverter.dateToInstitutionTimezone(courseClassFinishedDate))
    to
  }
  
  implicit def toCertificateInformationTO(rs: ResultSet): CertificateInformationTO =
    newCertificateInformationTO(
      rs.getString("fullName"),
      rs.getString("cpf"),
      rs.getString("title"),
      rs.getString("name"),
      rs.getString("institutionName"),
      rs.getTimestamp("certifiedAt"),
      rs.getString("assetsRepositoryUUID"),
      rs.getString("distributionPrefix"),
      rs.getString("courseVersionUUID"),
      rs.getString("baseURL"))
      
   def generateCertificate(userUUID: String, courseClassUUID: String): Array[Byte] = {
    generateCertificateReport(sql"""
				select p.fullName, c.title, cc.name, i.fullName as institutionName, i.assetsRepositoryUUID, cv.distributionPrefix, p.cpf, e.certifiedAt, cv.uuid as courseVersionUUID, i.baseURL
	    		from Person p
					join Enrollment e on p.uuid = e.person_uuid
					join CourseClass cc on cc.uuid = e.class_uuid
		    	join CourseVersion cv on cv.uuid = cc.courseVersion_uuid
		    	join Course c on c.uuid = cv.course_uuid
				join Institution i on i.uuid = cc.institution_uuid
				join S3ContentRepository s on s.uuid = i.assetsRepositoryUUID
				where e.certifiedAt is not null and 
        		  p.uuid = $userUUID and
				  cc.uuid = $courseClassUUID
		    """.map[CertificateInformationTO](toCertificateInformationTO))
  }
  
  def generateCertificate(certificateInformationTOs: List[CertificateInformationTO]): Array[Byte] = {
    generateCertificateReport(certificateInformationTOs)
  }
  
  def generateCertificateByCourseClass(courseClassUUID: String): Array[Byte] = {
    generateCertificateReport(getCertificateInformationTOsByCourseClass(courseClassUUID, null))
  }
  
  def getCertificateInformationTOsByCourseClass(courseClassUUID: String, enrollments: String) = {
    var sql = """select p.fullName, c.title, cc.name, i.fullName as institutionName, i.assetsRepositoryUUID, cv.distributionPrefix, p.cpf, e.certifiedAt, cv.uuid as courseVersionUUID, i.baseURL
      from Person p 
      join Enrollment e on p.uuid = e.person_uuid 
      join CourseClass cc on cc.uuid = e.class_uuid 
      join CourseVersion cv on cv.uuid = cc.courseVersion_uuid  
      join Course c on c.uuid = cv.course_uuid  
      join Institution i on i.uuid = cc.institution_uuid 
      where e.certifiedAt is not null and  
      e.state <> 'cancelled' and """ +
		s"""cc.uuid = '$courseClassUUID' """
	  if(enrollments != null)
		  sql += s"""and e.uuid in ( $enrollments )"""
    if (sql.contains("--")) throw new EntityConflictException("invalidValue")
    val pstmt = new PreparedStmt(sql,List())    
    pstmt.map[CertificateInformationTO](toCertificateInformationTO)
  }

  private def generateCertificateReport(certificateData: List[CertificateInformationTO]): Array[Byte] = {
    if(certificateData.length == 0){
    	return null
    }
    val parameters: HashMap[String, Object] = new HashMap()
    val institutionURL: String = composeURL(certificateData.head.getBaseURL, "repository", certificateData.head.getAssetsURL) + "/"
    parameters.put("institutionURL", institutionURL)
    
    val assetsURL: String = composeURL(institutionURL, certificateData.head.getDistributionPrefix, "/reports") + "/"
    parameters.put("assetsURL", assetsURL) 
	  
    val fileName = Settings.tmpDir + "tmp-" + certificateData.head.getCourseVersionUUID + (new Date).getTime + ".jasper"
    val jasperFile: File = new File(fileName)

    if(jasperFile.exists)
		  jasperFile.delete
		      
    try {
    	FileUtils.copyURLToFile(new URL(composeURL(assetsURL, "certificate.jasper")), jasperFile)
      ReportGenerator.getReportBytes(certificateData, parameters, jasperFile)
    } catch {
      //if a certificate isn't found on the version, use the default one
      case e: Exception => { 
        val cl = Thread.currentThread.getContextClassLoader 
        val jasperStream = cl.getResourceAsStream("reports/certificate.jasper")
        ReportGenerator.getReportBytesFromStream(certificateData, parameters, jasperStream, "pdf")
      }
    }  
  }
  
}
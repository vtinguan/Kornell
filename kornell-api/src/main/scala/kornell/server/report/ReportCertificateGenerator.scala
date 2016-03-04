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
  def newCertificateInformationTO(personFullName: String, personCPF: String, courseTitle: String, courseClassName: String, courseClassFinishedDate: Date, assetsURL: String, distributionPrefix: String, courseVersionUUID: String, baseURL: String): CertificateInformationTO = {
    val dateConverter = new DateConverter(ThreadLocalAuthenticator.getAuthenticatedPersonUUID.get)
    val to = newCertificateInformationTO
    to.setPersonFullName(personFullName)
    to.setPersonCPF(personCPF)
    to.setCourseTitle(courseTitle)
    to.setCourseClassName(courseClassName)
    to.setCourseClassFinishedDate(dateConverter.dateToInstitutionTimezone(courseClassFinishedDate))
    to.setAssetsURL(assetsURL)
    to.setDistributionPrefix(distributionPrefix)
    to.setCourseVersionUUID(courseVersionUUID)
    to.setBaseURL(baseURL)
    to
  }
  
  implicit def toCertificateInformationTO(rs: ResultSet): CertificateInformationTO =
    newCertificateInformationTO(
      rs.getString("fullName"),
      rs.getString("cpf"),
      rs.getString("title"),
      rs.getString("name"),
      rs.getTimestamp("certifiedAt"),
      rs.getString("assetsRepositoryUUID"),
      rs.getString("distributionPrefix"),
      rs.getString("courseVersionUUID"),
      rs.getString("baseURL"))
      
   def generateCertificate(userUUID: String, courseClassUUID: String): Array[Byte] = {
    generateCertificateReport(sql"""
				select p.fullName, c.title, cc.name, i.assetsRepositoryUUID, cv.distributionPrefix, p.cpf, e.certifiedAt, cv.uuid as courseVersionUUID, i.baseURL
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
    var sql = """select p.fullName, c.title, cc.name, i.assetsRepositoryUUID, cv.distributionPrefix, p.cpf, e.certifiedAt, cv.uuid as courseVersionUUID, i.baseURL
      from Person p 
      join Enrollment e on p.uuid = e.person_uuid 
      join CourseClass cc on cc.uuid = e.class_uuid 
      join CourseVersion cv on cv.uuid = cc.courseVersion_uuid 
      join Course c on c.uuid = cv.course_uuid  
      join Institution i on i.uuid = cc.institution_uuid 
      where e.certifiedAt is not null and  """ +
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
    val assetsURL: String = composeURL(certificateData.head.getBaseURL, "repository", certificateData.head.getAssetsURL, certificateData.head.getDistributionPrefix, "/reports") + "/"
    parameters.put("assetsURL", assetsURL)
	  

  	//store one jasperfile per course
    val fileName = Settings.tmpDir + "tmp-" + certificateData.head.getCourseVersionUUID + ".jasper"
    val jasperFile: File = new File(fileName)
        
    /*val diff = new Date().getTime - jasperFile.lastModified
    if(diff > 7 * 24 * 60 * 60 * 1000) //delete if older than 7 days
		  jasperFile.delete*/

    if(!jasperFile.exists)
    	FileUtils.copyURLToFile(new URL(composeURL(assetsURL, "certificate.jasper")), jasperFile)
    	
    ReportGenerator.getReportBytes(certificateData, parameters, jasperFile)
    
  }
  
}
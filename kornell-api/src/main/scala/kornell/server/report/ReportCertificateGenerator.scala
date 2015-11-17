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
import kornell.server.util.ServerTime
import java.util.logging.Logger
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTime
import java.util.Date
  
object ReportCertificateGenerator {
  
  val log = Logger.getLogger(getClass.getName)

  implicit def toCertificateInformationTO(rs: ResultSet): CertificateInformationTO = {
    
    log.info("[INFO] Date: " + rs.getString("certifiedAt"))
    log.info("[INFO] Date: " + rs.getTimestamp("certifiedAt"))
    log.info("[INFO] Date: " + rs.getDate("certifiedAt"))
    log.info("[INFO] Date: " + rs.getTimestamp("certifiedAt"))
    log.info("[INFO] Date: " + rs.getTimestamp("certifiedAt").getTime())
    log.info("[INFO] Date: " + ISODateTimeFormat.dateTime.print(rs.getTimestamp("certifiedAt").getTime()))
    
    val dateStr = ServerTime.adjustTimezoneOffsetDate(ISODateTimeFormat.dateTime.print(new DateTime(rs.getTimestamp("certifiedAt").getTime())), 300)
    
    log.info("[INFO] Date Adjusted from: " + dateStr)
    
    
    log.info("[INFO] Date: " + ISODateTimeFormat.dateTime.print(new DateTime(rs.getTimestamp("certifiedAt").getTime())))
    
    TOs.newCertificateInformationTO(
      rs.getString("fullName"),
      rs.getString("cpf"),
      rs.getString("title"),
      rs.getString("name"),
      new Date(rs.getTimestamp("certifiedAt").getTime()),
      rs.getString("assetsURL"),
      rs.getString("distributionPrefix"),
      rs.getString("courseVersionUUID"))
  }
      
   def generateCertificate(userUUID: String, courseClassUUID: String, tsOffset: String): Array[Byte] = {
    generateCertificateReport(sql"""
				select p.fullName, c.title, cc.name, i.assetsURL, cv.distributionPrefix, p.cpf, e.certifiedAt, cv.uuid as courseVersionUUID
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
		    """.map[CertificateInformationTO](toCertificateInformationTO), tsOffset)
  }
  
  def generateCertificate(certificateInformationTOs: List[CertificateInformationTO], tsOffset: String): Array[Byte] = {
    generateCertificateReport(certificateInformationTOs, tsOffset)
  }
  
  def generateCertificateByCourseClass(courseClassUUID: String, tsOffset: String): Array[Byte] = {
    generateCertificateReport(getCertificateInformationTOsByCourseClass(courseClassUUID, null), tsOffset)
  }
  
  def getCertificateInformationTOsByCourseClass(courseClassUUID: String, enrollments: String) = {
    var sql = """select p.fullName, c.title, cc.name, i.assetsURL, cv.distributionPrefix, p.cpf, e.certifiedAt, cv.uuid as courseVersionUUID
      from Person p 
      join Enrollment e on p.uuid = e.person_uuid 
      join CourseClass cc on cc.uuid = e.class_uuid 
      join CourseVersion cv on cv.uuid = cc.courseVersion_uuid 
      join Course c on c.uuid = cv.course_uuid 
      join S3ContentRepository s on s.uuid = cv.repository_uuid 
      join Institution i on i.uuid = cc.institution_uuid 
      where e.certifiedAt is not null and  """ +
		s"""cc.uuid = '$courseClassUUID' """
	if(enrollments != null)
		sql += s"""and e.uuid in ( $enrollments )"""
    if (sql.contains("--")) throw new EntityConflictException("invalidValue")
    val pstmt = new PreparedStmt(sql,List())    
    pstmt.map[CertificateInformationTO](toCertificateInformationTO)
  }

  private def generateCertificateReport(certificateData: List[CertificateInformationTO], tsOffset: String): Array[Byte] = {
    if(certificateData.length == 0){
    	return null
    }
    val parameters: HashMap[String, Object] = new HashMap()
    val assetsURL: String = composeURL(certificateData.head.getAssetsURL, certificateData.head.getDistributionPrefix, "/reports")
    parameters.put("assetsURL", assetsURL + "/")
	  
   
  	//store one jasperfile per course
    val fileName = Settings.tmpDir + "tmp-" + certificateData.head.getCourseVersionUUID + ".jasper"
    println(fileName);
    val jasperFile: File = new File(fileName)
        
    /*val diff = new Date().getTime - jasperFile.lastModified
    if(diff > 7 * 24 * 60 * 60 * 1000) //delete if older than 7 days
		  jasperFile.delete*/

    if(!jasperFile.exists)
    	FileUtils.copyURLToFile(new URL(composeURL(assetsURL, "certificate.jasper")), jasperFile)
    	
    ReportGenerator.getReportBytes(certificateData.map(fixDates(_, tsOffset)), parameters, jasperFile)  
  }
  
  private def fixDates(to: CertificateInformationTO, tsOffset: String) = {
    val dateStr = ServerTime.adjustTimezoneOffsetDate(ISODateTimeFormat.dateTime.print(new DateTime(to.getCourseClassFinishedDate)), tsOffset.toInt)
    val x= new DateTime(to.getCourseClassFinishedDate)
    
    log.info("[INFO] Date Adjusted from: " + ISODateTimeFormat.dateTime.print(new DateTime(to.getCourseClassFinishedDate))  + " to " + dateStr)
    
	//to.setCourseClassFinishedDate(x) 
    to
  }
  
}
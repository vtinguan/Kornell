package kornell.server.report

import java.io.InputStream
import java.sql.ResultSet
import java.util.HashMap
import scala.collection.JavaConverters.seqAsJavaListConverter
import kornell.core.to.report.InstitutionBillingEnrollmentReportTO
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.repository.TOs
import kornell.server.jdbc.repository.InstitutionRepo
import kornell.core.to.report.InstitutionBillingMonthlyReportTO
import kornell.core.entity.BillingType
import kornell.core.entity.Institution

object ReportInstitutionBillingGenerator {
  
  def generateInstitutionBillingReport(institution: Institution, periodStart: String, periodEnd: String): Array[Byte] = {
    val parameters: HashMap[String, Object] = new HashMap()
    parameters.put("institutionName", institution.getName)
    parameters.put("periodStart", periodStart)
    parameters.put("periodEnd", periodEnd)
	    
    institution.getBillingType match {
      case BillingType.monthly => generateInstitutionBillingMonthlyReport(institution.getUUID, periodStart, periodEnd, parameters)
      case BillingType.enrollment => generateInstitutionBillingEnrollmentReport(institution.getUUID, periodStart, periodEnd, parameters)
    }
  }

  private def generateInstitutionBillingMonthlyReport(institutionUUID: String, periodStart: String, periodEnd: String, parameters: HashMap[String,Object]): Array[Byte] = {

	  implicit def toInstitutionBillingMonthlyReportTO(rs: ResultSet): InstitutionBillingMonthlyReportTO =
	    TOs.newInstitutionBillingMonthlyReportTO(
	      rs.getString("personUUID"),
	      rs.getString("fullName"),
	      rs.getString("username"))
	      
    val institutionBillingReportTO = sql"""
    	SELECT
					p.uuid AS 'personUUID', 
					p.fullName AS 'fullName',
					pw.username AS 'username'
				FROM AttendanceSheetSigned att
				JOIN Person p ON p.uuid = att.personUUID
				JOIN Password pw ON pw.person_uuid = p.uuid
				WHERE att.eventFiredAt > ${periodStart} AND att.eventFiredAt < ${periodEnd}
				AND (email IS null OR email NOT LIKE '%craftware.com.br%')
				AND att.institutionUUID = ${institutionUUID} 
				AND (SELECT count(uuid) FROM Enrollment where person_uuid = p.uuid and DATE_FORMAT(enrolledOn, '%Y-%m-%d')< ${periodEnd}) > 0
				GROUP BY att.personUUID
				ORDER BY LOWER(p.fullName)
	    """.map[InstitutionBillingMonthlyReportTO](toInstitutionBillingMonthlyReportTO)
		  
    val cl = Thread.currentThread.getContextClassLoader
    val jasperStream = cl.getResourceAsStream("reports/institutionBillingXLS_monthly.jasper")
    ReportGenerator.getReportBytesFromStream(institutionBillingReportTO, parameters, jasperStream, "xls")
  }

  private def generateInstitutionBillingEnrollmentReport(institutionUUID: String, periodStart: String, periodEnd: String, parameters: HashMap[String,Object]): Array[Byte] = {

	  implicit def toInstitutionBillingEnrollmentReportTO(rs: ResultSet): InstitutionBillingEnrollmentReportTO =
	    TOs.newInstitutionBillingEnrollmentReportTO(
	      rs.getString("enrollmentUUID"),
	      rs.getString("courseTitle"),
	      rs.getString("courseVersionName"),
	      rs.getString("courseClassName"),
	      rs.getString("fullName"),
	      rs.getString("username"))
	      
    val institutionBillingReportTO = sql"""
    	SELECT 
				e.uuid AS 'enrollmentUUID', 
				c.title AS 'courseTitle',
				cv.name AS 'courseVersionName',
				cc.name AS 'courseClassName',
				p.fullName,
				pw.username
			FROM Enrollment e
			JOIN CourseClass cc ON cc.uuid = e.class_uuid
			JOIN CourseVersion cv ON cv.uuid = cc.courseVersion_uuid
			JOIN Course c ON c.uuid = cv.course_uuid
			JOIN Person p ON p.uuid = e.person_uuid
			JOIN Password pw on pw.person_uuid = p.uuid
			WHERE cc.institution_uuid = ${institutionUUID}
			AND (
					(e.lastBilledAt IS NULL
					AND progress IS NOT NULL
					AND e.lastProgressUpdate >= ${periodStart}
					AND e.lastProgressUpdate < ${periodEnd})
				OR (e.lastBilledAt >= ${periodStart}
					AND e.lastBilledAt < ${periodEnd})
				) 
			AND (p.email IS NULL OR p.email NOT LIKE '%@craftware.com.br')
			ORDER BY LOWER(c.title),LOWER(cv.name),LOWER(cc.name), LOWER(p.fullName)
	    """.map[InstitutionBillingEnrollmentReportTO](toInstitutionBillingEnrollmentReportTO)
		  
    val cl = Thread.currentThread.getContextClassLoader
    val jasperStream = cl.getResourceAsStream("reports/institutionBillingXLS_enrollment.jasper")
    ReportGenerator.getReportBytesFromStream(institutionBillingReportTO, parameters, jasperStream, "xls")
  }
 
}
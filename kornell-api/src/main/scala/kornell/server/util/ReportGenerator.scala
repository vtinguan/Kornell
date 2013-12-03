package kornell.server.util

import java.util.HashMap
import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper
import net.sf.jasperreports.engine.JREmptyDataSource
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperRunManager
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL
import java.sql.ResultSet

object ReportGenerator extends App {

  def generateCertificate(userUUID: String, courseClassUUID: String): Array[Byte] = {
    
    type ReportData = Tuple4[String,String, String, String]
    
    implicit def myConvertion(rs:ResultSet):ReportData = (rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4))

    val certificateData = sql"""
			select p.fullName, c.title, i.assetsURL, s.prefix
    		from Person p
			join Enrollment e on p.uuid = e.person_uuid
			join CourseClass cc on cc.uuid = e.class_uuid
    		join CourseVersion cv on cv.uuid = cc.courseVersion_uuid
    		join Course c on c.uuid = cv.course_uuid
			join S3ContentRepository s on s.uuid = cv.repository_uuid
			join Institution i on i.uuid = cc.institution_uuid
			where cc.uuid = $courseClassUUID
			and p.uuid = $userUUID
		""".first[ReportData].get

    val params: HashMap[String, Object] = new HashMap()
    params.put("userUuid", userUUID)
    params.put("name", certificateData._1.toUpperCase())
    params.put("course", certificateData._2.toUpperCase())
    val assetsURL: String = certificateData._3 + certificateData._4 + "/reports/"
    params.put("assetsURL", assetsURL)

    generateEmptyDataSourceReport(assetsURL + "certificate.jrxml", params)
  }

  def generateEmptyDataSourceReport(jrxmlPath: String, params: HashMap[String, Object]): Array[Byte] = {
    val file: File = new File(System.getProperty("java.io.tmpdir") + "tmp-" + params.get("userUuid") + ".jrxml")
    FileUtils.copyURLToFile(new URL(jrxmlPath), file)
    val jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath())
    JasperRunManager.runReportToPdf(jasperReport, params, new JREmptyDataSource())
  }
}
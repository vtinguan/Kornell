package kornell.server.report

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

  def generateCertificate(userUUID: String, courseUUID: String): Array[Byte] = {
    
    type ReportData = Tuple3[String,String,String]
    
    implicit def myConvertion(rs:ResultSet):ReportData = (rs.getString(1), rs.getString(2), rs.getString(3))

    val certificateData = sql"""
			select p.fullName, c.title, c.assetsURL from person p
			join enrollment e on p.uuid = e.person_uuid
			join course c on c.uuid = e.course_uuid
			where c.uuid = $courseUUID
			and p.uuid = $userUUID
		""".first[ReportData].get //a29c36f3-19b8-48fd-a86b-d05bbd260f10/d9aaa03a-f225-48b9-8cc9-15495606ac46

    val params: HashMap[String, Object] = new HashMap()
    params.put("userUuid", userUUID)
    params.put("name", certificateData._1.toUpperCase())
    params.put("course", certificateData._2.toUpperCase())

    val assetsURL: String = certificateData._3

    generateEmptyDataSourceReport(assetsURL + "certificate.jrxml", params)
  }

  def generateEmptyDataSourceReport(jrxmlPath: String, params: HashMap[String, Object]): Array[Byte] = {
    val file: File = new File(System.getProperty("java.io.tmpdir") + "tmp-" + params.get("userUuid") + ".jrxml")
    FileUtils.copyURLToFile(new URL(jrxmlPath), file)
    val jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath())
    JasperRunManager.runReportToPdf(jasperReport, params, new JREmptyDataSource())
  }
}
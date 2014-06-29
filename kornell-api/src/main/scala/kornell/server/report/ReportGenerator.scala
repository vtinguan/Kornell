package kornell.server.report

import java.io.File
import java.net.URL
import java.sql.ResultSet
import java.util.Date
import java.util.HashMap

import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.collection.mutable.ListBuffer

import org.apache.commons.io.FileUtils

import kornell.core.to.report.CertificateInformationTO
import kornell.core.to.report.CourseClassReportTO
import kornell.core.to.report.EnrollmentsBreakdownTO
import kornell.core.util.StringUtils.composeURL
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.repository.TOs
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.JasperRunManager
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.util.JRLoader

object ReportGenerator {

  implicit def toCertificateInformationTO(rs: ResultSet): CertificateInformationTO =
    TOs.newCertificateInformationTO(
      rs.getString("fullName"),
      rs.getString("cpf"),
      rs.getString("title"),
      rs.getDate("certifiedAt"),
      rs.getString("assetsURL"),
      rs.getString("distributionPrefix"))

  implicit def toCourseClassReportTO(rs: ResultSet): CourseClassReportTO =
    TOs.newCourseClassReportTO(
      rs.getString("fullName"),
      rs.getString("username"),
      rs.getString("state"),
      rs.getString("progressState"),
      rs.getInt("progress"))
      
  type BreakdownData = Tuple2[String,Integer] 
  implicit def breakdownConvertion(rs:ResultSet): BreakdownData = (rs.getString(1), rs.getInt(2))
  
  def generateCourseClassReport(courseClassUUID: String): Array[Byte] = {
    val courseClassReportTO = sql"""
				select 
					p.fullName,
					case    
						when p.cpf is null then p.email  
						else p.cpf   
					end as username,
					e.state,
					case    
						when progress is null OR progress = 0 then 'notStarted'  
						when progress > 0 and progress < 100 then 'inProgress'  
						else 'completed'   
					end as progressState,
    			e.progress
				from 
					Enrollment e 
					join Person p on p.uuid = e.person_uuid
				where
					e.state = 'enrolled' and
    		  e.class_uuid = ${courseClassUUID}
				order by 
					progressState,
    			progress,
    			p.fullName
	    """.map[CourseClassReportTO](toCourseClassReportTO)
	    
	    val parameters = getTotalsAsParameters(courseClassUUID)
	    addInfoParameters(courseClassUUID, parameters)
	
	    val enrollmentBreakdowns: ListBuffer[EnrollmentsBreakdownTO] = ListBuffer()
	    enrollmentBreakdowns += TOs.newEnrollmentsBreakdownTO("aa", new Integer(1))
	    enrollmentBreakdowns.toList
		    
	    val jasperFile = getClass.getResource("/reports/courseClassInfo.jasper").getFile()
	    val bytes = getReportBytes(courseClassReportTO, parameters, jasperFile)
	    
	    bytes
  }
      
  type ReportHeaderData = Tuple7[String,String, String, Date, String, String, String]
  implicit def headerDataConvertion(rs:ResultSet): ReportHeaderData = (rs.getString(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getString(5), rs.getString(6), rs.getString(7))
  
  private def addInfoParameters(courseClassUUID: String, parameters: HashMap[String, Object]) = {
    val headerInfo = sql"""
					select 
						i.fullName as 'institutionName',
						c.title as 'courseTitle',
						cc.name as 'courseClassName',
						cc.createdAt,
    				cc.maxEnrollments,
						i.assetsURL,
						(select eventFiredAt from CourseClassStateChanged 
							where toState = 'inactive' and courseClassUUID = cc.uuid
							order by eventFiredAt desc) as disabledAt
					from
						CourseClass cc
						join CourseVersion cv on cc.courseVersion_uuid = cv.uuid
						join Course c on cv.course_uuid = c.uuid
						join Institution i on i.uuid = cc.institution_uuid
					where cc.uuid = ${courseClassUUID}
		    """.first[ReportHeaderData](headerDataConvertion)
    parameters.put("institutionName", headerInfo.get._1)
    parameters.put("courseTitle", headerInfo.get._2)
    parameters.put("courseClassName", headerInfo.get._3)
    parameters.put("createdAt", headerInfo.get._4)
    parameters.put("maxEnrollments", headerInfo.get._5)
    parameters.put("assetsURL", headerInfo.get._6)
    parameters.put("disabledAt", headerInfo.get._7)
    
    println("&&&&&&&&&&&&&&&&&&&&&&&&&&&& "+headerInfo.get._7)
    
    parameters
  }

  private def getTotalsAsParameters(courseClassUUID: String): HashMap[String,Object] = {
    val enrollmentStateBreakdown = sql"""select 
					case    
						when progress is null OR progress = 0 then 'notStarted'  
						when progress > 0 and progress < 100 then 'inProgress'  
						else 'completed'   
					end as progressState,
					count(*) as total
				from 
					Enrollment e 
				where
					e.state = 'enrolled' and
    		  e.class_uuid = ${courseClassUUID}
				group by 
					case    
						when progress is null OR progress = 0 then 'notStarted'  
						when progress > 0 and progress < 100 then 'inProgress'  
						else 'completed'   
					end
		    """.map[BreakdownData](breakdownConvertion)
		    
    val parameters: HashMap[String, Object] = new HashMap()
    enrollmentStateBreakdown.foreach(rd => parameters.put(rd._1, rd._2)) 
    parameters
  }
  
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
    
    
    val diff = new Date().getTime - jasperFile.lastModified
    //if(diff > 1 * 24 * 60 * 60 * 1000) //delete if older than 1 day
    if(jasperFile.exists)
		  jasperFile.delete

    if(!jasperFile.exists)
    	FileUtils.copyURLToFile(new URL(jasperPath), jasperFile)
    	
    getReportBytes(certificateData, parameters, jasperFile)
  }

  private def getReportBytes(certificateData: List[Any], parameters: HashMap[String, Object], jasperFile: File): Array[Byte] =
    runReportToPdf(certificateData, parameters, JRLoader.loadObject(jasperFile).asInstanceOf[JasperReport])

  private def getReportBytes(certificateData: List[Any], parameters: HashMap[String, Object], jasperFile: String): Array[Byte] = 
    JasperRunManager.runReportToPdf(jasperFile, parameters, new JRBeanCollectionDataSource(certificateData asJava))
  
  private def runReportToPdf(certificateData: List[Any], parameters: HashMap[String, Object], jasperReport: JasperReport) = 
    JasperRunManager.runReportToPdf(jasperReport, parameters, new JRBeanCollectionDataSource(certificateData asJava))
  
}
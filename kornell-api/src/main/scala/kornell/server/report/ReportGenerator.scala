package kornell.server.report

import java.io.File
import java.io.InputStream
import java.util.HashMap

import scala.collection.JavaConverters.seqAsJavaListConverter

import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.JasperRunManager
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.util.JRLoader 


object ReportGenerator {

  def getReportBytes(certificateData: List[Any], parameters: HashMap[String, Object], jasperFile: File): Array[Byte] =
    runReportToPdf(certificateData, parameters, JRLoader.loadObject(jasperFile).asInstanceOf[JasperReport])

  def getReportBytesFromStream(certificateData: List[Any], parameters: HashMap[String, Object], jasperStream: InputStream): Array[Byte] =
    getReportBytesFromStream(certificateData, parameters, jasperStream, "pdf")

  def getReportBytesFromStream(certificateData: List[Any], parameters: HashMap[String, Object], jasperStream: InputStream, fileType: String): Array[Byte] =
    runReportToPdf(certificateData, parameters, JRLoader.loadObject(jasperStream).asInstanceOf[JasperReport])

  def getReportBytes(certificateData: List[Any], parameters: HashMap[String, Object], jasperFile: String): Array[Byte] = 
    JasperRunManager.runReportToPdf(jasperFile, parameters, new JRBeanCollectionDataSource(certificateData asJava))
  
  def runReportToPdf(certificateData: List[Any], parameters: HashMap[String, Object], jasperReport: JasperReport) = 
    JasperRunManager.runReportToPdf(jasperReport, parameters, new JRBeanCollectionDataSource(certificateData asJava))
    
  def clearJasperFiles = { 
    val folder = new File(System.getProperty("java.io.tmpdir"))
    folder.listFiles().foreach(file => 
		    if (file.getName().endsWith(".jasper")) 
		        file.delete()
    ) 
  }
    
}
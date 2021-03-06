package kornell.server.report

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.HashMap
import scala.collection.JavaConverters.seqAsJavaListConverter
import net.sf.jasperreports.engine.JRExporterParameter
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.JasperRunManager
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter
import net.sf.jasperreports.engine.export.JRXlsExporter
import net.sf.jasperreports.engine.util.JRLoader
import net.sf.jasperreports.engine.export.JRXlsExporterParameter
import kornell.core.util.UUID
import java.io.FileInputStream
import kornell.server.util.Settings


object ReportGenerator {

  def getReportBytes(certificateData: List[Any], parameters: HashMap[String, Object], jasperFile: File): Array[Byte] =
    runReportToPdf(certificateData, parameters, JRLoader.loadObject(jasperFile).asInstanceOf[JasperReport], "pdf")
    
  def getReportBytes(certificateData: List[Any], parameters: HashMap[String, Object], jasperFile: File, fileType: String): Array[Byte] =
    runReportToPdf(certificateData, parameters, JRLoader.loadObject(jasperFile).asInstanceOf[JasperReport], fileType)

  def getReportBytesFromStream(certificateData: List[Any], parameters: HashMap[String, Object], jasperStream: InputStream): Array[Byte] =
    getReportBytesFromStream(certificateData, parameters, jasperStream, "pdf")

  def getReportBytesFromStream(certificateData: List[Any], parameters: HashMap[String, Object], jasperStream: InputStream, fileType: String): Array[Byte] =
    runReportToPdf(certificateData, parameters, JRLoader.loadObject(jasperStream).asInstanceOf[JasperReport], fileType)

  def getReportBytes(certificateData: List[Any], parameters: HashMap[String, Object], jasperFile: String): Array[Byte] = 
    JasperRunManager.runReportToPdf(jasperFile, parameters, new JRBeanCollectionDataSource(certificateData asJava))
  
  def runReportToPdf(certificateData: List[Any], parameters: HashMap[String, Object], jasperReport: JasperReport, fileType: String) =
    if(fileType != null && fileType == "xls"){
      val fileName = Settings.tmpDir + "tmp-" + UUID.random + ".xls"
      val exporterXLS = new JRXlsExporter()
      val jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JRBeanCollectionDataSource(certificateData asJava))
      exporterXLS.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint)
      exporterXLS.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, fileName)
      exporterXLS.exportReport()
      val source = scala.io.Source.fromFile(fileName)(scala.io.Codec.ISO8859)
      val byteArray = source.map(_.toByte).toArray
      source.close()
      byteArray
    }
    else
    	JasperRunManager.runReportToPdf(jasperReport, parameters, new JRBeanCollectionDataSource(certificateData asJava))
    
  def clearJasperFiles = { 
    val folder = new File(Settings.tmpDir)
    var deleted = "Deleting files from folder " + folder.getAbsolutePath +": "
    folder.listFiles().foreach(file => 
		    if (file.getName.endsWith(".jasper") || file.getName.endsWith(".xls")) {
		        file.delete()
		        deleted += file.getName + ", "
		    }
    ) 
    deleted
  }
    
}
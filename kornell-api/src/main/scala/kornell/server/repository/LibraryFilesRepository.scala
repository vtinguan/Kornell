package kornell.server.repository

import kornell.server.dev.util.ContentsParser
import kornell.server.repository.s3.S3
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.core.entity.Person
import javax.xml.parsers.DocumentBuilderFactory
import scala.collection.mutable.ListBuffer
import javax.xml.xpath.XPathFactory
import org.w3c.dom.NodeList
import javax.xml.xpath.XPathConstants
import java.io.ByteArrayInputStream
import kornell.server.dev.util.LibraryFilesParser
import kornell.core.util.StringUtils
import scala.util.Try
import javax.inject.Inject
import kornell.server.content.ContentManagers
import kornell.server.jdbc.repository.CourseClassRepo

class LibraryFilesRepository @Inject()(
  cms:ContentManagers,
  courseClassesRepo:CourseClassesRepo
	) {
  //TODO: Review
  def findLibraryFiles(courseClassUUID: String) =  {
    val classRepo = courseClassesRepo.byUUID(courseClassUUID)
    val versionRepo = classRepo.version
    val version = versionRepo.get
    val cm = cms.forCourseVersion(version)
    val filesURL = StringUtils.composeURL("library","libraryFiles.knl")
    val structureSrc = cm.source(filesURL).get
    val libraryFilesText = structureSrc.mkString("")
    val fullURL = StringUtils.composeURL(cm.baseURL, "/TODO-REFACT-JULIO-findLibraryFiles", version.getDistributionPrefix(), "library")
    val contents = LibraryFilesParser.parse(fullURL, libraryFilesText)
    contents
  }
}

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

object LibraryFilesRepository {

  def findLibraryFiles(courseClassUUID: String) = {
    val classRepo = CourseClassesRepo(courseClassUUID)
    val versionRepo = classRepo.version
    val version = versionRepo.get
    val repositoryUUID = version.getRepositoryUUID();
    val repo = S3(repositoryUUID)
    val filesURL = StringUtils.composeURL(version.getDistributionPrefix(), "library")
    val structureSrc = repo.source(filesURL, "libraryFiles.knl")
    val libraryFilesText = structureSrc.mkString("")
    val fullURL = StringUtils.composeURL(repo.baseURL, repo.prefix, version.getDistributionPrefix(), "library")
    val contents = LibraryFilesParser.parse(fullURL, libraryFilesText)
    contents
  }
}
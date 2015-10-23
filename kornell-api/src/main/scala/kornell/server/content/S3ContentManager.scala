package kornell.server.content

import kornell.core.entity.S3ContentRepository
import com.amazonaws.services.s3.AmazonS3Client
import scala.util.Try
import java.util.logging.Logger
import kornell.core.util.StringUtils._
import scala.io.Source
import java.io.InputStream
import com.amazonaws.services.s3.model.ObjectMetadata
import scala.collection.JavaConverters._

class S3ContentManager(s3: AmazonS3Client,repo: S3ContentRepository)
  extends SyncContentManager {
  val logger = Logger.getLogger(classOf[S3ContentManager].getName)

  def source(infix: String, key: String) =
    inputStream(infix, key).map { Source.fromInputStream(_, "UTF-8") }
  
  def url(segments:String*):String = composeURL(repo.getPrefix, segments:_*) 

  def inputStream(infix: String, key: String): Try[InputStream] = Try {
    logger.finest(s"loading inputStream(${infix}, ${key})")
    val fqkn = url(infix, key)
    try {
      s3.getObject(repo.getBucketName, fqkn).getObjectContent
    } catch {
      case e: Throwable => {
        val cmd = s"aws s3api get-object --bucket ${repo.getBucketName} --key ${fqkn} --region ${repo.getRegion} file.out"
        logger.warning("Could not load object. Try [" + cmd + "]")
        throw e
      }
    }
  }
  
  def put(key: String, value: InputStream, contentType: String, contentDisposition: String, metadataMap: Map[String, String]) = {
    val metadata = new ObjectMetadata()
    metadata.setUserMetadata(metadataMap asJava)
    Option(contentType).foreach { metadata.setContentType(_) }
    Option(contentDisposition).foreach { metadata.setContentDisposition(_) }
    s3.putObject(repo.getBucketName(), url(key), value, metadata)
  }

}
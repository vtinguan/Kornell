package kornell.server.content

import kornell.core.entity.ContentStore
import com.amazonaws.services.s3.AmazonS3Client
import kornell.core.util.StringUtils._
import org.apache.http.impl.client.BasicCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import scala.io.Source
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import java.io.InputStream
import java.util.logging.Logger
import scala.util.Try

class S3ContentManager(cs: ContentStore,distributionPrefix:String) extends ContentManager {
  val log = Logger.getLogger(classOf[S3ContentManager].getClass.getName)
  val props = cs.getProperties()
  val distributionURL = props.get("distributionURL")
  val bucket = props.get("bucketName")
  val prefix = props.get("prefix")
  val fullPrefix = composeURL(prefix,distributionPrefix)

  override def getID() = cs.getUUID()+"/"+distributionPrefix
  
  val s3 = {
    val accessKeyId = props.get("accessKeyId")
    val creds = Option(accessKeyId).map { ak => 
      val sk = props.get("secretAccessKey")
      new BasicAWSCredentials(ak,sk)
    }
    val s3 = creds match {
      case Some(creds) => new AmazonS3Client(creds)
      case None => ??? // new AmazonS3Client
    }
    Option(props.get("region")) foreach { region =>
      s3.setRegion(Region.getRegion(Regions.fromName(region)))
    }    
    s3
  }
  
  override def getURL(obj:String) = composeURL(fullPrefix,obj) 

  override def getObjectStream(obj: String): Try[InputStream] = Try {
    val key = composeURL(fullPrefix, obj)
    log.finest(s"Fetching object [s3://$bucket/$key]")
    s3.getObject(bucket, key).getObjectContent
  }
  
  override def baseURL = composeURL(prefix)
}
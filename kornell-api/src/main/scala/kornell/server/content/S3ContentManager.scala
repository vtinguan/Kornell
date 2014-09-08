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

class S3ContentManager(cs: ContentStore) extends ContentManager {
  val props = cs.getProperties()
  val distributionURL = props.get("distributionURL")
  val bucket = props.get("bucketName")
  val prefix = props.get("prefix")
  val distributionPrefix = props.get("distributionPrefix")
  val fullPrefix = composeURL(prefix,distributionPrefix)

  val s3 = {
    val accessKeyId = props.get("accessKeyId")
    val creds = Option(accessKeyId).map { ak => 
      val sk = props.get("secretAccessKey")
      new BasicAWSCredentials(ak,sk)
    }
    val s3 = creds match {
      case Some(creds) => new AmazonS3Client(creds)
      case None => new AmazonS3Client
    }
    Option(props.get("region")) foreach { region =>
      s3.setRegion(Region.getRegion(Regions.fromName(region)))
    }    
    s3
  }
  
  override def getPath(obj:String) = composeURL(fullPrefix,obj) 

  override def getObjectStream(obj: String): InputStream = 
    s3.getObject(bucket, composeURL(fullPrefix, obj)).getObjectContent
  
}
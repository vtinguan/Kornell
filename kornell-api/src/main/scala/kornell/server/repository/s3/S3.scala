package kornell.server.repository.s3

import java.io.ByteArrayInputStream
import java.sql.ResultSet
import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.immutable.Stream.consWrapper
import scala.io.Source
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectListing
import kornell.server.jdbc.SQL.SQLHelper
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider
import kornell.server.util.Settings
import com.amazonaws.services.s3.model.ObjectMetadata
import scala.collection.JavaConverters._
import java.io.InputStream
import kornell.core.util.StringUtils._
class S3(regionName: String,
  val accessKey: String,
  val secretKey: String,
  val bucket: String,
  val prefix: String,
  distributionURL: String) {

  //TODO: An actom is an undivisible unit of learning content. Pls write that on the wiki, bur for now it is just a key...
  type Actom = String

  val region = Region.getRegion(Regions.fromName(regionName))
  val creds = if(accessKey != null)
    new BasicAWSCredentials(accessKey, secretKey)
  else 
	new ClasspathPropertiesFileCredentialsProvider().getCredentials()
  
  val s3 = new AmazonS3Client(creds)
  lazy val client = s3

  s3.setRegion(region)

  lazy val first: ObjectListing = s3.listObjects(bucket, prefix)
  def next(prev: ObjectListing): ObjectListing = s3.listNextBatchOfObjects(prev)

  def isActom(key: String) = key.endsWith("html")

  lazy val listings: Stream[ObjectListing] = first #::
    listings.map(next)
    .takeWhile(!_.getObjectSummaries.isEmpty)

  lazy val keys = listings
    .flatten(_.getObjectSummaries.asScala)
    .map(_.getKey)

  lazy val actoms: Stream[Actom] = keys.filter(isActom)

  def put(key: String, value: String) =
    s3.putObject(bucket, composeURL(prefix,key), new ByteArrayInputStream(value.getBytes()), null)

  def put (key:String, value:InputStream, contentType:String, contentDisposition:String, metadataMap:Map[String,String] ) = {
    val metadata = new ObjectMetadata()
    metadata.setUserMetadata(metadataMap asJava)
    Option(contentType).foreach { metadata.setContentType(_) }
    Option(contentDisposition).foreach { metadata.setContentDisposition(_) }
    s3.putObject(bucket,composeURL(prefix,key), value, metadata)    
  }
    
  def getObject(key: String) =
    s3.getObject(bucket, prefix + "/" + key)

  def source(ditributionPrefix: String, key: String) = Source.fromURL(url(ditributionPrefix, key), "utf-8")

  def url(ditributionPrefix: String, key: String) = composeURL(baseURL, prefix, ditributionPrefix, key)

  lazy val baseURL = distributionURL
  

}

object S3 {
  val DEFAULT_USER_CONTENT_BUCKET = "usercontent.test.eduvem.com";
  val USER_CONTENT_BUCKET = Settings.get("USER_CONTENT_BUCKET").getOrElse(DEFAULT_USER_CONTENT_BUCKET)
  
  val certificates = new S3("sa-east-1",
      null,null,
      USER_CONTENT_BUCKET,
      "userconent/certificates",null)
  
  implicit def toS3(rs: ResultSet) = new S3(
    rs.getString("region"),
    rs.getString("accessKeyId"), rs.getString("secretAccessKey"),
    rs.getString("bucketName"), rs.getString("prefix"),
    rs.getString("distributionURL"))

  def apply(repository_uuid: String) =
    sql"""
    	select region,accessKeyId,secretAccessKey,bucketName,prefix,distributionURL
    	from S3ContentRepository
    	where uuid=$repository_uuid
    """.first[S3].getOrElse({ throw new IllegalArgumentException(s"Could not find repository [$repository_uuid]") })

}
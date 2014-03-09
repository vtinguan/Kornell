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

import kornell.core.util.StringUtils.composeURL
import kornell.server.jdbc.SQL.SQLHelper

//TODO: Change to HTTP
class S3(regionName: String,
  val accessKey: String,
  val secretKey: String,
  val bucket: String,
  val prefix: String,
  distributionURL: String) {

  //TODO: An actom is an undivisible unit of learning content. Pls write that on the wiki, bur for now it is just a key...
  type Actom = String

  val region = Region.getRegion(Regions.fromName(regionName))
  val creds = new BasicAWSCredentials(accessKey, secretKey)
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
    s3.putObject(bucket, prefix + "/" + key, new ByteArrayInputStream(value.getBytes()), null)

  def getObject(key: String) =
    s3.getObject(bucket, prefix + "/" + key)

  def source(ditributionPrefix: String, key: String) = Source.fromURL(url(ditributionPrefix, key), "utf-8")

  def url(ditributionPrefix: String, key: String) = composeURL(baseURL, prefix, ditributionPrefix, key)

  //TODO: Resolve base url from region
  //TODO: Stinking (set and/or get)
  lazy val baseURL = {
    if(distributionURL == null)
    	"https://s3-" + regionName + ".amazonaws.com/" + bucket + "/"
    else
    	distributionURL
  }

}

object S3 {
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
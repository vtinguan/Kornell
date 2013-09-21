package kornell.server.dev

import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider
import scala.collection.JavaConverters._
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest
import com.amazonaws.services.sqs.model.GetQueueUrlRequest

object Sandbox extends App {
  val sqs = new AmazonSQSClient(new ClasspathPropertiesFileCredentialsProvider)

  val region = Region.getRegion(Regions.fromName("sa-east-1"))

  sqs.setRegion(region)
  val q = sqs.getQueueUrl(new GetQueueUrlRequest().withQueueName("midway-sync-q")).getQueueUrl()
  println(q)
}
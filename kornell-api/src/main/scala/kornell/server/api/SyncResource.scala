package kornell.server.api

import javax.ws.rs._
import javax.ws.rs.core._
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider
import com.amazonaws.services.sqs.model.SendMessageRequest
import com.amazonaws.services.sqs.model.GetQueueUrlRequest
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import scala.collection.JavaConverters._

@Path("/sync")
class SyncResource {

  @POST
  @Path("{regionName}/{queueName}")
  def postCommit(@PathParam("regionName") regionName: String,
    @PathParam("queueName") queueName: String,
    @QueryParam("payload") payload: String) = {
    val sqs = new AmazonSQSClient(new ClasspathPropertiesFileCredentialsProvider)
    val region = Region.getRegion(Regions.fromName(regionName))
    sqs.setRegion(region) 
    val queueUrl = sqs
      .getQueueUrl(new GetQueueUrlRequest().withQueueName(queueName))
      .getQueueUrl

    sqs.sendMessage(new SendMessageRequest()
      .withQueueUrl(queueUrl)
      .withMessageBody(payload))
      .getMessageId()
  }

}
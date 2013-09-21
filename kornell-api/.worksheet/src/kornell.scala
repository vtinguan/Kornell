import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider
import scala.collection.JavaConverters._
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.regions.Regions
import com.amazonaws.regions.Region
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest

object kornell {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(444); 
  val sqs = new AmazonSQSClient(new ClasspathPropertiesFileCredentialsProvider);System.out.println("""sqs  : com.amazonaws.services.sqs.AmazonSQSClient = """ + $show(sqs ));$skip(63); 
  val region = Region.getRegion(Regions.fromName("sa-east-1"));System.out.println("""region  : com.amazonaws.regions.Region = """ + $show(region ));$skip(24); 
  sqs.setRegion(region);$skip(49); 
  val qs = sqs.listQueues.getQueueUrls() asScala;System.out.println("""qs  : scala.collection.mutable.Buffer[String] = """ + $show(qs ));$skip(217); 
  
  qs.foreach { url =>
    println(url)
		val atts = sqs.getQueueAttributes(new GetQueueAttributesRequest().withQueueUrl(url)).getAttributes().asScala
		atts.foreach {case (key,value) => println(key+"="+value)}
  }}
}

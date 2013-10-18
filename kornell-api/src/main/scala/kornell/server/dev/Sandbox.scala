package kornell.server.dev

import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider
import scala.collection.JavaConverters._
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest
import com.amazonaws.services.sqs.model.GetQueueUrlRequest
import kornell.core.shared.data.Content
import scala.collection.mutable.ListBuffer
import kornell.core.shared.data.Topic
import scala.collection.JavaConverters._
import kornell.core.shared.data.ExternalPage
import kornell.server.repository.Beans
import kornell.server.dev.util.ContentsParser



object Sandbox extends App {
  val source =
    """
# Titulo 1
Pagina 1.1
Pagina 1.2
Pagina 1.3
# Titulo 2
Pagina 2.1
Pagina 2.2
Pagina 2.3    
""";

  val cs = ContentsParser.parse("",source);

  def printPage(p: ExternalPage) = {
    println("[" ++ p.getKey() + "] " + p.getTitle() + "")
  }

  def printTopic(t: Topic): Unit = {
    println(t.getName())
    (t.getChildren() asScala) foreach printContent
  }

  def printContent(c: Content) = c match {
    case c: Topic => printTopic(c)
    case c: ExternalPage => printPage(c)
    case _ => println("Opps")
  }

  println(cs.getChildren().size())
  (cs.getChildren() asScala) foreach printContent

}
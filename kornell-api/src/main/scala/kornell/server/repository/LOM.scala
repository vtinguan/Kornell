package kornell.server.repository

import kornell.core.lom.LOMFactory
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.lom.ExternalPage
import kornell.core.lom.Topic
import kornell.core.lom.Content
import kornell.core.lom.ContentFormat
import java.util.ArrayList
import kornell.core.util.StringUtils._
import scala.collection.JavaConverters._

object LOM {
  val factory = AutoBeanFactorySource.create(classOf[LOMFactory])

  def newTopic(name: String = "") = {
    val topic = factory.newTopic.as    
    topic.setName(name)
    topic.setChildren(new ArrayList)
    topic
  }
  
  def newContent(topic:Topic) = {
    val content = factory.newContent.as    
    content.setFormat(ContentFormat.Topic);
    content.setTopic(topic)
    content
  }

  def newExternalPage(baseURL:String ="",
		  			  prefix:String = "",
		  			  index: String = "", 
		  			  key: String = "", 
		  			  title: String = "") = {
    val page = factory.newExternalPage.as
    page.setTitle(title)
    try{
    	page.setIndex(index.toInt)
    }catch {
      case e: NumberFormatException => page.setIndex(-666)
    }
    
    page.setKey(key)
    page.setURL(composeURL(baseURL,prefix,key))
    page    
  }
  
  def newContent(page:ExternalPage) = {
    val content = factory.newContent.as    
    content.setFormat(ContentFormat.ExternalPage);
    content.setExternalPage(page)
    content
  }

  def newContents(children: List[Content] = List()) = {
    val contents = factory.newContents.as
    contents.setChildren(children asJava)
    contents
  }
  
}
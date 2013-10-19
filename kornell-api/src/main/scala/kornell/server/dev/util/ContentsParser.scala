package kornell.server.dev.util

import scala.collection.mutable.ListBuffer
import kornell.core.shared.data.Topic
import kornell.core.shared.data.Content
import kornell.server.repository.Beans
import kornell.core.shared.data.Contents
import scala.io.Source
import scala.io.BufferedSource
import kornell.core.shared.util.StringUtils._

object ContentsParser {
  
  val topicPattern = """#\s?(.*)""".r
  val pagePattern = """([^;]*);?([^;]*)?""".r 

  def parse(baseURL:String,source: BufferedSource ): Contents =
    parseLines(baseURL,source.getLines)
  def parse(baseURL:String,source: String): Contents = 
    parseLines(baseURL,source.lines) 
  
  def parseLines(baseURL:String,lines:Iterator[String]) = {
    val result = ListBuffer[Content]()
    var topic: Topic = null
    lines foreach { line =>
      line match {
        case topicPattern(topicName) => {
          topic = Beans.newTopic(topicName)
          result += Beans.newContent(topic) 
        }
        
        case pagePattern(key,title) => { 
          val page = Beans.newExternalPage(baseURL,key,title)
          val content = Beans.newContent(page)
          if (topic != null) topic.getChildren().add(content)
          else result += content
        }
        
      }
    }
    val contents = result.toList
    Beans.newContents(contents)
  }
}
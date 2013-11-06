package kornell.server.dev.util

import scala.collection.mutable.ListBuffer
import kornell.core.lom.Topic
import kornell.core.lom.Content
import kornell.server.repository.Entities
import kornell.core.lom.Contents
import scala.io.Source
import scala.io.BufferedSource
import kornell.core.util.StringUtils._
import kornell.server.repository.LOM

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
          topic = LOM.newTopic(topicName)
          result += LOM.newContent(topic) 
        }
        
        case pagePattern(key,title) => { 
          val page = LOM.newExternalPage(baseURL,key,title)
          val content = LOM.newContent(page)
          if (topic != null) topic.getChildren().add(content)
          else result += content
        }
        
      }
    }
    val contents = result.toList
    LOM.newContents(contents)
  }
}
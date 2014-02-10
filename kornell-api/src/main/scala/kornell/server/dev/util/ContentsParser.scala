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
  val pagePattern = """(([^;.]*).?([^;]*));?([^;]*)?""".r 

  def parse(baseURL:String,prefix:String,source: String,visited:List[String]): Contents =
    parseLines(baseURL,prefix,source.lines,visited)
    
  
  def parseLines(baseURL:String,prefix:String,lines:Iterator[String],visited:List[String]) = {
    val result = ListBuffer[Content]()
    var topic: Topic = null
    lines foreach { line =>
      line match {
        case topicPattern(topicName) => {
          topic = LOM.newTopic(topicName)
          result += LOM.newContent(topic) 
        }
        
        case pagePattern(key, index, extension, title) => {
          val page = LOM.newExternalPage(baseURL,prefix,index,key,title)
          page.setVisited(visited.contains(page.getKey()))
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
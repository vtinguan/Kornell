package kornell.server.dev.util

import scala.collection.mutable.ListBuffer
import kornell.core.shared.data.Topic
import kornell.core.shared.data.Content
import kornell.server.repository.Beans
import kornell.core.shared.data.Contents
import scala.io.Source
import scala.io.BufferedSource

object ContentsParser {
  val topicPattern = """#\s?(.*)""".r

  def parse(source: BufferedSource ): Contents =
    parseLines(source.getLines)
  def parse(source: String): Contents = 
    parseLines(source.lines) 
  
  def parseLines(lines:Iterator[String]) = {
    val result = ListBuffer[Content]()
    var topic: Topic = null
    lines foreach { line =>
      line match {
        case topicPattern(topicName) => {
          topic = Beans.newTopic(topicName)
          result += Beans.newContent(topic) 
        }
        case _ => { 
          val page = Beans.newExternalPage(line)
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
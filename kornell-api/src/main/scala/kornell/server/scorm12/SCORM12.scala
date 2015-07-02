package kornell.server.scorm12

import kornell.core.scorm12.rte.DMElement
import kornell.core.scorm12.rte.RTE
import java.util.{ Map => JMap }
import scala.collection.mutable.{ Map => MMap }
import scala.collection.JavaConverters._
import java.util.logging.Logger
import java.util.HashMap
import kornell.core.scorm12._
import kornell.core.entity.Person

object SCORM12 { 
  
  implicit class Element(el: DMElement) {
    def initialize(entries: JMap[String, String],person:Person): JMap[String, String] ={
      val launched = _initialize(entries,person).asJava      
      val maps = List(entries,launched) 
      val result = merged(maps)
      result.asJava 
    }

    def _initialize(entries: JMap[String, String],person:Person): MMap[String, String] = {
      type Maps = List[JMap[String,String]]      
      val kids:Maps   = el.getChildren().asScala
                        .map(c => c.initialize(entries,person))
                        .toList
      val selfie:Maps = List(el.initializeMap(entries,person))
      val maps = kids ++ selfie
      val result = merged(maps)
      result
    }
  }

  val dataModel: Element = RTE.root

  

}
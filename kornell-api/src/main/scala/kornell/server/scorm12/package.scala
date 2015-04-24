package kornell.server

import scala.collection.mutable.{ Map => MMap }
import java.util.{ Map => JMap }
import scala.collection.JavaConverters._
import java.util.logging.Logger
import kornell.core.scorm12.rte.DMElement
import kornell.server.scorm12.MergeMap

package object scorm12 {
  val logger = Logger.getLogger("kornell.server.scorm12");  
  implicit def merged(mms: Seq[JMap[String, String]]) = new MergeMap(mms).merged 
}
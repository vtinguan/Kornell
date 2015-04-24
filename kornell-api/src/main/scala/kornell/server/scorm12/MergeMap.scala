package kornell.server.scorm12

import scala.collection.mutable.{ Map => MMap }
import java.util.{ Map => JMap }
import scala.collection.JavaConverters._
import java.util.logging.Logger
import kornell.core.scorm12.rte.DMElement

class MergeMap(mms: Seq[JMap[String, String]]) {
  //Shameless copy: http://stackoverflow.com/questions/1262741/scala-how-to-merge-a-collection-of-maps
  //val ms = List(Map("hello" -> 1.1, "world" -> 2.2), Map("goodbye" -> 3.3, "hello" -> 4.4))
  //val mm = mergeMap(ms)((v1, v2) => v1 + v2)
  //println(mm) // prints Map(hello -> 5.5, world -> 2.2, goodbye -> 3.3)
  def merged(ms: Seq[JMap[String, String]])(f: (String, String) => String): MMap[String, String] =
    (MMap[String, String]() /: (for (m <- ms; kv <- m.asScala) yield kv)) { (a, kv) =>
      a + (if (a.contains(kv._1)) kv._1 -> f(a(kv._1), kv._2) else kv)
    }

  def merged(): MMap[String, String] = merged(mms) { (a, b) =>
    logger.warning(s"Siblings conflicted for key, arbitrarily picked [$b]");
    b
  }
}

package kornell.server.ws.rs

import javax.ws.rs.ext.MessageBodyReader
import java.lang.reflect.Type
import java.lang.annotation.Annotation
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import java.io.InputStream
import javax.ws.rs.core.MultivaluedMap
import com.google.web.bindery.autobean.shared.AutoBeanUtils
import com.google.web.bindery.autobean.shared.AutoBeanCodex
import kornell.core.shared.event.EventFactory
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import scala.io.Source

class EventsReader extends MessageBodyReader[Any] {
  val eventFactory = AutoBeanFactorySource.create(classOf[EventFactory])

  override def isReadable(
    arg0: Class[_],
    arg1: Type,
    arg2: Array[Annotation],
    mediaType: MediaType): Boolean = 
      mediaType.toString.startsWith("application/vnd.kornell.v1.event.")

  override def readFrom(
    clazz: Class[Any],
    arg1: Type,
    arg2: Array[Annotation],
    arg3: MediaType,
    arg4: MultivaluedMap[String, String],
    in: InputStream):Any = {
      val text = Source.fromInputStream(in).getLines().mkString("")
      val bean = AutoBeanCodex.decode(eventFactory, clazz, text)
      bean.as
  }
}
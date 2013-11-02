package kornell.server.ws.rs.reader

import javax.ws.rs.ext.MessageBodyReader
import com.google.web.bindery.autobean.shared.AutoBeanFactory
import com.google.web.bindery.autobean.shared.AutoBeanCodex
import java.lang.annotation.Annotation
import java.io.InputStream
import javax.ws.rs.core.MediaType
import scala.io.Source
import javax.ws.rs.core.MultivaluedMap
import java.lang.reflect.Type

trait AutoBeanReader extends MessageBodyReader[Any] {
  def getAutoBeanFactory: AutoBeanFactory
  def getTypePrefix: String

  override def isReadable(
    arg0: Class[_],
    arg1: Type,
    arg2: Array[Annotation],
    mediaType: MediaType): Boolean =
    mediaType.toString.startsWith(getTypePrefix)

  override def readFrom(
    clazz: Class[Any],
    arg1: Type,
    arg2: Array[Annotation],
    arg3: MediaType,
    arg4: MultivaluedMap[String, String],
    in: InputStream): Any = {
    val text = Source.fromInputStream(in).getLines().mkString("")
    val bean = AutoBeanCodex.decode(getAutoBeanFactory, clazz, text)
    bean.as
  }

}
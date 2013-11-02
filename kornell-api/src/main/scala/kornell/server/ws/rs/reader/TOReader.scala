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
import kornell.core.event.EventFactory
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import scala.io.Source
import kornell.core.to.TOFactory
import javax.ws.rs.ext.Provider
import kornell.server.ws.rs.reader.AutoBeanReader

@Provider
class TOReader extends AutoBeanReader {
  val factory = AutoBeanFactorySource.create(classOf[TOFactory])

  override def getTypePrefix = TOFactory.PREFIX
  override def getAutoBeanFactory = factory
}
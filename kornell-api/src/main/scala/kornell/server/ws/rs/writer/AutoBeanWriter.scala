package kornell.server.ws.rs

import java.lang.Class
import com.google.web.bindery.autobean.shared.AutoBean
import com.google.web.bindery.autobean.shared.AutoBeanCodex
import com.google.web.bindery.autobean.shared.AutoBeanUtils
import javax.ws.rs.ext.Provider
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.ext.MessageBodyWriter
import kornell.server.util.Conditional._
import java.io.OutputStream
import kornell.server.util.Passed
import kornell.server.util.Failed
import kornell.server.util.Err
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response

@Provider
class AutoBeanWriter extends MessageBodyWriter[Any] {
  override def getSize(t: Any,
    aType: java.lang.Class[_],
    genericType: java.lang.reflect.Type,
    annotations: Array[java.lang.annotation.Annotation],
    mediaType: MediaType) = -1L

  override def isWriteable(aType: java.lang.Class[_],
    genericType: java.lang.reflect.Type,
    annotations: Array[java.lang.annotation.Annotation],
    mediaType: MediaType) = mediaType.toString().contains("vnd.kornell");

  override def writeTo(t: Any,
    aType: java.lang.Class[_],
    genericType: java.lang.reflect.Type,
    annotations: Array[java.lang.annotation.Annotation],
    mediaType: MediaType,
    httpHeaders: MultivaluedMap[java.lang.String, java.lang.Object],
    out: java.io.OutputStream) {
    println("Outputting  "+ t)
    t match {
      case Some(thing) => { println ("SOME"); outputPayload(thing,out) }
      case Passed(block) => { println ("PASSED"); outputPayload(block,out) }
      case Failed(err) => { println ("FAILED"); spitErr(err) }
      case _ => { println ("OTHER"); outputPayload(t,out)}
    }

    println("EOF")
  }
  
  def spitErr(e:Err) = {
    val response = Response.status(Response.Status.BAD_REQUEST).build();
    throw new WebApplicationException(response);
  } 

  private def outputPayload(content: Any,out:OutputStream) = {    
    val bean = AutoBeanUtils.getAutoBean(content)
    val payload = AutoBeanCodex.encode(bean).getPayload
    println("Payloading "+content +" = " +payload)
    out.write(payload.getBytes)
  }
}
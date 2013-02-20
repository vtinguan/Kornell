package kornell.ws.rs

import javax.ws.rs.ext.MessageBodyWriter
import javax.ws.rs.ext.Provider
import com.google.web.bindery.autobean.shared.AutoBean
import java.lang.Class
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import com.google.web.bindery.autobean.shared.AutoBeanUtils
import com.google.web.bindery.autobean.shared.AutoBeanCodex

@Provider
class AutoBeanWriter extends MessageBodyWriter[Any]{
     override def getSize(t:Any,
             aType:java.lang.Class[_],
             genericType:java.lang.reflect.Type,
             annotations:Array[java.lang.annotation.Annotation],
             mediaType:MediaType) 
	 = -1L
	 
	 override def isWriteable(aType:java.lang.Class[_],
                    genericType:java.lang.reflect.Type,
                    annotations:Array[java.lang.annotation.Annotation],
                    mediaType:MediaType) 
	 = true
     
    override def writeTo(t:Any,
             aType:java.lang.Class[_],
             genericType:java.lang.reflect.Type,
             annotations:Array[java.lang.annotation.Annotation],
             mediaType:MediaType,
             httpHeaders:MultivaluedMap[java.lang.String,java.lang.Object],
             out:java.io.OutputStream) {
    	 val bean = AutoBeanUtils.getAutoBean(t);
    	 val payload = AutoBeanCodex.encode(bean).getPayload();
    	 out.write(payload.getBytes())
     }
}
package kornell.server.ws.rs.exception

import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider
import java.io.FileNotFoundException
import javax.ws.rs.core.Response
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.error.ErrorFactory
import scala.collection.JavaConverters._
import java.util.HashMap
import java.util.Map
import com.google.web.bindery.autobean.shared.AutoBeanUtils
import com.google.web.bindery.autobean.shared.AutoBeanCodex

@Provider
class FileNotFoundMapper extends ExceptionMapper[FileNotFoundException] {
  
  val errorFactory = AutoBeanFactorySource.create(classOf[ErrorFactory])
  val notFoundErr = {
    val nfErr = errorFactory.newError().as
    nfErr.setCode("404_FILENOTFOUD3")
    val props:java.util.Map[String,String] = new HashMap[String,String]
    props.put("path", "/some/path")    
    nfErr.setParams(props)
    val stuffs = new java.util.ArrayList[String]
    stuffs.add("something")
    nfErr.setStuffs(stuffs)
    nfErr.setUala(123)
    nfErr
  }

  override def toResponse(fnfe: FileNotFoundException): Response = {
    val bean = AutoBeanUtils.getAutoBean(notFoundErr)
    val payload = AutoBeanCodex.encode(bean).getPayload
    Response
    	.status(404)
    	.entity(payload)
    	.build
    	
  }
  
  
}






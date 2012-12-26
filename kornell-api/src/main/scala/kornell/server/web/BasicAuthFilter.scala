package kornell.server.web

import javax.servlet._
import javax.servlet.http._
import javax.servlet.annotation.WebFilter
import org.apache.commons.codec.binary.Base64

class BasicAuthFilter extends Filter {

  override def doFilter(sreq: ServletRequest, res: ServletResponse, chain: FilterChain) {
    val resp = res.asInstanceOf[HttpServletResponse]
    val req = sreq.asInstanceOf[HttpServletRequest]
    //val path = req.getPathInfo()    
    if("OPTIONS".equals(req.getMethod())){
      chain.doFilter(req,resp);
    }else    
      checkCredentials(req,resp,chain)    
  }
  
  
  def checkCredentials(req:HttpServletRequest, resp:HttpServletResponse, chain: FilterChain) = {
    val auth = req.getHeader("Authorization");
    if(auth != null && auth.length() > 0){
    	val (username,password) = extractCredentials(auth)
    	try{
    	  req.login(username, password);
    	  chain.doFilter(req, resp);
    	}catch{
    	  case se:ServletException => resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Authentication failed") 
    	}    	
    }else resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,"You should authenticate")
  }
  
  def extractCredentials(auth:String) = {
    val encoded = auth.split(" ")(1)
    val decoded = new String(Base64.decodeBase64(encoded))
    val extracted = decoded.split(":")
    (extracted(0),extracted(1))
  }
  
  override def init(cfg: FilterConfig) {}
  override def destroy() {}
}
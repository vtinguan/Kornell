package kornell.server.web

import javax.servlet._
import javax.servlet.http._
import javax.servlet.annotation.WebFilter
import org.apache.commons.codec.binary.Base64
import java.util.logging.Logger


class BasicAuthFilter extends Filter {
  val log = Logger.getLogger(classOf[BasicAuthFilter].getName) 
	
  val pubPaths = Set("/checkup","/sandbox", "/sync", "/report", "/user/check", "/user/create", "/email/welcome", "/institutions")
  
  override def doFilter(sreq: ServletRequest, sres: ServletResponse, chain: FilterChain) {
    (sreq, sres) match {
      case (hreq: HttpServletRequest, hres: HttpServletResponse) =>
        doFilter(hreq, hres, chain)
    }
  }

  def doFilter(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) = 
    if (isPublic(req,resp))
      chain.doFilter(req, resp)
    else
      checkCredentials(req, resp, chain)

  def isPublic(req: HttpServletRequest, resp: HttpServletResponse) ={
      val path = req.getRequestURI
      val isPublic = path == "/" || pubPaths.exists {path.startsWith(_)}        
      val isOption = "OPTIONS".equals(req.getMethod)
      isOption || isPublic 
    }
    
      
  def checkCredentials(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) = {
    val auth = req.getHeader("Authorization");
    if (auth != null && auth.length() > 0) {
      val (username, password) = extractCredentials(auth)
      try {
        req.login(username, password);
        chain.doFilter(req, resp);
      } catch {
        case se: ServletException => 
          se.printStackTrace(); resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, 
              s"Authentication failed for user $username at uri ${req.getRequestURI}")
      }
    } else resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You should authenticate")
  }

  def extractCredentials(auth: String) = {
    val encoded = auth.split(" ")(1)
    val decoded = new String(Base64.decodeBase64(encoded))
    val extracted = decoded.split(":")
    (extracted(0), extracted(1))
  }

  override def init(cfg: FilterConfig) {}
  override def destroy() {}
}
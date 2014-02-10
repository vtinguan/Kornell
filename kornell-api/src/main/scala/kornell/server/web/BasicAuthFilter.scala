package kornell.server.web

import javax.servlet._
import javax.servlet.http._
import javax.servlet.annotation.WebFilter
import org.apache.commons.codec.binary.Base64
import java.util.logging.Logger

class BasicAuthFilter extends Filter {
  val log = Logger.getLogger(classOf[BasicAuthFilter].getName)
  val pubPaths = Set(
      "/checkup", 
      "/sandbox", 
      "/sync", 
      "/report", 
      "/user/check", 
      "/user/create", 
      "/user/registrationRequest", 
      "/user/requestPasswordChange",
      "/user/changePassword",
      "/user/uploadProfileImage",
      "/email/welcome", 
      "/institutions",
      "/repository")

  override def doFilter(sreq: ServletRequest, sres: ServletResponse, chain: FilterChain) {
    (sreq, sres) match {
      case (hreq: HttpServletRequest, hres: HttpServletResponse) =>
        doFilter(hreq, hres, chain)
    }
  }

  def hasCredentials(req: HttpServletRequest) =
    req.getHeader("Authorization") != null
  
  def isPrivate(req: HttpServletRequest, resp: HttpServletResponse) = ! isPublic(req,resp)
  
  def doFilter(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) =
    if (hasCredentials(req) || isPrivate(req, resp))
      checkCredentials(req, resp, chain)
    else {
      println(s"-- Authorizing [bypass]" )
      chain.doFilter(req, resp)
    }

  def isPublic(req: HttpServletRequest, resp: HttpServletResponse) = {
    val path = req.getRequestURI
    val isPublic = path == "/api" || pubPaths.exists { p => path.startsWith(s"/api${p}") }
    val isOption = "OPTIONS".equals(req.getMethod)
    isOption || isPublic
  }

  def checkCredentials(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) = {
    val auth = req.getHeader("Authorization");
    println(s"-- Authorizing [$auth]" )
    if (auth != null && auth.length() > 0) {
      try {
    	val (username, password) = extractCredentials(auth)
        req.login(username, password);
    	println(s"-- Authorizing [$username]" )
        chain.doFilter(req, resp);
      } catch {
        case e: Exception =>
          e.printStackTrace(); resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
            s"Authentication failed at uri ${req.getRequestURI}")
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

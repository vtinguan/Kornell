package kornell.server.web

import javax.servlet._
import javax.servlet.http._
import javax.servlet.annotation.WebFilter
import org.apache.commons.codec.binary.Base64
import java.util.logging.Logger
import kornell.core.util.StringUtils
import kornell.server.jdbc.SQL._
import kornell.server.util.SHA256
import java.sql.ResultSet
import kornell.server.authentication.ThreadLocalAuthenticator

class BasicAuthFilter extends Filter {
  val log = Logger.getLogger(classOf[BasicAuthFilter].getName)
  val pubPaths = Set(
    "/newrelic",
    "/api",
    "/probes",
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
    "/repository",
    "/healthCheck")

  override def doFilter(sreq: ServletRequest, sres: ServletResponse, chain: FilterChain) =
    (sreq, sres) match {
      case (hreq: HttpServletRequest, hres: HttpServletResponse) =>
        doFilter(hreq, hres, chain)
    }

  def hasCredentials(req: HttpServletRequest): Boolean =
    req.getHeader("X-KNL-A") != null

  def isPrivate(req: HttpServletRequest, resp: HttpServletResponse) = !isPublic(req, resp)

  def doFilter(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) =
    if (hasCredentials(req) || isPrivate(req, resp))
      checkCredentials(req, resp, chain)
    else {
      chain.doFilter(req, resp)
    }

  def isPublic(req: HttpServletRequest, resp: HttpServletResponse) = {
    val path = req.getRequestURI
    val isPublic = path == "/api" || path == "/api/" || pubPaths.exists { p => path.startsWith(s"/api${p}") }
    val isOption = "OPTIONS".equals(req.getMethod)
    isOption || isPublic
  }

  def checkCredentials(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) = {
    val auth = req.getHeader("X-KNL-A");
    if (auth != null && auth.length() > 0) {
      try {
        val (username, password) = extractCredentials(auth)
        login(req, username, password)
        chain.doFilter(req, resp);
      } catch {
        case e: Exception =>
          e.printStackTrace(); resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
            s"Authentication failed at uri ${req.getRequestURI}")
      }
    } else resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must authenticate to access [" + req.getRequestURI + "]")
  }

  def extractCredentials(auth: String) = {
    val encoded = auth.split(" ")(1)
    val decoded = new String(Base64.decodeBase64(encoded))
    val extracted = decoded.split(":")
    (extracted(0), extracted(1))
  }

  override def init(cfg: FilterConfig) {}

  override def destroy() {}

  def login(req: HttpServletRequest, username: String, password: String) = {
    val personUUID = sql"""
    select person_uuid 
    from Password
    where username=${username}
    and password=${SHA256(password)}
    """.first[String]
    
    personUUID foreach ThreadLocalAuthenticator.setAuthenticatedPersonUUID    
  }

}

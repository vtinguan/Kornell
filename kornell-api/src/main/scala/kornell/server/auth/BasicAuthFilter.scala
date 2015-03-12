
package kornell.server.auth

import javax.servlet._
import javax.servlet.http._
import org.apache.commons.codec.binary.Base64
import java.util.logging.Logger
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.AuthRepo
import javax.inject.Inject
import kornell.server.auth.ThreadLocalAuthenticator

class BasicAuthFilter extends Filter {
  @Inject var authRepo:AuthRepo = _
  
  val log = Logger.getLogger(classOf[BasicAuthFilter].getName)
  val pubPaths = Set(
    "/newrelic",
    "/api",
    "/probes",
    "/checkup",
    "/sandbox",
    "/sync",
    "/report",
    "/user/hello",
    "/user/check",
    "/user/create",
    "/user/registrationRequest",
    "/user/requestPasswordChange",
    "/user/changePassword",
    "/user/uploadProfileImage",
    "/email/welcome",
    "/institutions",
    "/repository",
    "/healthCheck",
    "/errors")

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
    val isPublic = path == "/ArquillianServletRunner" || 
    	path == "/api" || 
    	path == "/api/" || 
    	pubPaths.exists { p => path.startsWith(s"/api${p}") }
    val isOption = "OPTIONS".equals(req.getMethod)
    
    //println(s"**** IS [$path] Pub? [${isOption || isPublic}]")
    isOption || isPublic
  }

  def checkCredentials(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) = {
    val auth = req.getHeader("X-KNL-A");
    if (auth != null && auth.length() > 0) {
      try {
        val (username, password, institutionUUID) = BasicAuthFilter.extractCredentials(auth)
        login(institutionUUID, username, password)
        
      } catch {
        case e: Exception =>
          e.printStackTrace(); resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
            s"Authentication failed at uri ${req.getRequestURI}")
      }
      chain.doFilter(req, resp);
      logout
    } else resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must authenticate to access [" + req.getRequestURI + "]")
  }

  override def init(cfg: FilterConfig) {}

  override def destroy() {}

  def login(institutionUUID: String, username: String, password: String) =
    authRepo.authenticate(institutionUUID, username, password).map { personUUID =>
      ThreadLocalAuthenticator.setAuthenticatedPersonUUID(personUUID)
    }

  def logout = ThreadLocalAuthenticator.clearAuthenticatedPersonUUID
}

object BasicAuthFilter{
 
  def extractCredentials(auth: String) = {
    val encoded = auth.split(" ")(1)
    val decoded = new String(Base64.decodeBase64(encoded))
    val extracted = decoded.split(":")
    (extracted(0), extracted(1), extracted(2))
  }
}

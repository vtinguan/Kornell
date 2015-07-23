
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
import kornell.server.jdbc.repository.AuthRepo
import kornell.core.error.exception.UnauthorizedAccessException
import kornell.core.error.KornellErrorTO
import kornell.server.jdbc.repository.TokenRepo
import kornell.core.entity.AuthClientType
import java.util.Date

class BasicAuthFilter extends Filter {
  val log = Logger.getLogger(classOf[BasicAuthFilter].getName)
  val pubPaths = Set(
    "/newrelic",
    "/api",
    "/probes",
    "/sync",
    "/report",
    "/user/hello",
    "/user/check",
    "/user/registrationRequest",
    "/user/requestPasswordChange",
    "/user/changePassword",
    "/user/resetPassword",
    "/institutions",
    "/repository",
    "/healthCheck",
    "/auth",
    "/log")

  override def doFilter(sreq: ServletRequest, sres: ServletResponse, chain: FilterChain) =
    (sreq, sres) match {
      case (hreq: HttpServletRequest, hres: HttpServletResponse) => {
        if (hreq.getRequestURI.startsWith("/api")) {
        	doFilter(hreq, hres, chain)
        } else {
          chain.doFilter(hreq, hres)
        }
      }
    }

  def hasCredentials(req: HttpServletRequest): Boolean =
    req.getHeader("X-KNL-TOKEN") != null

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
    val auth = req.getHeader("X-KNL-TOKEN");
    if (auth != null && auth.length() > 0) {
        val token = TokenRepo.checkToken(auth)
        if (!token.isDefined || (token.get.getClientType == AuthClientType.web && token.get.getExpiry.before(new Date))) {
          resp.setContentType(KornellErrorTO.TYPE)
          resp.setStatus(401)
          resp.setCharacterEncoding("UTF-8")
          resp.getWriter().write("{\"messageKey\":\"mustAuthenticate\"}")
        } else {
          ThreadLocalAuthenticator.setAuthenticatedPersonUUID(token.get.getPersonUUID)
          chain.doFilter(req, resp);
        }
        logout
    } else {
      resp.setContentType(KornellErrorTO.TYPE)
      resp.setStatus(401)
      resp.setCharacterEncoding("UTF-8")
      resp.getWriter().write("{\"messageKey\":\"mustAuthenticate\"}")
    }
  }

  override def init(cfg: FilterConfig) {}

  override def destroy() {}

  def logout = ThreadLocalAuthenticator.clearAuthenticatedPersonUUID
}


package kornell.server.web

import javax.servlet._
import javax.servlet.http._
import javax.servlet.annotation.WebFilter
import org.apache.commons.codec.binary.Base64
import java.util.logging.Logger
import kornell.core.util.StringUtils

class CacheControlFilter extends Filter {

  override def doFilter(sreq: ServletRequest, sres: ServletResponse, chain: FilterChain) {
    (sreq, sres) match {
      case (hreq: HttpServletRequest, hres: HttpServletResponse) =>
        doFilter(hreq, hres, chain)
    }
  }

  def doFilter(hreq: HttpServletRequest, hres: HttpServletResponse, chain: FilterChain) {
    chain.doFilter(hreq, hres)
    hres.setHeader("X-KNL-CC", this.getClass.getName + "-2014-02-10-1")
    hres.setHeader("Cache-Control", "max-age=0");
  }

  override def init(cfg: FilterConfig) {}
  override def destroy() {}
}

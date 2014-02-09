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
    val cacheControl = hreq.getHeader("Cache-Control")
    if (StringUtils.isNone(cacheControl)) {
    	hres.setHeader("Cache-Control", "0");
    	hres.setHeader("Cache-Control-By", this.getClass.getName)
    }
  }

  override def init(cfg: FilterConfig) {}
  override def destroy() {}
}

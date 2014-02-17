package kornell.server.web

import javax.servlet._
import javax.servlet.http._
import javax.servlet.annotation.WebFilter
import org.apache.commons.codec.binary.Base64
import java.util.logging.Logger
import kornell.core.util.StringUtils
import java.text.SimpleDateFormat
import java.util.Date

class CacheControlFilter extends Filter {

  val dateFmt:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS")
  
  override def doFilter(sreq: ServletRequest, sres: ServletResponse, chain: FilterChain) {
    (sreq, sres) match {
      case (hreq: HttpServletRequest, hres: HttpServletResponse) =>
        doFilter(hreq, hres, chain)
    }
  }

  def doFilter(hreq: HttpServletRequest, hres: HttpServletResponse, chain: FilterChain) {
    assert(! hres.isCommitted())
    hres.setHeader("X-KNL-TSTAMP", dateFmt.format(new Date()))
    hres.setHeader("Cache-Control", "max-age=0")
    chain.doFilter(hreq, hres)
    
  }

  override def init(cfg: FilterConfig) {}
  override def destroy() {}
}

package kornell.server.web

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * This filter enables requests from other origins, such as the GWT app, including pre-flight OPTION requests.
 * 
 * @see http://en.wikipedia.org/wiki/Cross-origin_resource_sharing
 * @see http://www.html5rocks.com/en/tutorials/cors/
 * @see http://caniuse.com/#feat=cors (Does IE suck or what)
 */
//TODO: Replace by standard apache tomcat CORS Filter
class CORSFilter extends Filter { 
  var allowedOrigins = ""
  
  override def init(cfg:FilterConfig){
    allowedOrigins = cfg.getInitParameter("allowedOrigins")
  }
  

  override def doFilter(sreq: ServletRequest, sres: ServletResponse, chain: FilterChain) {
    (sreq, sres) match {
      case (hreq: HttpServletRequest, hres: HttpServletResponse) =>
        doFilter(hreq, hres, chain)
    }
  }

  def doFilter(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) = {
    
    if(allowedOrigins != "") 
      resp.addHeader("Access-Control-Allow-Origin", allowedOrigins)
 
    resp.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,HEAD,OPTIONS");
    resp.addHeader("Access-Control-Allow-Headers", "origin, authorization, content-type")
    if ("OPTIONS".equals(req.getMethod()))
      resp.setStatus(200)
    else
      chain.doFilter(req,resp)
  }
  
  override def destroy(){}
	 
}
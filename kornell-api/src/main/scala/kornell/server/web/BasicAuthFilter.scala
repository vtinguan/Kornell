package kornell.server.web

import javax.servlet._
import javax.servlet.http._
import javax.servlet.annotation.WebFilter

@WebFilter(Array("/*"))
class BasicAuthFilter extends Filter {

  override def doFilter(sreq: ServletRequest, res: ServletResponse, chain: FilterChain) {
    val resp = res.asInstanceOf[HttpServletResponse]
    val req = sreq.asInstanceOf[HttpServletRequest]
    val path = req.getPathInfo()
    if (! isPublic(path))
    	checkCredentials(req,resp,chain)
    else chain.doFilter(req, res)
  }
  
  def isPublic(path:String) = path.startsWith("/auth") 
  def checkCredentials(req:HttpServletRequest, resp:HttpServletResponse, chain: FilterChain) = {
    chain.doFilter(req, resp)
  }
  
  override def init(cfg: FilterConfig) {}
  override def destroy() {}
}
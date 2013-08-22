package kornell.server.dev

import javax.servlet.annotation.WebServlet
import javax.servlet.http._

@WebServlet(Array("/sysinfo"))
class SysinfoServlet extends HttpServlet {
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    val out = resp.getWriter
    resp.setContentType("text/plain")    
    System.getProperties().list(out)
  }
}

object Sysinfo extends App {
  System.getProperties.list(System.out)
}
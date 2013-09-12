package kornell.server.dev

import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet

import kornell.server.repository.jdbc.SQLInterpolation._

@WebServlet(Array("/sandbox"))
class SandboxServlet extends HttpServlet{
	override def doGet( req:HttpServletRequest, resp:HttpServletResponse) {
		val out = resp.getWriter
		sql"""select terms from Institution""".foreach{rs => out.println(rs.getString("terms"))}
	  /*
		resp.setCharacterEncoding("UTF-8")
		resp.setContentType("text/html; charset=UTF-8");
		resp.setCharacterEncoding("UTF-8")
		
		out.println("<html><body>")
		out.println("Servlet Encoding: "+System.getProperty("javax.servlet.request.encoding"))
		out.println("File Encoding:"+System.getProperty("file.encoding"))
		out.println("Straight from the heart: áéíêão")
		sql"""select terms from Institution""".foreach{rs => out.println(rs.getString("terms"))}
		out.println("")
		out.println("</body></html>")
		*/
		out.println("Hello");
		
	}
}
package kornell.dev

import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet

import kornell.repository.jdbc.SQLInterpolation._

@WebServlet(Array("/sandbox"))
class SandboxServlet extends HttpServlet{
	override def doGet( req:HttpServletRequest, resp:HttpServletResponse) {
		//resp.setCharacterEncoding("UTF-8")
		resp.setContentType("text/html; charset=UTF-8");
		resp.setCharacterEncoding("UTF-8")
		val out = resp.getWriter
		out.println("<html><body>")
		out.println("Servlet Encoding: "+System.getProperty("javax.servlet.request.encoding"))
		out.println("File Encoding:"+System.getProperty("file.encoding"))
		out.println("Straight from the heart: áéíêão")
		sql"""select terms from Institution""".foreach{rs => out.println(rs.getString("terms"))}
		out.println("")
		out.println("</body></html>")
	}
}
package kornell.server.dev

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.annotation.WebServlet
import javax.naming.InitialContext
import javax.sql.DataSource
import javax.naming.Context

@WebServlet(Array("/checkup"))
class CheckupServlet extends HttpServlet{ 
	override def doGet(req:HttpServletRequest, resp:HttpServletResponse) = {
	  val initctx = new InitialContext();
	  val ctx = initctx.lookup("java:comp/env").asInstanceOf[Context]
	  val ds = ctx.lookup("jdbc/KornellDS").asInstanceOf[DataSource]
	  val conn = ds.getConnection
	  val rs = conn.createStatement().executeQuery("select count(*) from Person")
	  if(rs.next()){
	    val cnt = rs.getInt(1)
	    resp.getWriter().println(s"$cnt person here")
	  }else{
	    throw new RuntimeException("No one found")
	  }
	  conn.close()
	  
	}

}
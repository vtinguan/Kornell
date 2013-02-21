package kornell.dev

import javax.servlet.http._
import javax.inject.Inject
import javax.persistence.EntityManager
import scala.collection.JavaConverters._
import java.io.PrintWriter

class JPQLServlet extends HttpServlet {

  @Inject
  var em: EntityManager = _

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    val out = resp.getWriter

    try Option(req getParameter "q") foreach (query => execute(query, out))
    catch {
      case e: Exception => e.printStackTrace(out)
    }
  }

  def execute(query: String, out: PrintWriter) = {
    val results = em.createQuery(query).getResultList.asScala.map {_.asInstanceOf[Any]}
    for (result <- results){
    	print(out,result)
    	out.println
    }
  }
  
  def print(out:PrintWriter,result:Any) = result match {
    case a:Array[_] => out print (a mkString ", ")
    case _ => out print(result)
  }


 

}
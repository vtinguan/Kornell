package kornell.server.jdbc

import java.sql.Connection
import kornell.core.util.UUID

object SQL {
  type ConnectionFactory = () => Connection

  implicit class SQLHelper(val sc: StringContext) extends AnyVal {
    //TODO: Copied from http://www.monadzoo.com/blog/2013/01/06/scala-string-interpolation-it-happened/
    //TODO: What args:_* means?
    def sql(args: Any*) = {
      val parts = sc.parts.iterator
      val params = args.toList
      var query = new StringBuffer(parts.next)
      while (parts.hasNext) {
        query append "?"
        query append parts.next
      }
      new PreparedStmt(query.toString, params)
    }
  }

  implicit def randomUUID = UUID.randomUUID()
}
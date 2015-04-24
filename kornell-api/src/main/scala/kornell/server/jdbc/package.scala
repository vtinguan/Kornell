package kornell.server

import kornell.server.util.Settings
import java.util.logging.Logger
package object jdbc {
  def prop(name: String):String = Settings.get(name).getOrElse(null)

  val DEFAULT_URL = "jdbc:mysql:///knl_def_jdbc_url"
  val DEFAULT_USERNAME= "knl_def_jdbc_usr"
  val DEFAULT_PASSWORD= "knl_def_jdbc_pwd"

  val logger = Logger.getLogger("kornell.server.jdbc")
    
  
}
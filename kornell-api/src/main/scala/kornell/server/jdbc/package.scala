package kornell.server

import kornell.server.util.Settings
import java.util.logging.Logger
package object jdbc {
  def prop(name: String):String = Settings.get(name).getOrElse(null)

  val DEFAULT_URL = "jdbc:mysql:///ebdb?useUnicode=true&characterEncoding=UTF-8"
  val DEFAULT_USERNAME= "kornell"
  val DEFAULT_PASSWORD= "42kornell73"

  val logger = Logger.getLogger("kornell.server.jdbc")
    
  
}
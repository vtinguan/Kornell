package kornell.server.dev.util

import kornell.server.util.Settings
import kornell.server.util.Settings.EnumVal

object Sandbox extends App{
  def toS(ev:EnumVal) = s"[${if (ev.required) "*" else " " }] ${ev.name} = ${ev.getOpt.getOrElse("")}"
  
  Settings.values.foreach { ev => println(toS(ev)) }
  
}
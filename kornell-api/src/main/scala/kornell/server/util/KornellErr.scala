package kornell.server.util

class KornellErr (code:String,properties:Map[String,String]) extends Err {
  
  def toJSON:String = {
    val buf = new StringBuilder
    buf.append("{")
    val props = properties.map {case (k,v) => s"'$k':'$v',"}.mkString
    buf.append(props)
    buf.append(s"'code':'$code'")
    buf.append("}")  
    buf.toString
  }
    
}

object FileNotFoundErr {
  def apply(path:String) = new KornellErr("404_FILENOTFOUND",Map(("path"->path)))
}
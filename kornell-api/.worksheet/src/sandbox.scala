object sandbox {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(34); 
	val a:Any = null;System.out.println("""a  : Any = """ + $show(a ));$skip(109); 
	a match {
		case (x:Int) => println(x+1)
		case (x:String) => println(x)
		case null => println("NULOW")
	}}
}

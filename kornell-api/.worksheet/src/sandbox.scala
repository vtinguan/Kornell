object sandbox {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(40); 
	val a = Option("uala");System.out.println("""a  : Option[String] = """ + $show(a ));$skip(22); 
	val b = Option(null);System.out.println("""b  : Option[Null] = """ + $show(b ));$skip(47); 
	
	val z = for{ c <- a
		d <- b
	} yield c + d;System.out.println("""z  : Option[String] = """ + $show(z ));$skip(5); val res$0 = 
	
	z;System.out.println("""res0: Option[String] = """ + $show(res$0));$skip(22); 
	
	 z foreach println}
}

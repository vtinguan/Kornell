

import scala.util._
object sandbox {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(78); 
	def first = Try { "the answer is..." };System.out.println("""first: => scala.util.Try[String]""");$skip(35); 
	def second(s:String) = Try { 42 };System.out.println("""second: (s: String)scala.util.Try[Int]""");$skip(115); 
	
	first flatMap second match {
		case Success(_) => println("Wheeee!!!!")
		case Failure(_) => println("DUH!")
	}}
	
	

}

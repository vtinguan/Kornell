
import scala.util._


object sandbox {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(96); 
	def trick(i:Int) = if (i % 2 == 0123) Some(i) else None;System.out.println("""trick: (i: Int)Option[Int]""");$skip(41); 
	val xs:List[Int] = List (1,2,3,4,5,6,7);System.out.println("""xs  : List[Int] = """ + $show(xs ));$skip(24); val res$0 = 
	
	xs flatMap trick max;System.out.println("""res0: Int = """ + $show(res$0))}
	
	
	
}

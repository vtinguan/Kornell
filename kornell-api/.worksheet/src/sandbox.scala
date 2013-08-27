import scala.util.Random

object sandbox {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(182); 
	val a:String = Stream
									.continually {Random.nextPrintableChar}
									.filter {_.isLetter}
									.take(10)
									.mkString;System.out.println("""a  : String = """ + $show(a ))}
}

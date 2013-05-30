

object worksheet {

  import java.lang.Byte;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(365); 

  val stringToSignBytes = "50 55 54 0a 0a 61 70 70 6c 69 63 61 74 69 6f 6e 2f 78 2d 77 77 77 2d 66 6f 72 6d 2d 75 72 6c 65 6e 63 6f 64 65 64 0a 0a 78 2d 61 6d 7a 2d 64 61 74 65 3a 54 75 65 2c 20 32 38 20 4d 61 79 20 32 30 31 33 20 31 39 3a 31 36 3a 31 31 20 47 4d 54 0a 2f 63 64 66 2d 63 69 2f 73 33 70 75 74 2e 73 68";System.out.println("""stringToSignBytes  : String = """ + $show(stringToSignBytes ));$skip(123); 
  val provided = "PUT\n\napplication/x-www-form-urlencoded\n\nx-amz-date: Tue, 28 May 2013 19:16:11 GMT\n/cdf-ci/s3put.sh";System.out.println("""provided  : String = """ + $show(provided ));$skip(132); 

  val canonical = new String(
    stringToSignBytes
      .split(" ")
      .map(_.toUpperCase)
      .map(Byte.parseByte(_, 16)));System.out.println("""canonical  : String = """ + $show(canonical ));$skip(20); 

  println("-----");$skip(30); 
  println(canonical.getBytes);$skip(19); 
  println("-----");$skip(29); 
  println(provided.getBytes);$skip(19); 
  println("-----");$skip(39); 

  val eq = canonical.equals(provided);System.out.println("""eq  : Boolean = """ + $show(eq ));$skip(22); val res$0 = 

  canonical.length();System.out.println("""res0: Int = """ + $show(res$0));$skip(20); val res$1 = 
  provided.length();System.out.println("""res1: Int = """ + $show(res$1));$skip(24); val res$2 = 

  canonical.getBytes();System.out.println("""res2: Array[Byte] = """ + $show(res$2));$skip(22); val res$3 = 
  provided.getBytes();System.out.println("""res3: Array[Byte] = """ + $show(res$3));$skip(22); 

  println(canonical);$skip(19); 
  println("-----");$skip(20); 
  println(provided)}

}
 
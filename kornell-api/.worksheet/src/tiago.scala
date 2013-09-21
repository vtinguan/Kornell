object tiago {
 
  type SemArg = () => Int

  type Operacao = (Int,Int) => Int;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(116); 

	val soma:Operacao =  (a,b) => {a+b};System.out.println("""soma  : (Int, Int) => Int = """ + $show(soma ));$skip(37); 
	val mult:Operacao =  (a,b) => {a*b};System.out.println("""mult  : (Int, Int) => Int = """ + $show(mult ));$skip(49); 
	
  def executa(f:Operacao,a:Int,b:Int) = f(a,b);System.out.println("""executa: (f: (Int, Int) => Int, a: Int, b: Int)Int""");$skip(82); 
                                                  
  val seis = executa(soma,3,3);System.out.println("""seis  : Int = """ + $show(seis ));$skip(34); 
  val doze = executa(mult,seis,2);System.out.println("""doze  : Int = """ + $show(doze ))}
	
}

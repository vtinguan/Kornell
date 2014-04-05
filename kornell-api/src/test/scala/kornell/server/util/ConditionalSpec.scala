package kornell.server.util

import org.junit.runner.RunWith
import kornell.server.util.Conditional._
import org.scalatest.junit.JUnitRunner
import kornell.server.test.UnitSpec
 
@RunWith(classOf[JUnitRunner]) 
class ConditionalSpec extends UnitSpec { 

  "A empty conditional" should "always pass" in {    
    assert(Conditional({}).isPassed )
  }
 
  "A true conditional" should "always evaluate" in {
    assert( 42.requiring(true).get == 42 )
  }

 
 "A false condition" should "not pass" in {
    assert(! {}.requiring(false).isPassed)
  }
 
 "Conditioning to a or b" should "eval if either is true" in {
    val answer = 42 requiring(false) or(true)     
    assert(answer.get == 42)
  }
    
  "A OR requirement without a true leg" should "be denied without evaluating" in {
    val cond = Conditional({fail()})
    assert(! cond.requiring(false).or(false).isPassed)
  }
  
} 


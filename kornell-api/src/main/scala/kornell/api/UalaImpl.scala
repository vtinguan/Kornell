package kornell.api

import javax.enterprise.inject.Alternative

@Alternative
class UalaImpl extends OptionService{
  
  override def doTheTrick = Option("Uala!!! CDI+Scala!!!")

}
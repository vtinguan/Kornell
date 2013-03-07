package kornell.api

import javax.enterprise.inject.Alternative

@Alternative
class AnotherImpl extends OptionService{
  
  override def doTheTrick = Option("Another One Bytes the Dust!!")

}
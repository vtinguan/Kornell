package kornell.server.scorm.scorm12.rte

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.scorm.scorm12.rte.RTE12Factory
import kornell.core.to.ActionType
import kornell.server.repository.TOs
import scala.collection.JavaConverters._

object RTE12 {
	val factory = AutoBeanFactorySource.create(classOf[RTE12Factory])

	def newOpenSCO12Action(resId:String, href:String, data:Map[String,String]) = {
	  val action = TOs.newActionTO
	  val openSCO = factory.newOpenSCO12Action.as
	  openSCO.setResourceId(resId)
	  openSCO.setHref(href)
	  openSCO.setData(data.asJava)
	  action.setOpenSCO12Action(openSCO)
	  action.setType(ActionType.OpenSCO12)
	  action
	}
	
}
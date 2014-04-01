package kornell.server.util

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.value.ValueFactory

object TimeUtil {
   val valueFactory = AutoBeanFactorySource.create(classOf[ValueFactory])
   
   def newDate = valueFactory.newDate().as()

}
package kornell.server.util

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.value.{ValueFactory => KVF}

object ValueFactory {
   val valueFactory = AutoBeanFactorySource.create(classOf[KVF])
   
   def newDate = valueFactory.newDate().as()

}
package kornell.repository

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.shared.to.TOFactory

trait TOs {
	val tos = AutoBeanFactorySource.create(classOf[TOFactory])
	
	def newUserInfoTO() = tos.newUserInfoTO.as
}
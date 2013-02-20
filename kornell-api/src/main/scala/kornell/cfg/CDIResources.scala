package kornell.cfg

import javax.enterprise.inject.Produces
import javax.persistence.PersistenceContext
import javax.persistence.EntityManager
import javax.enterprise.context.ApplicationScoped
import kornell.core.shared.to.TOFactory
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource

class CDIResources {
    @Produces
    @PersistenceContext
    val em:EntityManager = null;
    
    @Produces
    @ApplicationScoped
    val createTOFactory = AutoBeanFactorySource.create(classOf[TOFactory])
    
}
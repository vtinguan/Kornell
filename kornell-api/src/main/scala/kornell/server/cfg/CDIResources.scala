package kornell.server.cfg

import javax.enterprise.inject.Produces
import javax.persistence.PersistenceContext
import javax.persistence.EntityManager
import javax.enterprise.context.ApplicationScoped
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.shared.data.BeanFactory



class CDIResources {
    @Produces
    @PersistenceContext
    val em:EntityManager = null;
    
    @Produces
    @ApplicationScoped
    val BeanFactory = AutoBeanFactorySource.create(classOf[BeanFactory])
    
}
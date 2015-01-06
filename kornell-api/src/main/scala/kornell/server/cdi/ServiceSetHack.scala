package kornell.server.cdi

import javax.enterprise.context.ApplicationScoped
import java.util.TreeSet
import javax.enterprise.inject.Produces
import java.util.Set
import com.google.common.util.concurrent.Service
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Default

@ApplicationScoped
class ServiceSetHack {
  @Produces
  @Default
  def createServiceSet:Set[Service] =     
    new TreeSet[Service]
}
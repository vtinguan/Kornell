package kornell.server

import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.AuthRepo
import kornell.core.entity.RoleCategory
import scala.collection.JavaConverters._
import java.util.logging.Logger

package object api {
    val logger = Logger.getLogger("kornell.server.api")

  //TODO: Consider moving these functions to AuthRepo
  //TODO: Good cache candidate
  def rolesOf(username: String) = (Set.empty ++ AuthRepo.rolesOf(username)).asJava

  def isPlatformAdmin(implicit sc:SecurityContext):Boolean = 
    RoleCategory.isPlatformAdmin(rolesOf(sc.getUserPrincipal.getName))
    
  def isInstitutionAdmin(institutionUUID:String)(implicit sc:SecurityContext):Boolean = 
    RoleCategory.isInstitutionAdmin(rolesOf(sc.getUserPrincipal.getName), institutionUUID)
    
  def isCourseClassAdmin(courseClassUUID:String)(implicit sc:SecurityContext):Boolean = 
    RoleCategory.isCourseClassAdmin(rolesOf(sc.getUserPrincipal.getName), courseClassUUID)
}
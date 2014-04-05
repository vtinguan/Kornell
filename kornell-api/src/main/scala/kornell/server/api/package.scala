package kornell.server

import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.AuthRepo
import kornell.core.entity.RoleCategory
import scala.collection.JavaConverters._

package object api {
  //TODO: Consider moving these functions to AuthRepo
  //TODO: Good cache candidate
  def rolesOf(username: String) = (Set.empty ++ AuthRepo.rolesOf(username)).asJava

  def isPlatformAdmin(implicit sc:SecurityContext):Boolean = 
    RoleCategory.isPlatformAdmin(rolesOf(sc.getUserPrincipal.getName))
    
  def isInstitutionAdmin(institutionUUID:String)(implicit sc:SecurityContext):Boolean = 
    RoleCategory.isInstitutionAdmin(rolesOf(sc.getUserPrincipal.getName), institutionUUID)
}
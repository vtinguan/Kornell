package kornell.server

import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.AuthRepo
import kornell.core.entity.RoleCategory
import scala.collection.JavaConverters._
import java.util.logging.Logger
import kornell.server.authentication.ThreadLocalAuthenticator
import kornell.server.jdbc.repository.RolesRepo

package object api {
  val logger = Logger.getLogger("kornell.server.api")
  
  def isControlPanelAdmin():Boolean = RoleCategory.isControlPanelAdmin(RolesRepo.getUserRoles(getAuthenticatedPersonUUID, RoleCategory.BIND_DEFAULT).getRoleTOs)

  def isPlatformAdmin(institutionUUID:String):Boolean = RoleCategory.isPlatformAdmin(RolesRepo.getUserRoles(getAuthenticatedPersonUUID, RoleCategory.BIND_DEFAULT).getRoleTOs, institutionUUID)
    
  def isInstitutionAdmin(institutionUUID:String):Boolean = 
    RoleCategory.isInstitutionAdmin(RolesRepo.getUserRoles(getAuthenticatedPersonUUID, RoleCategory.BIND_DEFAULT).getRoleTOs, institutionUUID)
    
  def isCourseClassAdmin(courseClassUUID:String):Boolean = 
    RoleCategory.isCourseClassAdmin(RolesRepo.getUserRoles(getAuthenticatedPersonUUID, RoleCategory.BIND_DEFAULT).getRoleTOs, courseClassUUID)
    
  def getAuthenticatedPersonUUID = ThreadLocalAuthenticator.getAuthenticatedPersonUUID.getOrElse(null)
  
 
}
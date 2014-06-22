package kornell.server

import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.AuthRepo
import kornell.core.entity.RoleCategory
import scala.collection.JavaConverters._
import java.util.logging.Logger

package object api {
  val logger = Logger.getLogger("kornell.server.api")

  def isPlatformAdmin(implicit sc:SecurityContext):Boolean = {
    val isPlatformAdmin = RoleCategory.isPlatformAdmin(AuthRepo.getUserRoles)
    isPlatformAdmin
  }
    
  def isInstitutionAdmin(institutionUUID:String)(implicit sc:SecurityContext):Boolean = 
    RoleCategory.isInstitutionAdmin(AuthRepo.getUserRoles, institutionUUID)
    
  def isCourseClassAdmin(courseClassUUID:String)(implicit sc:SecurityContext):Boolean = 
    RoleCategory.isCourseClassAdmin(AuthRepo.getUserRoles, courseClassUUID)
}
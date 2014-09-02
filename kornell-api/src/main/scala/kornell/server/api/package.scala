package kornell.server

import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.AuthRepo
import kornell.core.entity.RoleCategory
import scala.collection.JavaConverters._
import java.util.logging.Logger
import kornell.server.authentication.ThreadLocalAuthenticator

package object api {
  val logger = Logger.getLogger("kornell.server.api")

  def isPlatformAdmin:Boolean = {
    val isPlatformAdmin = RoleCategory.isPlatformAdmin(AuthRepo().getUserRoles)
    isPlatformAdmin 
  }
    
  def isInstitutionAdmin(institutionUUID:String):Boolean = 
    RoleCategory.isInstitutionAdmin(AuthRepo().getUserRoles, institutionUUID)
    
  def isCourseClassAdmin(courseClassUUID:String):Boolean = 
    RoleCategory.isCourseClassAdmin(AuthRepo().getUserRoles, courseClassUUID)
    
  def getAuthenticatedPersonUUID = ThreadLocalAuthenticator.getAuthenticatedPersonUUID.getOrElse(null)
  
 
}
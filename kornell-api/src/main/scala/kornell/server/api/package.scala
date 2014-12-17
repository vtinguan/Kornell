package kornell.server

import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.AuthRepo
import kornell.core.entity.RoleCategory
import scala.collection.JavaConverters._
import java.util.logging.Logger
import kornell.server.authentication.ThreadLocalAuthenticator

//TODO: URGENT
package object api {
  val logger = Logger.getLogger("kornell.server.api")

  def isPlatformAdmin:Boolean = true//RoleCategory.isPlatformAdmin(authRepo.getUserRoles)
    
  def isInstitutionAdmin(institutionUUID:String):Boolean = true
    //RoleCategory.isInstitutionAdmin(authRepo.getUserRoles, institutionUUID)
    
  def isCourseClassAdmin(courseClassUUID:String):Boolean = true
    //RoleCategory.isCourseClassAdmin(authRepo.getUserRoles, courseClassUUID)
    
  def getAuthenticatedPersonUUID = ThreadLocalAuthenticator.getAuthenticatedPersonUUID.getOrElse(null)
  
 
}
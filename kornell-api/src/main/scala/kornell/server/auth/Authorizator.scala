package kornell.server.auth

import kornell.core.entity.RoleCategory
import javax.enterprise.context.Dependent
import javax.inject.Inject
import kornell.server.jdbc.repository.AuthRepo

@Dependent
class Authorizator @Inject() (
  val authRepo:AuthRepo){

  def isPlatformAdmin:Boolean = RoleCategory.isPlatformAdmin(authRepo.getUserRoles)
    
  def isInstitutionAdmin(institutionUUID:String):Boolean =
    RoleCategory.isInstitutionAdmin(authRepo.getUserRoles, institutionUUID)
    
  def isCourseClassAdmin(courseClassUUID:String):Boolean = 
    RoleCategory.isCourseClassAdmin(authRepo.getUserRoles, courseClassUUID)
    
  def getAuthenticatedPersonUUID = ThreadLocalAuthenticator.getAuthenticatedPersonUUID.getOrElse(null)
}
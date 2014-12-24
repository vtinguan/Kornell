package kornell.server.api

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import kornell.core.entity.Institution
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.util.Conditional.toConditional
import kornell.server.util.RequirementNotMet
import kornell.core.to.InstitutionRegistrationPrefixesTO
import kornell.server.jdbc.repository.InstitutionRepo
import kornell.core.entity.Roles
import kornell.server.jdbc.repository.RolesRepo
import kornell.core.to.RolesTO
import javax.ws.rs.QueryParam
import kornell.server.jdbc.repository.ChatThreadsRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.jdbc.repository.InstitutionHostNameRepo
import kornell.server.jdbc.repository.InstitutionHostNameRepo
import kornell.core.to.InstitutionHostNamesTO
import kornell.server.util.Identifiable
import javax.inject.Inject
import javax.enterprise.context.Dependent
import kornell.server.jdbc.repository.RolesRepo
import kornell.server.auth.Authorizator

@Dependent
class InstitutionResource @Inject()(   
    val auth:Authorizator,
    val chatThreadsRepo:ChatThreadsRepo,
    val rolesRepo:RolesRepo)
  extends Identifiable {
  
  def this() = this(null,null,null)

  
  @GET
  @Produces(Array(Institution.TYPE))
  def get =  {
    InstitutionRepo(uuid).get
   }.requiring(auth.isPlatformAdmin, AccessDeniedErr()).get
  
  @PUT
  @Consumes(Array(Institution.TYPE))
  @Produces(Array(Institution.TYPE))
  def update(institution: Institution) = {
    InstitutionRepo(uuid).update(institution)
  }.requiring(auth.isPlatformAdmin, RequirementNotMet).get
  
  @GET
  @Produces(Array(InstitutionRegistrationPrefixesTO.TYPE))
  @Path("registrationPrefixes")
  def getRegistrationPrefixes() = {
    InstitutionRepo(uuid).getRegistrationPrefixes
  }.requiring(auth.isPlatformAdmin, RequirementNotMet).get
  
  @PUT
  @Consumes(Array(Roles.TYPE))
  @Produces(Array(Roles.TYPE))
  @Path("admins")
  def updateAdmins(roles: Roles) = {
        val r = rolesRepo.updateInstitutionAdmins(uuid, roles)
        chatThreadsRepo.updateParticipantsInCourseClassSupportThreadsForInstitution(uuid)
        r
  }.requiring(auth.isPlatformAdmin, AccessDeniedErr())
   .get

  @GET
  @Produces(Array(RolesTO.TYPE))
  @Path("admins")
  def getAdmins(@QueryParam("bind") bindMode:String) = {
        rolesRepo.getInstitutionAdmins(uuid, bindMode)
  }.requiring(auth.isPlatformAdmin, AccessDeniedErr())
   .get
   
  @PUT
  @Consumes(Array(InstitutionHostNamesTO.TYPE))
  @Produces(Array(InstitutionHostNamesTO.TYPE))
  @Path("hostnames")
  def updateHostnames(hostnames: InstitutionHostNamesTO) = {
      InstitutionHostNameRepo(uuid).updateHostnames(hostnames)
  }.requiring(auth.isPlatformAdmin, AccessDeniedErr())
   .get

  @GET
  @Produces(Array(InstitutionHostNamesTO.TYPE))
  @Path("hostnames")
  def getHostnames() = {
        InstitutionHostNameRepo(uuid).get
  }.requiring(auth.isPlatformAdmin, AccessDeniedErr())
   .get
}

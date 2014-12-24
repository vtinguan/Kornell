package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.CourseRepo
import kornell.core.entity.Course
import kornell.server.util.Conditional.toConditional
import kornell.server.util.RequirementNotMet
import kornell.server.jdbc.repository.PersonRepo
import javax.ws.rs.Consumes
import javax.ws.rs.PUT
import javax.ws.rs.DELETE
import kornell.server.util.Identifiable
import javax.enterprise.context.Dependent
import javax.inject.Inject
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.auth.Authorizator

@Dependent
class CourseResource @Inject()(
  val auth:Authorizator,
  val peopleRepo:PeopleRepo		
  ) extends Identifiable {
  
  def this() = this(null,null)
  
  @GET
  @Produces(Array(Course.TYPE))
  def get = {
    CourseRepo(uuid).get
  }.requiring(auth.isPlatformAdmin, RequirementNotMet)
   .or(auth.isInstitutionAdmin(peopleRepo.byUUID(auth.getAuthenticatedPersonUUID).get.getInstitutionUUID), RequirementNotMet)
   .get
   
  @PUT
  @Consumes(Array(Course.TYPE))
  @Produces(Array(Course.TYPE))
  def update(course: Course) = {
    CourseRepo(uuid).update(course)
  }.requiring(auth.isPlatformAdmin, RequirementNotMet)
   .or(auth.isInstitutionAdmin(peopleRepo.byUUID(auth.getAuthenticatedPersonUUID).get.getInstitutionUUID), RequirementNotMet)
   .get
}
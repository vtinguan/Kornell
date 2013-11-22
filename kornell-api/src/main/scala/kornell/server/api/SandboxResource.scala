package kornell.server.api

import javax.ws.rs.Path
import kornell.core.to.UserInfoTO
import kornell.server.util.Randoms._
import kornell.server.repository.jdbc.People
import kornell.server.repository.TOs
import javax.ws.rs.GET
import javax.ws.rs.Produces
import kornell.core.entity.EnrollmentState
import kornell.server.repository.jdbc.Enrollments

@Path("sandbox")
class SandboxResource {

  @Produces(Array(UserInfoTO.TYPE))
  @Path("genUser")
  @GET
  def genUser: UserInfoTO = {
    val fullName = "Fulano de Tal"
    val username = randomString
    val password = randomString
    val institution_uuid = "00a4966d-5442-4a44-9490-ef36f133a259";
    val course_uuid = "d9aaa03a-f225-48b9-8cc9-15495606ac46";

    val personRepo = People().createTestPerson(fullName)
    val person = personRepo.get().get
    
    val enrollmnt = personRepo.setPassword(username, password)
      .registerOn(institution_uuid)
    Enrollments().createEnrollment(course_uuid, person.getUUID(),EnrollmentState.requested)
      

     val user = TOs.newUserInfoTO
     user.setEmail(person.getEmail)
     //TODO: Care for the multiple institution user
     user.setInstitutionAssetsURL("")
     user.setPerson(person)
     user.setUsername(username)
     user
  }

}
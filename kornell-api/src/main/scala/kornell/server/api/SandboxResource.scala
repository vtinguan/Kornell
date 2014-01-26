package kornell.server.api

import javax.ws.rs.Produces
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.repository.TOs
import kornell.server.jdbc.repository.PeopleRepo
import javax.ws.rs.GET
import kornell.core.to.UserInfoTO
import javax.ws.rs.Path
import kornell.core.entity.EnrollmentState

@Path("sandbox")
class SandboxResource {
}
package kornell.server.cdi

import javax.enterprise.context.ApplicationScoped
import kornell.server.jdbc.repository.AuthRepo
import javax.enterprise.inject.Produces

@ApplicationScoped
class AuthRepoProducer {
	@Produces
	@ApplicationScoped
	@Preferred
	def createAuthRepo = AuthRepo()
}
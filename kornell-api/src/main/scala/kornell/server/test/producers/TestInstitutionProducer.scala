package kornell.server.test.producers

import javax.enterprise.context.Dependent
import kornell.server.test.util.Generator
import javax.inject.Inject
import kornell.server.jdbc.repository.InstitutionsRepo
import javax.enterprise.inject.Produces
import kornell.core.entity.Institution
import kornell.server.repository.Entities
import javax.enterprise.context.ApplicationScoped
import kornell.core.entity.BillingType

@Dependent
class TestInstitutionProducer @Inject() (
  val ittsRepo: InstitutionsRepo)
  extends Producer {

  @Produces
  def institution: Institution = ittsRepo.create(uuid = randUUID,
    name = randName,
    baseURL = randURL,
    billingType = BillingType.enrollment)
}
package kornell.server.api

import javax.ws.rs.Path
import com.newrelic.api.agent.NewRelic
import javax.ws.rs.GET

@Path("newrelic")
class NewRelicResource {
	@Path("browserTimingHeader")
	@GET
	def getBrowserTimingHeader = NewRelic.getBrowserTimingHeader
	
	@Path("browserTimingFooter")
	@GET
	def getBrowserTimingFooter = NewRelic.getBrowserTimingFooter
}
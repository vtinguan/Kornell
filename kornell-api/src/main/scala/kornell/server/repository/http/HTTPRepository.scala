package kornell.server.repository.http

import kornell.core.util.StringUtils._

class HTTPRepository(distributionURL:String,prefix:String) {
  val baseURL = composeURL(distributionURL,prefix)
  

}
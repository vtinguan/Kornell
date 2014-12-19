package kornell.server.authentication

object ThreadLocalAuthenticator {
  
	val threadLocal = new ThreadLocal[String]
	
	def setAuthenticatedPersonUUID(personUUID:String) = {
	  logger.finer(s"Thread assumed identity [$personUUID]")
	  threadLocal.set(personUUID)
	  personUUID
	}
	
	def getAuthenticatedPersonUUID() = Option(threadLocal.get)
	
	def clearAuthenticatedPersonUUID = {
	  logger.finer(s"Thread is now anonymous")
	  threadLocal.set(null)
	}
}
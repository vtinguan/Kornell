package kornell.server.auth

//TODO: Consider nesting authentication
object ThreadLocalAuthenticator {
  
	val threadLocal = new ThreadLocal[String]
	
	def setAuthenticatedPersonUUID(personUUID:String) = {
	  val authPerson = getAuthenticatedPersonUUID
	  if (authPerson.isDefined)
		  logger.warning(s"!!! Thread changing identity [ ${authPerson.get} -> ${personUUID} ]")
	  logger.finer(s"*** Thread assumed identity [$personUUID]")
	  threadLocal.set(personUUID)
	  personUUID
	}
	
	def getAuthenticatedPersonUUID : Option[String] = {
	  val authPersonUUID = Option(threadLocal.get)
	  logger.finest("*** Thread user is "+authPersonUUID)
	  authPersonUUID
	}
	
	def clearAuthenticatedPersonUUID = {
	  logger.finer(s"*** Thread is now anonymous")
	  threadLocal.set(null)
	}
}
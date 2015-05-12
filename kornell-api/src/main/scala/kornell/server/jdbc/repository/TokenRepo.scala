	package kornell.server.jdbc.repository

import kornell.core.entity.AuthClientType
import kornell.server.jdbc.SQL._
import org.joda.time.DateTime
import java.util.Date
import kornell.server.repository.TOs
import kornell.core.to.TokenTO

object TokenRepo {

  def checkToken(token: String) = {
	sql"""select * from Token where token = ${token}""".first[TokenTO]
  }
  
  def getToken(personUUID: String) = {
    sql"""select * from Token where personUUID = ${personUUID}""".first[TokenTO]
  }
  
  def deleteToken(token: String) = {
    sql"""delete from Token where token = ${token}""".executeUpdate
  }
  
  def createToken(token: String, personUUID: String, authClientType: AuthClientType) = {
    val expiry = {
      if (authClientType == AuthClientType.web) {
        new DateTime(new Date).plusDays(7).toDate
      } else {
        null
      }
    }
    sql"""insert into Token (token, personUUID, expiry, clientType) values
      	(${token}, ${personUUID}, ${expiry}, ${authClientType.toString})""".executeUpdate
      	
    TOs.newTokenTO(token, expiry, personUUID, authClientType)
  }
}

package kornell.server.jdbc.repository

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.bufferAsJavaListConverter
import kornell.server.jdbc.SQL.SQLHelper
import kornell.core.entity.ChatThread
import kornell.core.util.UUID
import java.util.Date
import kornell.server.repository.Entities
import java.sql.ResultSet

class ChatThreadsRepo {
}

object ChatThreadsRepo {
  
  def getSupportChatThreadName(courseClassName: String) = "Suporte: " + courseClassName

  def postMessageToCourseClassSupportThread(personUUID: String, courseClassUUID: String, message: String) = {
      val courseClass = CourseClassRepo(courseClassUUID).get
      val chatThreadUUID = getChatThreadUUID(personUUID, courseClass.getUUID)
      if(!chatThreadUUID.isDefined){
        val chatThread = createChatThread(getSupportChatThreadName(courseClass.getName), courseClass.getInstitutionUUID)
        updateChatThreadParticipants(personUUID, chatThread, courseClass.getUUID)
        createCourseClassSupportChatThread(chatThread.getUUID, courseClass.getUUID, personUUID)
        createChatThreadMessage(chatThread.getUUID, personUUID, message)
      } else {
      	createChatThreadMessage(chatThreadUUID.get, personUUID, message)
      }
      ""
  }

  def updateChatThreadParticipants(personUUID: String, chatThread: ChatThread, courseClassUUID: String) = {
    val participantsThatShouldExist = RolesRepo.getCourseClassThreadSupportParticipants(courseClassUUID, chatThread.getInstitutionUUID, null)
    		.getRoleTOs.asScala.map(_.getRole.getPersonUUID).union(List(personUUID))
    val participants = getChatTreadParticipantsUUIDs(chatThread.getUUID)
    val createThese = participantsThatShouldExist.filterNot(participants.contains)
    val removeThese = participants.filterNot(participantsThatShouldExist.contains)
    createThese.foreach(createChatThreadParticipant(chatThread.getUUID, _))
    removeThese.foreach(removeChatThreadParticipant(chatThread.getUUID, _))
  }

  def createChatThreadParticipant(chatThreadUUID: String, personUUID: String) = {
    sql"""
		insert into ChatThreadParticipant (uuid, chatThreadUUID, personUUID, lastReadAt)
		values (${UUID.random}, ${chatThreadUUID} , ${personUUID}, null)""".executeUpdate
  }

  def removeChatThreadParticipant(chatThreadUUID: String, personUUID: String) = {
    sql"""
      delete from ChatThreadParticipant 
      where chatThreadUUID = ${chatThreadUUID}
    	and personUUID = ${personUUID}""".executeUpdate
  }

  def createChatThread(name: String, institutionUUID: String): ChatThread = {
      createChatThread(Entities.newChatThread(UUID.random, name, new Date(), institutionUUID))
  }

  def createChatThread(chatThread: ChatThread): ChatThread = {
    if (chatThread.getUUID == null)
      chatThread.setUUID(UUID.random)
    sql"""
		insert into ChatThread (uuid, name, createdAt, institutionUUID)
		values (${chatThread.getUUID}, ${chatThread.getName} , ${chatThread.getCreatedAt}, ${chatThread.getInstitutionUUID})
	  """.executeUpdate
	  chatThread
  }
 
  def createCourseClassSupportChatThread(chatThreadUUID: String, courseClassUUID: String, personUUID: String) = {
    sql"""
		insert into CourseClassSupportChatThread (uuid, chatThreadUUID, courseClassUUID, personUUID)
		values (${UUID.random}, ${chatThreadUUID} , ${courseClassUUID}, ${personUUID})
	  """.executeUpdate
  }

  def createChatThreadMessage(chatThreadUUID: String, personUUID: String, message: String) = {
    sql"""
		insert into ChatThreadMessage (uuid, chatThreadUUID, sentAt, personUUID, message)
		values (${UUID.random}, ${chatThreadUUID} , ${new Date()}, ${personUUID}, ${message})
	  """.executeUpdate
  }
  
  def getChatThreadUUID(personUUID: String, courseClassUUID: String) = {
    sql"""
		    | select st.chatThreadUUID
	      	| from CourseClassSupportChatThread st
    			| where st.personUUID = ${personUUID}
    			| and st.courseClassUUID = ${courseClassUUID}
		    """.first[String](toString)
  }
  
  def updateParticipantsInCourseClassSupportThreads(courseClassUUID: String) = {
    ???
  }
  
  def updateCourseClassSupportThreadsNames(courseClassUUID: String) = {
    ???
  }
  
  implicit def toString(rs: ResultSet): String = rs.getString(1)
      
  def getChatTreadParticipantsUUIDs(chatThreadUUID: String) = {
    sql"""
		    | select ctp.uuid
	      	| from ChatThreadParticipant ctp
    			| where ctp.chatThreadUUID = ${chatThreadUUID}
		    """.map[String](toString)
  }
  
  def getTotalUnreadCountByPerson(personUUID: String, institutionUUID: String) = {
    sql"""
		    | select count(tm.uuid) as unreadMessages
					| from ChatThreadMessage tm
					| join (
						| select t.uuid as chatThreadUUID,
						| tp.lastReadAt as threadLastReadAt,
						| ccs.courseClassUUID
						| from ChatThread t
						| join ChatThreadParticipant tp on t.uuid = tp.chatThreadUUID
						| left join CourseClassSupportChatThread ccs on ccs.chatThreadUUID = t.uuid
						| where tp.PersonUUID = ${personUUID}
						| and t.institutionUUID = ${institutionUUID}
					| ) countByCC on tm.chatThreadUUID = countByCC.chatThreadUUID
					| where (countByCC.threadLastReadAt < tm.sentAt or countByCC.threadLastReadAt is null)
		    """.first[String](toString).get
  }
}
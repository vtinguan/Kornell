package kornell.server.jdbc.repository

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.bufferAsJavaListConverter
import kornell.server.jdbc.SQL.SQLHelper
import kornell.core.entity.ChatThread
import kornell.core.util.UUID
import java.util.Date
import kornell.server.repository.Entities

class ChatThreadsRepo {
}

object ChatThreadsRepo {
  
  def getSupportChatThreadName(courseClassName: String) = "Suporte: " + courseClassName

  def postMessageToCourseClassSupportThread(personUUID: String, courseClassUUID: String, message: String) = {
      val courseClass = CourseClassRepo(courseClassUUID).get
      var thread = getCourseClassSupportChatThread(personUUID, courseClass.getUUID)
      if(thread == null){
        thread = createChatThread(getSupportChatThreadName(courseClass.getName), courseClass.getInstitutionUUID)
        updateChatThreadParticipants(thread)
        createCourseClassSupportChatThread(courseClass.getUUID, thread.getUUID, personUUID)
      }
      createChatThreadMessage(thread.getUUID, personUUID, message)
  }

  def updateChatThreadParticipants(thread: ChatThread) = {
    ??? 
  }

  def createChatThread(name: String, institutionUUID: String): ChatThread = {
      createChatThread(Entities.newChatThread(UUID.random, name, new Date(), institutionUUID))
  }

  def createChatThread(chatThread: ChatThread): ChatThread = {
    if (chatThread.getUUID == null)
      chatThread.setUUID(UUID.random)
    sql"""
		insert into ChatThread (uuid, name, createdAt, institutionUUID)
		values (${chatThread.getUUID}, ${chatThread.getName} , ${chatThread.getCreatedAt}, ${chatThread.getInstitutionUUID},)
	  """.executeUpdate
	  chatThread
  }
 
  def createCourseClassSupportChatThread(arg: String, arg1: Any, personUUID: String) = {
    //UUID.random
    ???
  }

  def createChatThreadMessage(threadUUID: String, personUUID: String, message: String) = {
    //UUID.random
    ???
  }
  
  def getCourseClassSupportChatThread(personUUID: String, courseClassUUID: String): ChatThread = {
    ???
  }
  
  def updateParticipantsInCourseClassSupportThreads(courseClassUUID: String) = {
    ???
  }
  
  def updateCourseClassSupportThreadsNames(courseClassUUID: String) = {
    ???
  }
}
package kornell.server.jdbc.repository

import scala.collection.JavaConverters.setAsJavaSetConverter
import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.bufferAsJavaListConverter
import kornell.server.jdbc.SQL.SQLHelper
import kornell.core.entity.ChatThread
import kornell.core.util.UUID
import java.util.Date
import kornell.server.repository.Entities
import java.sql.ResultSet
import kornell.core.to.UnreadChatThreadsTO
import kornell.server.repository.TOs
import kornell.core.to.UnreadChatThreadTO
import kornell.core.to.ChatThreadMessageTO
import kornell.core.entity.CourseClass
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import java.util.concurrent.TimeUnit._
import kornell.core.entity.Person
import kornell.core.to.ChatThreadMessagesTO

class ChatThreadsRepo {
}

object ChatThreadsRepo {
  
  val supportThreadType = "SUPPORT";
  val tutoringThreadType = "TUTORING"
  
  def getSupportChatThreadName(courseClassName: String): String = "Ajuda para turma: " + courseClassName
  def getSupportChatThreadNameAdminPerspective(courseClassName: String): String = "\n (Ajuda para turma: " + courseClassName + ")"
  def getSupportChatThreadNameAdminPerspective(userFullName: String, courseClassName: String): String = userFullName + getSupportChatThreadNameAdminPerspective(courseClassName)
  
  def getTutoringChatThreadName(courseClassName: String): String = "Tutor para turma: " + courseClassName
  def getTutoringChatThreadNameAdminPerspective(courseClassName: String): String = "\n (Tutor para turma: " + courseClassName + ")"
  def getTutoringChatThreadNameAdminPerspective(userFullName: String, courseClassName: String): String = userFullName + getTutoringChatThreadNameAdminPerspective(courseClassName)

  def postMessageToCourseClassSupportThread(personUUID: String, courseClassUUID: String, message: String, supportType: String) = {
      val courseClass = CourseClassRepo(courseClassUUID).get
      val chatThreadUUID = getCourseClassSupportChatThreadUUID(personUUID, courseClass.getUUID, supportType)
      if(!chatThreadUUID.isDefined){
        val chatThread = createChatThread(courseClass.getInstitutionUUID)
        updateChatThreadParticipants(chatThread.getUUID, personUUID, courseClass, supportType)
        createCourseClassSupportChatThread(chatThread.getUUID, courseClass.getUUID, personUUID, supportType)
        createChatThreadMessage(chatThread.getUUID, personUUID, message)
      } else {
      	createChatThreadMessage(chatThreadUUID.get, personUUID, message)
      }
  }

  def updateChatThreadParticipants(chatThreadUUID: String, threadCreatorUUID: String, courseClass: CourseClass, supportType: String) = {
    val participantsThatShouldExist = supportType match {
      case `supportThreadType` => RolesRepo.getCourseClassThreadSupportParticipants(courseClass.getUUID, courseClass.getInstitutionUUID, null)
            .getRoleTOs.asScala.map(_.getRole.getPersonUUID).+:(threadCreatorUUID).toList.distinct
      case `tutoringThreadType` =>RolesRepo.getTutorsForCourseClass(courseClass.getCourseVersionUUID())
          .getRoleTOs.asScala.map(_.getRole.getPersonUUID).+:(threadCreatorUUID).toList.distinct
    }

    val participants = getChatTreadParticipantsUUIDs(chatThreadUUID).distinct
    val createThese = participantsThatShouldExist.filterNot(participants.contains)
    val removeThese = participants.filterNot(participantsThatShouldExist.contains)
    createThese.foreach(createChatThreadParticipant(chatThreadUUID, _, courseClass.getName, threadCreatorUUID))
    removeThese.foreach(removeChatThreadParticipant(chatThreadUUID, _))
  }
  
  def getChatTreadParticipantsUUIDs(chatThreadUUID: String) = sql"""
		    | select p.uuid
	      	| from ChatThreadParticipant ctp 
  			  | join Person p on p.uuid = ctp.personUUID
    			| where ctp.chatThreadUUID = ${chatThreadUUID}
		    """.map[String]

  def createChatThreadParticipant(chatThreadUUID: String, personUUID: String, courseClassName: String, threadCreatorUUID: String) = {
    val chatThreadName = {
      if(personUUID.equals(threadCreatorUUID))
      	getSupportChatThreadName(courseClassName)
      else
        getSupportChatThreadNameAdminPerspective(PeopleRepo.uuidLoader.load(threadCreatorUUID).get.getFullName(), courseClassName)
    }
    sql"""
			insert into ChatThreadParticipant (uuid, chatThreadUUID, personUUID, lastReadAt, chatThreadName)
			values (${UUID.random}, ${chatThreadUUID} , ${personUUID}, ${new Date()}, ${chatThreadName})""".executeUpdate
  }

  def removeChatThreadParticipant(chatThreadUUID: String, personUUID: String) = {
    sql"""
      delete from ChatThreadParticipant 
      where chatThreadUUID = ${chatThreadUUID}
    	and personUUID = ${personUUID}""".executeUpdate
  }

  def createChatThread(institutionUUID: String): ChatThread = {
      createChatThread(Entities.newChatThread(UUID.random, new Date(), institutionUUID))
  }

  def createChatThread(chatThread: ChatThread): ChatThread = {
    if (chatThread.getUUID == null)
      chatThread.setUUID(UUID.random)
    sql"""
		insert into ChatThread (uuid, createdAt, institutionUUID)
		values (${chatThread.getUUID}, ${chatThread.getCreatedAt}, ${chatThread.getInstitutionUUID})
	  """.executeUpdate
	  chatThread
  }
 
  def createCourseClassSupportChatThread(chatThreadUUID: String, courseClassUUID: String, personUUID: String, supportType: String) = {
    sql"""
		insert into CourseClassSupportChatThread (uuid, chatThreadUUID, courseClassUUID, personUUID, supportType)
		values (${UUID.random}, ${chatThreadUUID} , ${courseClassUUID}, ${personUUID}, ${supportType})
	  """.executeUpdate
  }

  def createChatThreadMessage(chatThreadUUID: String, personUUID: String, message: String) = {
    sql"""
		insert into ChatThreadMessage (uuid, chatThreadUUID, sentAt, personUUID, message)
		values (${UUID.random}, ${chatThreadUUID} , ${new Date()}, ${personUUID}, ${message})
	  """.executeUpdate
  }
  
  def getCourseClassSupportChatThreadUUID(personUUID: String, courseClassUUID: String, supportType: String) = {
    sql"""
		    | select st.chatThreadUUID
	      	| from CourseClassSupportChatThread st
    			| where st.personUUID = ${personUUID}
    			| and st.courseClassUUID = ${courseClassUUID}
    			| and st.supportType = ${supportType}
		    """.first[String]
  }
  
  
  def updateParticipantsInCourseClassSupportThreads(courseClassUUID: String) = {
	  type CourseClassSupportThreadData = Tuple2[String,String] 
	  implicit def courseClassSupportThreadConvertion(rs:ResultSet): CourseClassSupportThreadData = (rs.getString(1), rs.getString(2))
	  
    val courseClass = CourseClassRepo(courseClassUUID).get
    sql"""
    		select chatThreadUUID, personUUID from CourseClassSupportChatThread
    		  where courseClassUUID = ${courseClassUUID}
		    """.map[CourseClassSupportThreadData](courseClassSupportThreadConvertion)
		    .foreach(ct => updateChatThreadParticipants(ct._1, ct._2, courseClass, supportThreadType))
  }
  
  def updateCourseClassSupportThreadsNames(courseClassUUID: String, courseClassName: String) = {
    sql"""
    	update ChatThreadParticipant set ChatThreadParticipant.chatThreadName = ${getSupportChatThreadName(courseClassName)}
				where uuid in (
					select uuid from (
						select tp.uuid as uuid from CourseClassSupportChatThread ccs
						join ChatThread t on ccs.chatThreadUUID = t.uuid
						join ChatThreadParticipant tp on t.uuid = tp.chatThreadUUID and tp.personUUID = ccs.personUUID
						join CourseClass cc on cc.uuid = ccs.courseClassUUID
						where ccs.courseClassUUID = ${courseClassUUID}
					) as ids
    	)""".executeUpdate
    	
    sql"""
    	update ChatThreadParticipant ctp
			inner join CourseClassSupportChatThread sc on sc.chatThreadUUID = ctp.chatThreadUUID
			inner join Person p on p.uuid = sc.personUUID
			set ctp.chatThreadName = concat(p.fullName, ${getSupportChatThreadNameAdminPerspective(courseClassName)})
			where ctp.uuid in (
				select uuid from (
					select tp.uuid from CourseClassSupportChatThread ccs
					join ChatThread t on ccs.chatThreadUUID = t.uuid
					join ChatThreadParticipant tp on t.uuid = tp.chatThreadUUID and tp.personUUID <> ccs.personUUID
					join CourseClass cc on cc.uuid = ccs.courseClassUUID
					where ccs.courseClassUUID = ${courseClassUUID}
				) as ids
			)""".executeUpdate
  }
  
  def getTotalUnreadCountByPerson(personUUID: String, institutionUUID: String) = {
    sql"""
		    | select count(tm.uuid) as unreadMessages
					| from ChatThreadMessage tm
					| join (
						| select t.uuid as chatThreadUUID,
						| tp.lastReadAt as threadLastReadAt
						| from ChatThread t
						| join ChatThreadParticipant tp on t.uuid = tp.chatThreadUUID
						| left join CourseClassSupportChatThread ccs on ccs.chatThreadUUID = t.uuid
						| where tp.PersonUUID = ${personUUID}
						| and t.institutionUUID = ${institutionUUID}
					| ) countByCC on tm.chatThreadUUID = countByCC.chatThreadUUID
					| where (countByCC.threadLastReadAt < tm.sentAt or countByCC.threadLastReadAt is null)
					| and tm.personUUID <> ${personUUID}
		    """.first[String].get
  }
  
  def getTotalUnreadCountsByPersonPerThread(personUUID: String, institutionUUID: String) = {
    TOs.newUnreadChatThreadsTO(sql"""
				| select count(unreadMessages) as unreadMessages, chatThreadUUID, chatThreadName, courseClassUUID
				| from (
					| select tm.uuid as unreadMessages,
						| countByCC.chatThreadUUID,
						| countByCC.chatThreadName,
						| countByCC.courseClassUUID,
						| countByCC.threadLastReadAt,
						| tm.personUUID,
						| tm.sentAt,
						| countByCC.lastSentAt
					| from ChatThread t
					| join (
						| select distinct t.uuid as chatThreadUUID,
							| tp.lastReadAt as threadLastReadAt,
							| ccs.courseClassUUID,
							| tp.chatThreadName as chatThreadName,
							| max(ctm.sentAt) as lastSentAt
						| from ChatThread t
						| join ChatThreadParticipant tp on t.uuid = tp.chatThreadUUID
						| left join CourseClassSupportChatThread ccs on ccs.chatThreadUUID = t.uuid
						| join ChatThreadMessage ctm on ctm.chatThreadUUID = t.uuid
						| where tp.PersonUUID = ${personUUID}
							| and t.institutionUUID = ${institutionUUID}
						| group by (t.uuid)
					| ) countByCC on t.uuid = countByCC.chatThreadUUID
					| left join ChatThreadMessage tm on tm.chatThreadUUID = t.uuid and tm.personUUID <> ${personUUID}
    				| and (threadLastReadAt < sentAt or threadLastReadAt is null)
				| ) as threadMessages
				| group by chatThreadUUID
				| order by unreadMessages desc, lastSentAt desc
		    """.map[UnreadChatThreadTO](toUnreadChatThreadTO))
  }
  
  def getDatabaseTime = sql"""select now()""".first[String].get 

  def getChatThreadMessages(chatThreadUUID: String) = {
    TOs.newChatThreadMessagesTO(sql"""
				| select p.fullName as senderFullName, tm.sentAt, tm.message from
				| 	ChatThreadMessage tm 
				| 	join Person p on p.uuid = tm.personUUID
				| 	where tm.chatThreadUUID = ${chatThreadUUID}
				| 	order by tm.sentAt
		    """.map[ChatThreadMessageTO](toChatThreadMessageTO), 
		    getDatabaseTime)
  } 

  def getChatThreadMessagesSince(chatThreadUUID: String, lastFetchedMessageSentAt: String) = {
    TOs.newChatThreadMessagesTO(sql"""
				| select p.fullName as senderFullName, tm.sentAt, tm.message from
				| 	ChatThreadMessage tm 
				| 	join Person p on p.uuid = tm.personUUID
				| 	where tm.chatThreadUUID = ${chatThreadUUID}
    	  |   and tm.sentAt > ${lastFetchedMessageSentAt}
				| 	order by tm.sentAt
		    """.map[ChatThreadMessageTO](toChatThreadMessageTO),
		    getDatabaseTime)
  } 

  def markAsRead(chatThreadUUID: String, personUUID: String) = {
    sql"""
    	| update ChatThreadParticipant p set
			| 	lastReadAt = ${new Date()}
      | where p.chatThreadUUID = ${chatThreadUUID} 
      | and p.personUUID = ${personUUID}""".executeUpdate
  }
  
  implicit def toString(rs: ResultSet): String = rs.getString(1)
  
}
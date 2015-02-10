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
import kornell.core.entity.RoleCategory
import kornell.core.entity.ChatThreadType
import kornell.core.to.EnrollmentTO
import kornell.core.entity.ChatThreadParticipant
import java.text.SimpleDateFormat
import kornell.core.entity.Enrollment
import kornell.server.util.RequirementNotMet


class ChatThreadsRepo {
}

object ChatThreadsRepo {
  
  def getSupportChatThreadName(courseClassName: String): String = "Ajuda para turma: " + courseClassName
  def getSupportChatThreadNameAdminPerspective(courseClassName: String): String = "\n (Ajuda para turma: " + courseClassName + ")"
  def getSupportChatThreadNameAdminPerspective(userFullName: String, courseClassName: String): String = userFullName + getSupportChatThreadNameAdminPerspective(courseClassName)
  
  def getTutoringChatThreadName(courseClassName: String): String = "Tutor para turma: " + courseClassName
  def getTutoringChatThreadNameAdminPerspective(courseClassName: String): String = "\n (Tutor para turma: " + courseClassName + ")"
  def getTutoringChatThreadNameAdminPerspective(userFullName: String, courseClassName: String): String = userFullName + getTutoringChatThreadNameAdminPerspective(courseClassName)

  def postMessageToCourseClassSupportThread(personUUID: String, courseClassUUID: String, message: String, threadType: ChatThreadType) = {
      val courseClass = CourseClassRepo(courseClassUUID).get
      val chatThreadUUID = getChatThreadUUID(personUUID, courseClass.getUUID, threadType)
      if(!chatThreadUUID.isDefined){
        val chatThread = createChatThread(courseClass.getInstitutionUUID, courseClass.getUUID, personUUID, threadType)
        updateChatThreadParticipants(chatThread.getUUID, personUUID, courseClass, threadType)
        createChatThreadMessage(chatThread.getUUID, personUUID, message)
      } else {
      	createChatThreadMessage(chatThreadUUID.get, personUUID, message)
      }
  }
  
  def postMessageToDirectThread(fromPersonUUID: String, toPersonUUID: String, message: String) = {
    val fromPerson = new PersonRepo(fromPersonUUID).get
    val toPerson = new PersonRepo(toPersonUUID).get
    if (!fromPerson.getInstitutionUUID.equals(toPerson.getInstitutionUUID)) {
      throw new IllegalStateException("Cannot send message to person in another institution")
    }
    //the message may be coming from any of the participants, gotta check both sides
    val chatThreadUUID = {
      val tempThreadUUID = getChatThreadUUID(fromPersonUUID, null, ChatThreadType.DIRECT)
      if (tempThreadUUID.isDefined) {
        tempThreadUUID
      } else {
        val tempThreadToUUID = getChatThreadUUID(toPersonUUID, null, ChatThreadType.DIRECT)
        if (tempThreadToUUID.isDefined) {
          tempThreadToUUID
        } else {
          null
        }
      }
      }
    if (!chatThreadUUID.isDefined) {
      val chatThread = createChatThread(fromPerson.getInstitutionUUID, null, fromPersonUUID, ChatThreadType.DIRECT)
      createChatThreadParticipant(chatThread.getUUID, fromPersonUUID, null, null, toPerson.getFullName)
      createChatThreadParticipant(chatThread.getUUID, toPersonUUID, null, null, fromPerson.getFullName)
      createChatThreadMessage(chatThread.getUUID, fromPersonUUID, message)
    } else {
      createChatThreadMessage(chatThreadUUID.get, fromPersonUUID, message)
    }
  }

  def updateChatThreadParticipants(chatThreadUUID: String, threadCreatorUUID: String, courseClass: CourseClass, threadType: ChatThreadType) = {
    val participantsThatShouldExist = threadType match {
      case ChatThreadType.SUPPORT => RolesRepo.getCourseClassThreadSupportParticipants(courseClass.getUUID, courseClass.getInstitutionUUID, null)
            .getRoleTOs.asScala.map(_.getRole.getPersonUUID).+:(threadCreatorUUID).toList.distinct
      case ChatThreadType.TUTORING =>RolesRepo.getTutorsForCourseClass(courseClass.getCourseVersionUUID(), RoleCategory.BIND_WITH_PERSON)
          .getRoleTOs.asScala.map(_.getRole.getPersonUUID).+:(threadCreatorUUID).toList.distinct
      case ChatThreadType.COURSE_CLASS | ChatThreadType.DIRECT => throw new IllegalStateException("not-supported-for-this-type")
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
		    
  def getChatTreadParticipant(personUUID: String, chatThreadUUID: String) = sql"""
            | select *
            | from ChatThreadParticipant ctp 
            | where ctp.chatThreadUUID = ${chatThreadUUID}
		    | and ctp.personUUID = ${personUUID}
            """.first[ChatThreadParticipant](toChatThreadParticipant)

  def updateChatThreadParticipantActivation(chatThreadParticipantUUID: String, active: Boolean) = {
    if (active) {
      sql"""update ChatThreadParticipant set active = ${active}, lastJoinDate = ${new Date} where uuid = ${chatThreadParticipantUUID}""".executeUpdate
    } else {
      sql"""update ChatThreadParticipant set active = ${active} where uuid = ${chatThreadParticipantUUID}""".executeUpdate
    }
    
  }

  def createChatThreadParticipant(chatThreadUUID: String, personUUID: String, courseClassName: String, threadCreatorUUID: String, targetPersonName: String = null) = {
    val chatThreadName = {
      if (courseClassName == null) {
        "Chat with " + targetPersonName
      }
      if (threadCreatorUUID == null) {
        "Chat for " + courseClassName
      } else if(personUUID.equals(threadCreatorUUID)) {
      	getSupportChatThreadName(courseClassName)
      } else {
        getSupportChatThreadNameAdminPerspective(PeopleRepo.uuidLoader.load(threadCreatorUUID).get.getFullName(), courseClassName)
      }
    }
    sql"""
			insert into ChatThreadParticipant (uuid, chatThreadUUID, personUUID, lastReadAt, chatThreadName, active, lastJoinDate)
			values (${UUID.random}, ${chatThreadUUID} , ${personUUID}, ${new Date()}, ${chatThreadName}, 1, ${new Date()})""".executeUpdate
  }

  def removeChatThreadParticipant(chatThreadUUID: String, personUUID: String) = {
    sql"""
      delete from ChatThreadParticipant 
      where chatThreadUUID = ${chatThreadUUID}
    	and personUUID = ${personUUID}""".executeUpdate
  }

  def createChatThread(institutionUUID: String, courseClassUUID: String, personUUID: String, threadType: ChatThreadType): ChatThread = {
      createChatThread(Entities.newChatThread(UUID.random, new Date(), institutionUUID, courseClassUUID, personUUID, threadType.toString))
  }

  def createChatThread(chatThread: ChatThread): ChatThread = {
    if (chatThread.getUUID == null)
      chatThread.setUUID(UUID.random)
    sql"""
		insert into ChatThread (uuid, createdAt, institutionUUID, courseClassUUID, personUUID, threadType, active)
		values (${chatThread.getUUID}, ${chatThread.getCreatedAt}, ${chatThread.getInstitutionUUID}, ${chatThread.getCourseClassUUID}, ${chatThread.getPersonUUID}, ${chatThread.getThreadType}, 1)
	  """.executeUpdate
	  chatThread
  }

  def createChatThreadMessage(chatThreadUUID: String, personUUID: String, message: String) = {
    sql"""
		insert into ChatThreadMessage (uuid, chatThreadUUID, sentAt, personUUID, message)
		values (${UUID.random}, ${chatThreadUUID} , ${new Date()}, ${personUUID}, ${message})
	  """.executeUpdate
  }
  
  def getChatThreadUUID(personUUID: String, courseClassUUID: String, threadType: ChatThreadType) = {
    sql"""
		    | select uuid
	      	| from ChatThread 
    			| where personUUID = ${personUUID}
    			| and courseClassUUID = ${courseClassUUID}
    			| and threadType = ${threadType.toString}
		    """.first[String]
  }
  
  def updateParticipantsInCourseClassSupportThreadsForInstitution(institutionUUID: String) = {
    sql"""select uuid from CourseClass where institution_uuid = ${institutionUUID}""".map[String](toString)
    .foreach(cc => updateParticipantsInSupportThreads(cc, ChatThreadType.SUPPORT))
  }
  
  def updateParticipantsInSupportThreads(courseClassUUID: String, threadType: ChatThreadType) = {
	  type CourseClassSupportThreadData = Tuple2[String,String] 
	  implicit def courseClassSupportThreadConvertion(rs:ResultSet): CourseClassSupportThreadData = (rs.getString(1), rs.getString(2))
	  
    val courseClass = CourseClassRepo(courseClassUUID).get
    sql"""
    		select uuid, personUUID from ChatThread
    		  where courseClassUUID = ${courseClassUUID}
		    """.map[CourseClassSupportThreadData](courseClassSupportThreadConvertion)
		    .foreach(ct => updateChatThreadParticipants(ct._1, ct._2, courseClass, threadType))
  }
  
  /**
   * Used for when we change a courseClass name so we want to update all courseClass related threads (tutoring and support)
   */
  def updateCourseClassSupportThreadsNames(courseClassUUID: String, courseClassName: String) = {
    sql"""
    	update ChatThreadParticipant set ChatThreadParticipant.chatThreadName = ${getSupportChatThreadName(courseClassName)}
				where uuid in (
					select uuid from (
						select tp.uuid as uuid from ChatThread ct
						join ChatThreadParticipant tp on ct.uuid = tp.chatThreadUUID and tp.personUUID = ct.personUUID
						join CourseClass cc on cc.uuid = ct.courseClassUUID
						where ct.courseClassUUID = ${courseClassUUID} and ct.threadType in (${ChatThreadType.SUPPORT.toString}, ${ChatThreadType.TUTORING.toString})
					) as ids
    	)""".executeUpdate
    	
    sql"""
    	update ChatThreadParticipant ctp
			inner join ChatThread ct on ct.uuid = ctp.chatThreadUUID
			inner join Person p on p.uuid = ct.personUUID
			set ctp.chatThreadName = concat(p.fullName, ${getSupportChatThreadNameAdminPerspective(courseClassName)})
			where ctp.uuid in (
				select uuid from (
					select tp.uuid from ChatThread t
					join ChatThreadParticipant tp on t.uuid = tp.chatThreadUUID and tp.personUUID <> t.personUUID
					join CourseClass cc on cc.uuid = t.courseClassUUID
					where t.courseClassUUID = ${courseClassUUID} and t.threadType in (${ChatThreadType.SUPPORT.toString}, ${ChatThreadType.TUTORING.toString})
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
						| where tp.PersonUUID = ${personUUID}
						| and t.institutionUUID = ${institutionUUID}
					| ) countByCC on tm.chatThreadUUID = countByCC.chatThreadUUID
					| where (countByCC.threadLastReadAt < tm.sentAt or countByCC.threadLastReadAt is null)
					| and tm.personUUID <> ${personUUID}
		    """.first[String].get
  }
  
  def getTotalUnreadCountsByPersonPerThread(personUUID: String, institutionUUID: String) = {
    TOs.newUnreadChatThreadsTO(sql"""
				| select count(unreadMessages) as unreadMessages, chatThreadUUID, chatThreadName, courseClassUUID, threadType
				| from (
					| select tm.uuid as unreadMessages,
						| countByCC.chatThreadUUID,
						| countByCC.chatThreadName,
						| countByCC.courseClassUUID,
						| countByCC.threadLastReadAt,
						| tm.personUUID,
						| tm.sentAt,
						| countByCC.lastSentAt,
    					| t.threadType
					| from ChatThread t
					| join (
						| select distinct t.uuid as chatThreadUUID,
							| tp.lastReadAt as threadLastReadAt,
							| t.courseClassUUID,
							| tp.chatThreadName as chatThreadName,
							| max(ctm.sentAt) as lastSentAt
						| from ChatThread t
						| join ChatThreadParticipant tp on t.uuid = tp.chatThreadUUID
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
  
  def getServerTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(new Date()) 

  def getChatThreadMessages(chatThreadUUID: String) = {
    TOs.newChatThreadMessagesTO(sql"""
				| select p.fullName as senderFullName, tm.sentAt, tm.message from
				| 	ChatThreadMessage tm 
				| 	join Person p on p.uuid = tm.personUUID
				| 	where tm.chatThreadUUID = ${chatThreadUUID}
				| 	order by tm.sentAt
		    """.map[ChatThreadMessageTO](toChatThreadMessageTO), 
		    getServerTime)
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
		    getServerTime)
  } 

  def markAsRead(chatThreadUUID: String, personUUID: String) = {
    sql"""
    	| update ChatThreadParticipant p set
			| 	lastReadAt = ${new Date()}
      | where p.chatThreadUUID = ${chatThreadUUID} 
      | and p.personUUID = ${personUUID}""".executeUpdate
  }
  
  def getCourseClassGlobalChatThread(courseClassUUID: String) = {
    sql"""select * from ChatThread where courseClassUUID = ${courseClassUUID} and threadType =  ${ChatThreadType.COURSE_CLASS.toString}""".first[ChatThread](toChatThread)
  }
  
  def createCourseClassGlobalChatThread(institutionUUID: String, courseClassUUID: String) = {
    sql"""insert into ChatThread (uuid, institutionUUID, courseClassUUID, threadType, createdAt, active) values (
    ${UUID.random}, ${institutionUUID}, ${courseClassUUID}, ${ChatThreadType.COURSE_CLASS.toString}, ${new Date}, 1)
    """.executeUpdate
  }
  
  def updateChatThreadParticipantStatus(chatThreadParticipantUUID: String, status: Boolean) = {
    sql"""update ChatThreadParticipant set active = ${status} where uuid = ${chatThreadParticipantUUID}""".executeUpdate
  }
  
  def updateChatThreadStatus(chatThreadUUID: String, status: Boolean) = {
    sql"""update ChatThread set active = ${status} where uuid = ${chatThreadUUID}""".executeUpdate
  }
  
  /**
   * Call this when enrolling a user
   */
  def addParticipantToCourseClassThread(enrollment: Enrollment) {
    val chatThread = getCourseClassGlobalChatThread(enrollment.getCourseClassUUID)
    val participant = getChatTreadParticipant(enrollment.getPersonUUID, chatThread.get.getUUID)
    if (!participant.isDefined) {
      val courseClass = CourseClassRepo(enrollment.getCourseClassUUID).get
      createChatThreadParticipant(chatThread.get.getUUID, enrollment.getPersonUUID, courseClass.getName, null)
    } else {
      updateChatThreadParticipantStatus(participant.get.getUUID, true)
    }
  }
  
  /**
   * Call this when un-enrolling a user
   */
  def disableParticipantFromCourseClassThread(enrollment: Enrollment) = {
    val chatThread = getCourseClassGlobalChatThread(enrollment.getCourseClassUUID)
    val participant = getChatTreadParticipant(enrollment.getPersonUUID, chatThread.get.getUUID)
    if (participant.isDefined) {
      updateChatThreadParticipantStatus(participant.get.getUUID, false)
    }
  }
  
  /**
   * Call this when updating the course class properties
   */
  def addParticipantsToCourseClassThread(courseClassUUID: String) = {
    val courseClass = CourseClassRepo(courseClassUUID).get
    val chatThread = getCourseClassGlobalChatThread(courseClassUUID)
    if (courseClass.isCourseClassChatEnabled) {
        if (!chatThread.isDefined) {
          //create new chat thread
            val createdChatThread = createChatThread(courseClass.getInstitutionUUID, courseClassUUID, null, ChatThreadType.COURSE_CLASS)
            //add everyone in the class to the thread
            EnrollmentsRepo.byCourseClass(courseClassUUID).getEnrollmentTOs().asScala.foreach(enrollment => {
              val participant = getChatTreadParticipant(enrollment.getPersonUUID, chatThread.get.getUUID)
              if (!participant.isDefined) {
                  createChatThreadParticipant(chatThread.get.getUUID, enrollment.getPersonUUID, courseClass.getName, null)
              }})
        } else {
            //chat is enabled on class and thread exists
          if (!chatThread.get.isActive) {
            updateChatThreadStatus(chatThread.get.getUUID, true)
          }
        }
    } else {
        if (chatThread.isDefined) {
          //Chat thread exists but property is disabled on course class
          updateChatThreadStatus(chatThread.get.getUUID, false)
        }
    }
  }
  
  implicit def toString(rs: ResultSet): String = rs.getString(1)
  
}
package kornell.api.client;

import kornell.core.to.ChatThreadMessagesTO;
import kornell.core.to.UnreadChatThreadsTO;

public class ChatThreadsClient extends RESTClient {

	public void postMessageToSupportCourseClassThread(String message, String courseClassUUID, Callback<String> callback) {
		POST("/chatThreads/courseClass/" + courseClassUUID + "/support").sendRequest(message, callback);
	}

	public void postMessageToTutoringCourseClassThread(String message, String courseClassUUID, Callback<String> callback) {
		POST("/chatThreads/courseClass/" + courseClassUUID + "/tutoring").sendRequest(message, callback);
	}

	public void postMessageToSupportInstitutionThread(String message, String courseClassUUID, Callback<String> callback) {
		POST("/chatThreads/courseClass/" + courseClassUUID + "/institutionSupport").sendRequest(message, callback);
	}

	public void postMessageToSupportPlatformThread(String message, Callback<String> callback) {
		POST("/chatThreads/platformSupport").sendRequest(message, callback);
	}
	
	public void postMessageToDirectThread(String message, String personUUID, Callback<Void> callback) {
		POST("/chatThreads/direct/" + personUUID).sendRequest(message, callback);
	}
	
	public void postMessageToChatThread(String message, String chatThreadUUID, Callback<ChatThreadMessagesTO> callback) {
		postMessageToChatThread(message, chatThreadUUID, "", callback);
	}
	
	public void postMessageToChatThread(String message, String chatThreadUUID, String since, Callback<ChatThreadMessagesTO> callback) {
		POST("/chatThreads/"+chatThreadUUID+"/message/?since="+since).sendRequest(message, callback);
	}
	
	public void getTotalUnreadCount(String institutionUUID, Callback<String> callback) {
		GET("/chatThreads/unreadCount/?institutionUUID=" + institutionUUID).sendRequest(null, callback);
	}
	
	public void getTotalUnreadCountsPerThread(String institutionUUID, Callback<UnreadChatThreadsTO> callback) {
		GET("/chatThreads/unreadCountPerThread/?institutionUUID=" + institutionUUID).sendRequest(null, callback);
	}
	
	public void getChatThreadMessagesx(String chatThreadUUID, Callback<ChatThreadMessagesTO> callback) {
		getChatThreadMessages(chatThreadUUID, "", "", callback);
	}
	
	public void getChatThreadMessages(String chatThreadUUID, String since, String before, Callback<ChatThreadMessagesTO> callback) {
		GET("/chatThreads/"+chatThreadUUID+"/messages/?since="+since+"&before="+before).sendRequest(null, callback);
	}

}

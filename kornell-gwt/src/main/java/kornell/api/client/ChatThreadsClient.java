package kornell.api.client;

import kornell.core.to.ChatThreadMessagesTO;
import kornell.core.to.UnreadChatThreadsTO;

public class ChatThreadsClient extends RESTClient {

	public void postMessageToCourseClassThread(String message, String courseClassUUID, Callback<Void> callback) {
		POST("/chatThreads/courseClass/" + courseClassUUID).sendRequest(message, callback);
	}
	
	public void postMessageToChatThread(String message, String chatThreadUUID, Callback<Void> callback) {
		POST("/chatThreads/"+chatThreadUUID+"/message").sendRequest(message, callback);
	}
	
	public void getTotalUnreadCount(String institutionUUID, Callback<String> callback) {
		GET("/chatThreads/unreadCount/?institutionUUID=" + institutionUUID).sendRequest(null, callback);
	}
	
	public void getTotalUnreadCountsPerThread(String institutionUUID, Callback<UnreadChatThreadsTO> callback) {
		GET("/chatThreads/unreadCountPerThread/?institutionUUID=" + institutionUUID).sendRequest(null, callback);
	}
	
	public void getChatThreadMessages(String chatThreadUUID, Callback<ChatThreadMessagesTO> callback) {
		GET("/chatThreads/"+chatThreadUUID+"/messages").sendRequest(null, callback);
	}

}

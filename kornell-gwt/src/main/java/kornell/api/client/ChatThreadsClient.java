package kornell.api.client;

import kornell.core.entity.ChatThread;

public class ChatThreadsClient extends RESTClient {
	
	public void postMessageToCourseClassThread(String message, String courseClassUUID, Callback<Void> callback) {
		POST("/chatThreads/courseClass/" + courseClassUUID).sendRequest(message, callback);
	}
	
	/*public void postMessageToInstitutionThread(String message, String institutionUUID, Callback<Thread> callback) {
		POST("/threads/institution/" + institutionUUID).sendRequest(message, callback);
	}*/
	
	public void getTotalUnreadCount(String institutionUUID, Callback<String> callback) {
		GET("/chatThreads/unreadCount/?institutionUUID=" + institutionUUID).sendRequest(null, callback);
	}

}

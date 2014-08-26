package kornell.api.client;

import kornell.core.entity.ChatThread;

public class ChatThreadsClient extends RESTClient {
	
	public void postMessageToCourseClassThread(String message, String courseClassUUID, Callback<ChatThread> callback) {
		POST("/chatThreads/courseClass/" + courseClassUUID).sendRequest(message, callback);
	}
	
	/*public void postMessageToInstitutionThread(String message, String institutionUUID, Callback<Thread> callback) {
		POST("/threads/institution/" + institutionUUID).sendRequest(message, callback);
	}*/
	
	public void getCourseClassMessages(String courseClassUUID, Callback<ChatThread> callback) {
		GET("/chatThreads/?courseClassUUID=" + courseClassUUID).sendRequest(null, callback);
	}

}

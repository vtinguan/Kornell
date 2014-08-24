package kornell.api.client;

import kornell.core.entity.Message;

public class MessagesClient extends RESTClient {
	
	public void sendMessageToCourseClassAdmin(Message message, String institutionUUID, String courseClassUUID, Callback<Message> callback) {
		POST("/messages/?institutionUUID=" + institutionUUID + "&courseClassUUID=" + courseClassUUID).withContentType(Message.TYPE).withEntityBody(message).go(callback);
	}
	
	public void sendMessageToInstitutionAdmin(Message message, String institutionUUID, Callback<Message> callback) {
		POST("/messages/?institutionUUID=" + institutionUUID).withContentType(Message.TYPE).withEntityBody(message).go(callback);
	}
	
	public void getCourseClassMessages(String courseClassUUID, Callback<Message> callback) {
		POST("/messages/?courseClassUUID=" + courseClassUUID).sendRequest(null, callback);
	}

}

package kornell.api.client;

import kornell.core.entity.CourseClass;
import kornell.core.entity.Message;

public class MessagesClient extends RESTClient {
	
	public void create(Message message, Callback<Message> callback) {
		POST("/messages").withContentType(Message.TYPE).withEntityBody(message).go(callback);
	}

}

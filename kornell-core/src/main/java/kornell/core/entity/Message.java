package kornell.core.entity;

import java.util.Date;

public interface Message extends Entity {
	public static String TYPE = EntityFactory.PREFIX + "message+json";

	String getSubject();
	void setSubject(String subject);
	
	String getBody();
	void setBody(String body);

	String getSenderUUID();
	void setSenderUUID(String senderUUID);

	String getParentMessageUUID();
	void setParentMessageUUID(String parentMessageUUID);
	
	Date getSentAt();
	void setSentAt(Date sentAt);
}

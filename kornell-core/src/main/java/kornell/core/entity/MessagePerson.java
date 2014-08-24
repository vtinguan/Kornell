package kornell.core.entity;

import java.util.Date;

public interface MessagePerson extends Entity {
	public static String TYPE = EntityFactory.PREFIX + "messagePerson+json";

	String getMessageUUID();
	void setMessageUUID(String messageUUID);

	String getRecipientUUID();
	void setRecipientUUID(String recipientUUID);
	
	MessageType getMessageType();
	void setMessageType(MessageType messageType);

	String getInstitutionUUID();
	void setInstitutionUUID(String institutionUUID);
	
	Date getReadAt();
	void setReadAt(Date readAt);
	
	Date getArchivedAt();
	void setArchivedAt(Date archivedAt);
}

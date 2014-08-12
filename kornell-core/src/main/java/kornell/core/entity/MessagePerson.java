package kornell.core.entity;

import java.math.BigDecimal;
import java.util.Date;

public interface MessagePerson extends Entity {
	public static String TYPE = EntityFactory.PREFIX + "messagePerson+json";

	String getMessageUUID();
	void setMessageUUID(String messageUUID);

	String getRecipientUUID();
	void setRecipientUUID(String recipientUUID);

	String getInstitutionUUID();
	void setInstitutionUUID(String institutionUUID);
	
	Date getReadAt();
	void setReadAt(Date readAt);
	
	Date getArchivedAt();
	void setArchivedAt(Date archivedAt);
}

package kornell.core.to;

import kornell.core.entity.RoleType;

public interface ChatThreadMessageTO {
	public static final String TYPE = TOFactory.PREFIX+"chatThreadMessage+json";

	String getSenderFullName();
	void setSenderFullName(String senderFullName);
	
	RoleType getSenderRole();
	void setSenderRole(RoleType senderRole);

	String getSentAt();
	void setSentAt(String sentAt);

	String getMessage();
	void setMessage(String message);
	
}
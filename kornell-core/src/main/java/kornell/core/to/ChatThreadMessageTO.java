package kornell.core.to;

public interface ChatThreadMessageTO {
	public static final String TYPE = TOFactory.PREFIX+"chatThreadMessage+json";

	String getSenderFullName();
	void setSenderFullName(String senderFullName);

	String getSentAt();
	void setSentAt(String sentAt);

	String getMessage();
	void setMessage(String message);
	
}
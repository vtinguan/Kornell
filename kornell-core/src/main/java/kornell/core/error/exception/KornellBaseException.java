package kornell.core.error.exception;

@SuppressWarnings("serial")
public class KornellBaseException extends Exception {
	
	private String messageKey;
	
	public KornellBaseException(String messageKey) {
		this.messageKey = messageKey;
	}
	
	public KornellBaseException(String messageKey, Exception cause) {
		super(cause);
		this.messageKey = messageKey;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
}

package kornell.core.error.exception;

public class ServerErrorException extends KornellBaseException {
	
	public ServerErrorException(String messageKey) {
		super(messageKey);
	}
	
	public ServerErrorException(String messageKey, Exception cause) {
		super(messageKey, cause);
	}
	
	public Throwable getCause() {
		return super.getCause();
	}
}

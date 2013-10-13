package kornell.gui.client.presentation.util;

public class ValidatorHelper {

	private final String EMAIL_PATTERN = "^[^0-9][A-z0-9_]+([.][A-z0-9_]+)*[@][A-z0-9_]+([.][A-z0-9_]+)*[.][A-z]{2,4}$";
	private final String USERNAME_PATTERN = "^[^0-9.][A-z0-9.]{2,}$";
	private final String PASSWORD_PATTERN = "^(?=.*[0-9]+.*)(?=.*[a-z]+.*)(?=.*[A-Z]+.*)[0-9a-zA-Z]{8,}$";
	
	public boolean emailValid(String field){
		return field == null ? false : field.trim().matches(EMAIL_PATTERN);
	}
	
	public boolean usernameValid(String field){
		return field == null ? false : field.trim().matches(USERNAME_PATTERN);
	}
	
	public boolean passwordValid(String field){
		return field == null ? false : field.trim().matches(PASSWORD_PATTERN);
	}
	
	public boolean lengthValid(String field, int minLength, int maxLength){
		return field == null ? false : field.trim().length() >= minLength && field.trim().length() <= maxLength;
	}
	
	public boolean lengthValid(String field, int minLength){
		return lengthValid(field, minLength, Integer.MAX_VALUE);
	}
}

package kornell.gui.client.session;

import kornell.api.client.Callback;
import kornell.core.entity.Institution;

import com.google.gwt.storage.client.Storage;

public class UserSession {
	private static final String SEPARATOR = ".";
	private static final String PREFIX = "Kornell.v1.UserSession";
	private static UserSession current = null;
	
	private String personUUID;
	private String institutionUUID;
	
	public static UserSession setCurrentPerson(String personUUID, String institutionUUID){
		current = new UserSession(personUUID,institutionUUID);
		return current;
	}
	 
	public static void current(Callback<UserSession> callback){
		if(current == null){
			throw new IllegalStateException("Unknown User");
		}
		callback.ok(current);
	}

	private UserSession(String personUUID, String institutionUUID) {
		this.personUUID = personUUID;
		this.institutionUUID=institutionUUID;
	}

	public String getItem(String key) {
		Storage localStorage = Storage.getLocalStorageIfSupported();
		if(localStorage != null)
			return localStorage.getItem(prefixed(key));
		return null;
	}

	public void setItem(String key, String value) {
		Storage localStorage = Storage.getLocalStorageIfSupported();
		if(localStorage != null)
			localStorage.setItem(prefixed(key), value);		
	}

	private String prefixed(String key) {
		return PREFIX + SEPARATOR + personUUID + SEPARATOR + key ;
	}

	public String getPersonUUID() {;
		return personUUID;
	}

	public String getInstitutionUUID() {
		return institutionUUID;
	}

}

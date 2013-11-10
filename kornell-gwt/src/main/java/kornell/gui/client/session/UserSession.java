package kornell.gui.client.session;

import kornell.api.client.Callback;

import com.google.gwt.storage.client.Storage;

public class UserSession {
	private static final String SEPARATOR = ".";
	private static final String PREFIX = "Kornell.v1.UserSession";
	private static String currentPersonUUID;
	private static UserSession current = null;
	
	private String personUUID;
	
	public static UserSession setCurrentPerson(String personUUID){
		UserSession.currentPersonUUID = personUUID;
		return current();
	}
	
	//TODO: Rethink user session (GUI, synchronization, remoting, etc)
	public static UserSession current(){		
		if(current == null){
			if(currentPersonUUID == null) throw new IllegalStateException("Unknown User");
			current = new UserSession(currentPersonUUID);
		}
		return current;
	}
	 
	public static void current(Callback<UserSession> callback){
		callback.ok(current);
	}

	public UserSession(String personUUID) {
		this.personUUID = personUUID;
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
}

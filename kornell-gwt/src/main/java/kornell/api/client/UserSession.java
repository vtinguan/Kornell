package kornell.api.client;

import java.util.List;

import kornell.core.entity.Registration;
import kornell.core.entity.Role;
import kornell.core.entity.RoleType;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;

public class UserSession extends KornellClient {
	private static final String SEPARATOR = ".";
	private static final String PREFIX = "Kornell.v1.UserSession";
	private static UserSession current;

	private String personUUID;
	private String institutionUUID;

	private UserInfoTO currentUser;

	@Deprecated
	public static UserSession setCurrentPerson(String personUUID,
			String institutionUUID) {
		current.personUUID = personUUID;
		current.institutionUUID = institutionUUID;
		return current;
	}


	public static void current(final Callback<UserSession> callback) {
		if(current == null){			
			current = UserSession.restore();
			GWT.log("Restoring User Session "+current.toString());
			current.getCurrentUser(new Callback<UserInfoTO>() {				
				@Override
				public void ok(UserInfoTO userInfo) {
					GWT.log("Welcome Back "+userInfo.getPerson().getFullName());
					current.setCurrentUser(userInfo);
					callback.ok(current);
				}		
				@Override
				public void unauthorized() {
					current.setCurrentUser(null);
					callback.ok(current);
				}
			});
		}else {
			GWT.log("Reusing Session from memory "+current.toString());
			callback.ok(current);
		}
	}

	private static UserSession restore() {
		GWT.log("\\0/ New User Session! \\0/");
		return new UserSession();
	}


	private UserSession(String personUUID, String institutionUUID) {
		this.personUUID = personUUID;
		this.institutionUUID = institutionUUID;
	}

	private UserSession() {
	}

	public String getItem(String key) {
		Storage localStorage = Storage.getLocalStorageIfSupported();
		if (localStorage != null)
			return localStorage.getItem(prefixed(key));
		return null;
	}

	public void setItem(String key, String value) {
		Storage localStorage = Storage.getLocalStorageIfSupported();
		if (localStorage != null)
			localStorage.setItem(prefixed(key), value);
	}

	private String prefixed(String key) {
		return PREFIX + SEPARATOR + currentUser.getPerson().getUUID() + SEPARATOR + key;
	}

	public String getPersonUUID() {
		return personUUID;
	}

	public String getInstitutionUUID() {
		return institutionUUID;
	}

	public boolean hasRole(RoleType type, String targetInstitutionUUID) {
		if (currentUser == null)
			return false;
		for (Role role : currentUser.getRoles()) {
			switch (role.getRoleType()) {
			case user: 
				if (RoleType.user.equals(type))
					return true;
				break;
			case dean:
				if (RoleType.dean.equals(type)
						&& role.getDeanRole().getInstitutionUUID()
								.equals(targetInstitutionUUID))
					return true;
				break;
			}
		}
		return false;
	}

	@Deprecated
	public void getCurrentUser(final Callback<UserInfoTO> cb) {
		Callback<UserInfoTO> wrapper = new Callback<UserInfoTO>() {
			@Override
			public void ok(UserInfoTO user) {
				GWT.log("Fetched user ["+user.getPerson().getUUID()+"]");
				current.setCurrentUser(user);
				cb.ok(user);
			};

			@Override
			protected void unauthorized() {
				cb.unauthorized();
			}

			@Override
			protected void forbidden() {
				cb.forbidden();
			}
		};
		if (currentUser == null)
			GET("/user").sendRequest(null, wrapper);
		else
			cb.ok(currentUser);
	}

	public void setCurrentUser(UserInfoTO user) {
		this.currentUser = user;
	};

	public void login(String username, String password, String confirmation,
			final Callback<UserInfoTO> callback) {
		final String auth = "Basic "
				+ ClientProperties.base64Encode(username + ":" + password);

		Callback<UserInfoTO> wrapper = new Callback<UserInfoTO>() {
			@Override
			public void ok(UserInfoTO user) {
				setCurrentUser(user);
				// TODO: https://github.com/Craftware/Kornell/issues/7
				ClientProperties.set("Authorization", auth);
				callback.ok(user);
			}

			@Override
			protected void unauthorized() {
				callback.unauthorized();
			}
		};
		confirmation = "".equals(confirmation) ? "NONE" : confirmation;
		GET("/user/login/" + confirmation).addHeader("Authorization", auth)
				.sendRequest(null, wrapper);

	}

	public boolean isAuthenticated() {		
		return currentUser != null;
	}

	public UserInfoTO getUserInfo() {
		return currentUser;
	}
	
	public boolean isDean() {
		return hasRole(RoleType.dean, institutionUUID);
	}
	
	public boolean isRegistered(){
		for (Registration registration : currentUser.getRegistrationsTO().getRegistrations()) {
			if(registration.getInstitutionUUID().equals(institutionUUID))
				return true;
		}
		return false;
	}

}

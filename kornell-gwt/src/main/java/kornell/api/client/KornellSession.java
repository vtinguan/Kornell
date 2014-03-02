package kornell.api.client;

import kornell.core.entity.Role;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.shared.GWT;

public class KornellSession extends KornellClient {
	private static final String SEPARATOR = ".";
	private static final String PREFIX = "Kornell.v1.UserSession";

	private UserInfoTO currentUser = null;

	public KornellSession() {
		GWT.log("Instantiated new session");
	}
	
	public void getCurrentUser(final Callback<UserInfoTO> callback) {
		if (currentUser != null) {
			callback.ok(currentUser);
		} else {
			Callback<UserInfoTO> wrapper = new Callback<UserInfoTO>() {
				@Override
				public void ok(UserInfoTO userInfo) {
					setCurrentUser(userInfo);
					callback.ok(userInfo);
				}

				@Override
				public void unauthorized() {
					setCurrentUser(null);
					callback.unauthorized();
				}
			};
			GET("/user").sendRequest(null, wrapper);
		}
	}

	public void setCurrentUser(UserInfoTO userInfo) {
		this.currentUser = userInfo;
	}

	public String getItem(String key) {
		return ClientProperties.get(prefixed(key));
	}

	public void setItem(String key, String value) {
		ClientProperties.set(prefixed(key), value);
	}

	private String prefixed(String key) {
		return PREFIX + SEPARATOR + currentUser.getPerson().getUUID()
				+ SEPARATOR + key;
	}

	public boolean isPlatformAdmin() {
		return hasRole(RoleType.platformAdmin, null, null);
	}

	public boolean isInstitutionAdmin(String institutionUUID) {
		return hasRole(RoleType.institutionAdmin, institutionUUID, null) || isPlatformAdmin();
	}

	public boolean isInstitutionAdmin() {
		return isInstitutionAdmin(Dean.getInstance().getInstitution().getUUID());
	}

	public boolean isCourseClassAdmin(String courseClassUUID) {
		return hasRole(RoleType.courseClassAdmin, null, courseClassUUID) || isInstitutionAdmin();
	}

	public boolean isCourseClassAdmin() {
		if(Dean.getInstance().getCourseClassTO() == null) return false;
		return isCourseClassAdmin(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID());
	}

	private boolean hasRole(RoleType type, String institutionUUID, String courseClassUUID) {
		if (currentUser == null)
			return false;
		for (Role role : currentUser.getRoles()) {
			if(RoleCategory.isValidRole(role, type, institutionUUID, courseClassUUID))
				return true;
		}
		return false;
	}

	public UserInfoTO getCurrentUser() {
		if (currentUser != null) {
			GWT.log("WARNING: Requested current user for unauthenticated session. Watch out for NPEs. Check before or use callback to be safer.");
		}
		return currentUser;
	}

	public boolean isAuthenticated() {
		return currentUser != null;
	}

	public void login(String username, String password, String confirmation,
			final Callback<UserInfoTO> callback) {
		final String auth = "Basic "
				+ ClientProperties.base64Encode(username + ":" + password);

		Callback<UserInfoTO> wrapper = new Callback<UserInfoTO>() {
			@Override
			public void ok(UserInfoTO user) {
				setCurrentUser(user);
				// TODO: https://github.com/Craftware/Kornell/issues/7
				ClientProperties.set("X-KNL-A", auth);
				callback.ok(user);
				//TODO: fire event
			}

			@Override
			protected void unauthorized() {
				callback.unauthorized();
			}
		};
		confirmation = "".equals(confirmation) ? "NONE" : confirmation;
		GET("/user/login/" + confirmation).addHeader("X-KNL-A", auth)
				.sendRequest(null, wrapper);

	}
	
	public void logout(){
		ClientProperties.remove(ClientProperties.X_KNL_A);
		setCurrentUser(null);
	}

	public boolean isAnonymous() {
		return ! isAuthenticated();
	}

}

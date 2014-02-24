package kornell.api.client;

import com.google.gwt.core.shared.GWT;

import kornell.core.entity.Role;
import kornell.core.entity.RoleType;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.util.ClientProperties;

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
		return hasRole(RoleType.platformAdmin);
	}

	public boolean isInstitutionAdmin() {
		return hasRole(RoleType.institutionAdmin) || isPlatformAdmin();
	}

	public boolean isCourseClassAdmin() {
		return hasRole(RoleType.courseClassAdmin) || isInstitutionAdmin();
	}

	public boolean hasRole(RoleType type) {
		if (currentUser == null)
			return false;
		for (Role role : currentUser.getRoles()) {
			switch (role.getRoleType()) {
			case user:
				if (RoleType.user.equals(type))
					return true;
				break;
			case courseClassAdmin:
				if (Dean.getInstance().getCourseClassTO() != null
						&& RoleType.courseClassAdmin.equals(type)
						&& role.getCourseClassAdminRole()
								.getCourseClassUUID()
								.equals(Dean.getInstance().getCourseClassTO()
										.getCourseClass().getUUID()))
					return true;
				break;
			case institutionAdmin:
				if (RoleType.institutionAdmin.equals(type)
						&& role.getInstitutionAdminRole()
								.getInstitutionUUID()
								.equals(Dean.getInstance().getInstitution()
										.getUUID()))
					return true;
				break;
			case platformAdmin:
				if (RoleType.platformAdmin.equals(type))
					return true;
				break;
			default:
				break;
			}
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

}

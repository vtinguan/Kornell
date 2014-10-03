package kornell.api.client;

import java.util.List;
import java.util.logging.Logger;

import kornell.core.entity.Role;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.to.UserInfoTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.event.shared.EventBus;


public class KornellSession extends KornellClient {
	Logger logger = Logger.getLogger(KornellSession.class.getName());

	private static final String PREFIX = ClientProperties.PREFIX + "UserSession";

	private UserInfoTO currentUser = null;

	private EventBus bus;

	public KornellSession(EventBus bus) {
		this.bus = bus;
		logger.info("Instantiated new Kornell Session");
	}
	
	public void getCurrentUser(final Callback<UserInfoTO> callback) {
		getCurrentUser(false, callback);
	}
	
	public void getCurrentUser(boolean skipCache, final Callback<UserInfoTO> callback) {
		if (currentUser != null && !skipCache) {
			callback.ok(currentUser);
		} else {
			Callback<UserInfoTO> wrapper = new Callback<UserInfoTO>() {
				@Override
				public void ok(UserInfoTO userInfo) {
					setCurrentUser(userInfo);
					callback.ok(userInfo);
				}

				@Override
				public void unauthorized(String errorMessage) {
					setCurrentUser(null);
					//callback.unauthorized(errorMessage);
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
		return PREFIX + ClientProperties.SEPARATOR + currentUser.getPerson().getUUID()
				+ ClientProperties.SEPARATOR + key;
	}

	public boolean isPlatformAdmin() {
		return isValidRole(RoleType.platformAdmin, null, null);
	}

	public boolean isInstitutionAdmin(String institutionUUID) {
		return isValidRole(RoleType.institutionAdmin, institutionUUID, null) || isPlatformAdmin();
	}

	public boolean isInstitutionAdmin() {
		return isInstitutionAdmin(Dean.getInstance().getInstitution().getUUID());
	}

	public boolean isCourseClassAdmin(String courseClassUUID) {
		return isValidRole(RoleType.courseClassAdmin, null, courseClassUUID) || isInstitutionAdmin();
	}

	public boolean isCourseClassAdmin() {
		if(Dean.getInstance().getCourseClassTO() == null) return false;
		return isCourseClassAdmin(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID());
	}

	private boolean isValidRole(RoleType type, String institutionUUID, String courseClassUUID) {
		if (currentUser == null)
			return false;
		return RoleCategory.isValidRole(currentUser.getRoles(), type, institutionUUID, courseClassUUID);
	}
	
	public UserInfoTO getCurrentUser() {
		if (currentUser == null) {
			GWT.log("WARNING: Requested current user for unauthenticated session. Watch out for NPEs. Check before or use callback to be safer.");
		}
		return currentUser;
	}

	public boolean isAuthenticated() {
		return currentUser != null;
	}

	public void login(String username, String password, final Callback<UserInfoTO> callback) {
		final String auth = ClientProperties.getAuthString(username, password);

		Callback<UserInfoTO> wrapper = new Callback<UserInfoTO>() {
			@Override
			public void ok(UserInfoTO user) {
				setCurrentUser(user);
				ClientProperties.set(ClientProperties.X_KNL_A, auth);
				bus.fireEvent(new LoginEvent(user));
				callback.ok(user);
			}

			@Override
			protected void unauthorized(String errorMessage) {
				setCurrentUser(null);
				callback.unauthorized(errorMessage);
			}
		};
		GET("/user").addHeader(ClientProperties.X_KNL_A, auth).sendRequest(null, wrapper);
	}
	
	public void logout(){
		ClientProperties.remove(ClientProperties.X_KNL_A);
		setCurrentUser(null);
	}

	public boolean isAnonymous() {
		return ! isAuthenticated();
	}

	public boolean hasSignedTerms() {
		return StringUtils.isSome(Dean.getInstance().getInstitution().getTerms()) &&
				currentUser != null &&
				currentUser.getPerson().getTermsAcceptedOn() != null;
	}

}

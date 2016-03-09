package kornell.api.client;

import java.util.List;
import java.util.logging.Logger;

import kornell.core.entity.CourseClass;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.error.KornellErrorTO;
import kornell.core.to.CourseClassTO;
import kornell.core.to.RoleTO;
import kornell.core.to.TokenTO;
import kornell.core.to.UserInfoTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.util.ClientProperties;

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
				public void unauthorized(KornellErrorTO kornellErrorTO) {
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

	public boolean isPlatformAdmin(String institutionUUID) {
		return isValidRole(RoleType.platformAdmin, institutionUUID, null);
	}

	public boolean isPlatformAdmin() {
		return isValidRole(RoleType.platformAdmin, Dean.getInstance().getInstitution().getUUID(), null);
	}

	public boolean isInstitutionAdmin(String institutionUUID) {
		return isValidRole(RoleType.institutionAdmin, institutionUUID, null) || isPlatformAdmin(institutionUUID);
	}

	public boolean isInstitutionAdmin() {
		return isInstitutionAdmin(Dean.getInstance().getInstitution().getUUID());
	}
	
	public boolean hasCourseClassRole(String courseClassUUID) {
		return isCourseClassAdmin(courseClassUUID) || isCourseClassObserver(courseClassUUID) || isCourseClassTutor(courseClassUUID);
	}
	
	public boolean hasCourseClassRole() {
		return isCourseClassAdmin() || isCourseClassObserver() || isCourseClassTutor();
	}

	public boolean isCourseClassAdmin(String courseClassUUID) {
		return isValidRole(RoleType.courseClassAdmin, null, courseClassUUID) || isInstitutionAdmin();
	}

	public boolean isCourseClassAdmin() {
		Dean dean = Dean.getInstance();
		if(dean == null) return false;
		CourseClassTO courseClassTO = dean.getCourseClassTO();
		if(courseClassTO == null) return false;
		CourseClass courseClass = courseClassTO.getCourseClass();
		if(courseClass == null) return false;
		String courseClassUUID = courseClass.getUUID();
		return isCourseClassAdmin(courseClassUUID);
	}

	public boolean isCourseClassObserver(String courseClassUUID) {
		return isValidRole(RoleType.observer, null, courseClassUUID) || isInstitutionAdmin();
	}

	public boolean isCourseClassObserver() {
		Dean dean = Dean.getInstance();
		if(dean == null) return false;
		CourseClassTO courseClassTO = dean.getCourseClassTO();
		if(courseClassTO == null) return false;
		CourseClass courseClass = courseClassTO.getCourseClass();
		if(courseClass == null) return false;
		String courseClassUUID = courseClass.getUUID();
		return isCourseClassObserver(courseClassUUID);
	}

	public boolean isCourseClassTutor(String courseClassUUID) {
		return isValidRole(RoleType.tutor, null, courseClassUUID) || isInstitutionAdmin();
	}

	public boolean isCourseClassTutor() {
		Dean dean = Dean.getInstance();
		if(dean == null) return false;
		CourseClassTO courseClassTO = dean.getCourseClassTO();
		if(courseClassTO == null) return false;
		CourseClass courseClass = courseClassTO.getCourseClass();
		if(courseClass == null) return false;
		String courseClassUUID = courseClass.getUUID();
		return isCourseClassTutor(courseClassUUID);
	}

	private boolean isValidRole(RoleType type, String institutionUUID, String courseClassUUID) {
		if (currentUser == null)
			return false;
		return RoleCategory.isValidRole(currentUser.getRoles(), type, institutionUUID, courseClassUUID);
	}
	
	public UserInfoTO getCurrentUser() {
		if (currentUser == null) {
			logger.warning("WARNING: Requested current user for unauthenticated session. Watch out for NPEs. Check before or use callback to be safer.");
		}
		return currentUser;
	}

	public boolean isAuthenticated() {
		return currentUser != null;
	}

	public void login(String username, String password, final Callback<UserInfoTO> callback) {
		final Callback<UserInfoTO> wrapper = new Callback<UserInfoTO>() {
			@Override
			public void ok(UserInfoTO user) {
				setCurrentUser(user);
				callback.ok(user);
			}

			@Override
			protected void unauthorized(KornellErrorTO kornellErrorTO) {
				setCurrentUser(null);
				callback.unauthorized(kornellErrorTO);
			}
		};
		
		Callback<TokenTO> loginWrapper = new Callback<TokenTO>() {

			@Override
			public void ok(TokenTO to) {
				ClientProperties.set(ClientProperties.X_KNL_TOKEN, to.getToken());
				GET("/user").sendRequest(null, wrapper);
			}
			
			@Override
			protected void unauthorized(KornellErrorTO kornellErrorTO) {
				setCurrentUser(null);
				callback.unauthorized(kornellErrorTO);
			}
			
			//user must change his password
			@Override
			protected void forbidden(KornellErrorTO kornellErrorTO) {
				callback.forbidden(kornellErrorTO);
			}
			
		};
		POST_LOGIN(username, password, "/auth/token").sendRequest(null, loginWrapper);
	}
	
	public void logout(){
		POST("/auth/logout").sendRequest(null, new Callback<String>() {
			@Override
			public void ok(String to) {
				//Nothing to do
			}
			
			@Override
			protected void unauthorized(KornellErrorTO kornellErrorTO) {
				//nothing to do here too, if for some reason the token is not there when the user
				//tries to logout, let's just ignore.
			}
		});
		
		ClientProperties.remove(ClientProperties.X_KNL_TOKEN);
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

	public boolean hasAnyAdminRole(List<RoleTO> roleTOs) {
		return (RoleCategory.hasRole(roleTOs, RoleType.courseClassAdmin) || 
				RoleCategory.hasRole(roleTOs, RoleType.observer) || 
				RoleCategory.hasRole(roleTOs, RoleType.tutor) || 
				isInstitutionAdmin());
	}

}

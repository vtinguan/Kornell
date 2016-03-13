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
import kornell.core.to.UserHelloTO;
import kornell.core.to.UserInfoTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.GenericClientFactoryImpl;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.util.ClientProperties;


public class KornellSession extends KornellClient {
	Logger logger = Logger.getLogger(KornellSession.class.getName());

	private static final String PREFIX = ClientProperties.PREFIX + "UserSession";

	private UserInfoTO currentUser = null;
	private Dean dean = null;

	public KornellSession() {
		logger.info("Instantiated new Kornell Session");
		this.dean = GenericClientFactoryImpl.DEAN;
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
		this.dean = GenericClientFactoryImpl.DEAN;
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
		return isValidRole(RoleType.platformAdmin, dean.getInstitution().getUUID(), null);
	}

	public boolean isInstitutionAdmin(String institutionUUID) {
		return isValidRole(RoleType.institutionAdmin, institutionUUID, null) || isPlatformAdmin(institutionUUID);
	}

	public boolean isInstitutionAdmin() {
		return isInstitutionAdmin(dean.getInstitution().getUUID());
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

	public void login(String username, String password, final Callback<UserHelloTO> callback) {
		final Callback<UserHelloTO> wrapper = new Callback<UserHelloTO>() {
			@Override
			public void ok(UserHelloTO userHello) {
				setCurrentUser(userHello.getUserInfoTO());
				callback.ok(userHello);
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
				GET("/user/login").sendRequest(null, wrapper);
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
		return StringUtils.isSome(dean.getInstitution().getTerms()) &&
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

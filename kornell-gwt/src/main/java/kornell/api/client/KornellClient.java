package kornell.api.client;

import kornell.core.entity.Institution;
import kornell.core.to.CourseTO;
import kornell.core.to.CoursesTO;
import kornell.core.to.RegistrationsTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.session.UserSession;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.client.GWT;

public class KornellClient extends RESTClient implements LogoutEventHandler {
	private UserInfoTO currentUser;	

	private KornellClient() {}
	
	public void login(
			String username,
			String password,
			String confirmation,
			final Callback<UserInfoTO> callback) {
		final String auth = "Basic "+ ClientProperties.base64Encode(username+":"+password);				
		
		Callback<UserInfoTO> wrapper = new Callback<UserInfoTO>(){
			protected void ok(UserInfoTO user) {
				setCurrentUser(user);
				//TODO: https://github.com/Craftware/Kornell/issues/7
				ClientProperties.set("Authorization", auth);
				
				callback.ok(user);
			}

			@Override
			protected void unauthorized() {
				callback.unauthorized();
			}
		};
		//TODO: What is this?
		confirmation = "".equals(confirmation)?"NONE":confirmation;
		GET("/user/login/"+confirmation)
			.addHeader("Authorization", auth)
			.sendRequest(null, wrapper);

	}

	public void getCourses(Callback<CoursesTO> callback){
		GET("/courses").sendRequest(null, callback);	
	}

	private void setCurrentUser(UserInfoTO user) {
		this.currentUser = user;	
	};
	

	
	//TODO: Is this safe?
	public void getUser(String username, Callback<UserInfoTO> cb){
		GET("/user/"+username).sendRequest(null, cb);	
	}
	
	public void checkUser(String username, String email, Callback<UserInfoTO> cb){
		GET("/user/check/"+username+"/"+email).sendRequest(null, cb);
	}

	public void createUser(String data, Callback<UserInfoTO> cb){
		PUT("/user/create/").sendRequest(data, cb);
	}

	public void sendWelcomeEmail(String userUUID, Callback<Void> cb){
		GET("/email/welcome/"+userUUID).sendRequest(null, cb);
	}
	
	public void getCourseTO(String uuid, Callback<CourseTO> cb) {
		GET("/courses/"+uuid).sendRequest(null, cb);
	}
	
	public static KornellClient getInstance() {		
		return new KornellClient();
	}
	
	public class RegistrationsClient {
		public void getUnsigned(Callback<RegistrationsTO> callback) {
			GET("/registrations").sendRequest("", callback);		
		}
	}

	//TODO: extract those inner classes
	public class InstitutionClient{
		private String institutionUUID;

		public InstitutionClient(String uuid){
			this.institutionUUID = uuid;
		}
		
		public void acceptTerms(Callback<Void> cb){
			PUT("/institutions/"+institutionUUID).go(cb);
		}
		
		//TODO: remove this
		public void getInstitution(Callback<Institution> cb){
			GET("/institutions/"+institutionUUID).sendRequest(null, cb);
		}
	}
	
	public RegistrationsClient registrations(){
		//TODO: Consider lifecycle
		return new RegistrationsClient();
	}

	public InstitutionClient institution(String uuid) {
		return new InstitutionClient(uuid);
	}

	public void placeChanged(final String token) {
		PUT("/user/placeChange").sendRequest(token,new Callback(){
			@Override
			protected void ok() {
				GWT.log("Place changed to ["+token+"]");
			}
		});
	}

	public void notesUpdated(String courseUUID, String notes) {
		PUT("/enrollment/"+courseUUID+"/notesUpdated").sendRequest(notes,new Callback<Void>(){
			@Override
			protected void ok() {
				GWT.log("notes updated");
			}
		});
	}

	@Override
	public void onLogout() {
		forgetCredentials();
	}

	public CourseClient course(String courseUUID) {
		return new CourseClient(courseUUID);
	}
	
	public EventsClient events(){
		return new EventsClient();
	}

	@SuppressWarnings("rawtypes")
	//TODO: Remove raw type
	public void check(String src, Callback callback) {
		HEAD(src).go(callback);
	}

}	
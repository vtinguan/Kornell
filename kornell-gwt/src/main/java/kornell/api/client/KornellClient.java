package kornell.api.client;

import kornell.core.shared.to.CourseTO;
import kornell.core.shared.to.CoursesTO;
import kornell.core.shared.to.RegistrationsTO;
import kornell.core.shared.to.UserInfoTO;
import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.RequestBuilder;

public class KornellClient implements LogoutEventHandler {

	private String apiURL = null;
	private UserInfoTO currentUser;	

	private KornellClient() {
		discoverApiUrl();
	}

	private void discoverApiUrl() {
		apiURL = KornellClient.getFromEnvironment();
		if(apiURL == null || apiURL.length() == 0){ 
			useDefaultUrl();
		}else{
			GWT.log("API url already discovered");
		}
		GWT.log("Using API Endpoint: "+apiURL);
	}

	

	private static native String getFromEnvironment() /*-{
	  //console.debug("Using API Endpoint: "+$wnd.KornellConfig.apiEndpoint);
	  //console.debug($wnd.KornellConfig.apiEndpoint);
	  return $wnd.KornellConfig.apiEndpoint; 
	}-*/;

	private void useDefaultUrl() {
		apiURL = "http://localhost:8080";
	}

	public void login(
			String username,
			String password,
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
				
		createGET("/user")
			.addHeader("Authorization", auth)
			.sendRequest(null, wrapper);

	}

	public void getCourses(Callback<CoursesTO> callback){
		createGET("/courses").sendRequest(null, callback);	
	}

	private void setCurrentUser(UserInfoTO user) {
		this.currentUser = user;	
	};
	
	public void getCurrentUser(Callback<UserInfoTO> cb){
		//TODO: Consider client side caching
		createGET("/user").sendRequest(null, cb);	
	}
	
	public void getCourseTO(String uuid, Callback<CourseTO> cb) {
		createGET("/courses/"+uuid).sendRequest(null, cb);
	}

	private ExceptionalRequestBuilder createGET(String path) {		
		ExceptionalRequestBuilder reqBuilder = new ExceptionalRequestBuilder(RequestBuilder.GET, getApiUrl()+path);
		setAuthenticationHeaders(reqBuilder);
		return  reqBuilder;
	}
	
	private ExceptionalRequestBuilder createPUT(String path) {		
		ExceptionalRequestBuilder reqBuilder =
				new ExceptionalRequestBuilder(RequestBuilder.PUT, getApiUrl()+path);
		setAuthenticationHeaders(reqBuilder);
		return  reqBuilder;
	}

	private void setAuthenticationHeaders(ExceptionalRequestBuilder reqBuilder) {
		String auth = ClientProperties.get("Authorization");
		if(auth != null && auth.length()>0)
			reqBuilder.setHeader("Authorization", auth);
	}
	
	public String getApiUrl() {
		while(apiURL == null){
			GWT.log("Could not find API URL. Looking up again");
			discoverApiUrl();
		}
		return apiURL;
	}

	public static KornellClient getInstance() {		
		return new KornellClient();
	}
	
	public class RegistrationsClient {
		public void getUnsigned(Callback<RegistrationsTO> callback) {
			createGET("/registrations").sendRequest("", callback);		
		}
	}

	//TODO: extract those inner classes
	public class InstitutionClient{
		private String institutionUUID;

		public InstitutionClient(String uuid){
			this.institutionUUID = uuid;
		}
		
		public void acceptTerms(Callback<Void> cb){
			createPUT("/institutions/"+institutionUUID)
			.sendRequest("",cb);
		}
	}
	
	public RegistrationsClient registrations(){
		//TODO: Consider lifecycle
		return new RegistrationsClient();
	}

	public InstitutionClient institution(String uuid) {
		return new InstitutionClient(uuid);
	}

	public void placeChanged(String token) {
		createPUT("/user/placeChange").sendRequest(token,new Callback(){
			@Override
			protected void ok() {
				GWT.log("place changed");
			}
		});
	}

	public void notesUpdated(String courseUUID, String notes) {
		createPUT("/enrollment/"+courseUUID+"/notesUpdated").sendRequest(notes,new Callback(){
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

	private void forgetCredentials() {
		ClientProperties.remove("Authentication");
	}

}	
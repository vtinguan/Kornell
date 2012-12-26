package kornell.api.client;

import kornell.api.client.data.Person;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.storage.client.Storage;

public class KornellClient {
	Storage store = Storage.getLocalStorageIfSupported();

	private String apiURL;
	private Person currentUser;	

	public KornellClient(String apiURL) {
		this.apiURL = apiURL;
	}

	public void login(
			String username,
			String password,
			final Callback callback) {
		RequestBuilder builder = createGET("/user");
		final String auth = "Basic "+KornellClient.encode(username,password);		
		builder.setHeader("Authorization", auth);
		
		Callback wrapper = new Callback(){
			protected void ok(Person person) {
				setCurrentUser(person);
				//TODO: https://github.com/Craftware/Kornell/issues/7
				storeAuth(auth);
				callback.ok(person);
			}
			
			private void storeAuth(String auth) {				  				  
				  if(store != null){
					  store.setItem("Authorization", auth);
				  }else GWT.log("Ooops, your browser does not support local storage");
			}

			@Override
			protected void unauthorized() {
				callback.unauthorized();
			}
		};
				
		try {	
			builder.sendRequest(null, wrapper);
		} catch (RequestException ex) {
			GWT.log("Exssssxxxceptuimmmm",ex);
		}

	}

	private void setCurrentUser(Person person) {
		this.currentUser = person;
		
	};
	
	public void getCurrentUser(Callback cb){
		try{
			RequestBuilder builder = createGET("/user");
			builder.sendRequest(null, cb);	
		}catch(RequestException e){
			GWT.log("OOPS...",e);
		}
	}

	private RequestBuilder createGET(String path) {
		RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.GET, apiURL+path);
		if(store != null){
			String auth = store.getItem("Authorization");
			if(auth != null && auth.length()>0)
				reqBuilder.setHeader("Authorization", auth);
		}
		return  reqBuilder;
	}
	
	private static String encode(String username, String password) {
		return KornellClient.base64Encode(username+":"+password);
	}

	private static native String base64Encode(String plain) /*-{
	  return window.btoa(plain);
	}-*/;
}	
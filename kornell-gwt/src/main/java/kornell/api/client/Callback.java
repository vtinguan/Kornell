package kornell.api.client;

import static com.google.gwt.http.client.Response.SC_FORBIDDEN;
import static com.google.gwt.http.client.Response.SC_OK;
import static com.google.gwt.http.client.Response.SC_UNAUTHORIZED;
import kornell.api.client.data.Person;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class Callback implements RequestCallback{

	@Override
	public void onResponseReceived(Request request, Response response) {
		if (! isTrusted(response))
			throw new RuntimeException("Won't touch untrusted response");		
		int statusCode = response.getStatusCode();		
		switch (statusCode) {
			case SC_OK: ok(response); break;
			case SC_FORBIDDEN: forbidden(); break;
			case SC_UNAUTHORIZED: unauthorized(); break;
			case 0: cancelled(); break;
		default:
			GWT.log("Got a response, but don't know what to do about it");
			break;
		}
	}

	protected void ok(Response response) {
		dispatchByMimeType(response);		
	}

	private void dispatchByMimeType(Response response) {
		String contentType = response.getHeader("Content-Type");
		String responseText = response.getText();
		
		if(contentType.contains("json")){
			responseText = '('+responseText+')';		
			if ("application/json".equals(contentType))
				ok(Callback.parseJson(responseText));
			else if (Person.MIME_TYPE.equals(contentType))
				ok(Person.parseJson(responseText));
		}
		else ok(); 
	}

	protected void ok(Person person) {}

	protected void ok(JSONValue json){}

	private static JSONValue parseJson(String jsonStr){
		return JSONParser.parseStrict(jsonStr);
	}

	protected boolean isTrusted(Response response) {
		return true;
	}

	protected void cancelled() {
		GWT.log("Your request was cancelled, probably for the same origin policy, check your cross origin resourse sharing configuration.");
	}
	
	protected void unauthorized() {}
	protected void forbidden() {}
	protected void ok() {}

	@Override
	public void onError(Request request, Throwable exception) {
		error(request,exception);
		
	}

	private void error(Request request, Throwable exception) {
		GWT.log("Error!",exception);
	}

}

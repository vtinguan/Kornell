package kornell.api.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import static com.google.gwt.http.client.Response.*;

public class Callback implements RequestCallback{

	@Override
	public void onResponseReceived(Request request, Response response) {
		int statusCode = response.getStatusCode();
		String statusText = response.getStatusText();
		switch (statusCode) {
			case SC_OK: ok(); break;
			case SC_FORBIDDEN: forbidden(); break;
			case SC_UNAUTHORIZED: unauthorized(); break;
			case 0: cancelled(); break;
		default:
			GWT.log("Got a response, but don't know what to do about it");
			break;
		}
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

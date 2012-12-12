package kornell.api.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

public class Callback implements RequestCallback{

	@Override
	public void onResponseReceived(Request request, Response response) {
		int statusCode = response.getStatusCode();
		switch (statusCode) {
			case 200: ok(); break;
			case 403: forbidden(); break;
		default:
			GWT.log("Got a response, but don't know what to do about it");
			break;
		}
	}

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

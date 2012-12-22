package kornell.api.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;

public class KornellClient {

	private String apiURL;

	public KornellClient(String apiURL) {
		this.apiURL = apiURL;
	}

	public void checkPassword(
			String username,
			String password,
			RequestCallback callback) {
		StringBuffer postData = new StringBuffer();
		

		RequestBuilder builder =
				new RequestBuilder(RequestBuilder.GET, apiURL+"/auth");
		String authorization = "Basic "+KornellClient.encode(username,password);
		builder.setHeader("Authorization", authorization);
		try {
			builder.sendRequest(postData.toString(), callback);
		} catch (RequestException ex) {
			GWT.log("Exssssxxxceptuimmmm",ex);
		}

	}

	private static String encode(String username, String password) {
		return KornellClient.base64Encode(username+":"+password);
	}

	private static native String base64Encode(String plain) /*-{
	  return window.btoa(plain);
	}-*/;

}

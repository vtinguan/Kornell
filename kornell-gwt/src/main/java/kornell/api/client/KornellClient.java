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
		postData.append(URL.encode("username")).append("=")
				.append(URL.encode(username));
		postData.append("&");
		postData.append(URL.encode("password")).append("=")
				.append(URL.encode(password));

		RequestBuilder builder =
				new RequestBuilder(RequestBuilder.POST, apiURL+"/auth/checkPassword");
		builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
		try {
			builder.sendRequest(postData.toString(), callback);
		} catch (RequestException ex) {
			GWT.log("Exssssxxxceptuimmmm",ex);
		}

	}

}

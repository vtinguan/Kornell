package kornell.api.client;

import kornell.core.util.StringUtils;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;

public class RESTClient {
	private static String apiURL = null;

	public RESTClient() {
		if (RESTClient.apiURL == null){
			RESTClient.discoverApiUrl();
		}
	}

	private static void discoverApiUrl() {
		apiURL = RESTClient.getFromEnvironment();
		if (apiURL == null || apiURL.length() == 0) {
			useDefaultUrl();
		} else {
			GWT.log("API url already discovered");
		}
		GWT.log("Using API Endpoint: " + apiURL);
	}

	private static native String getFromEnvironment() /*-{
		//console.debug("Using API Endpoint: "+$wnd.KornellConfig.apiEndpoint);
		//console.debug($wnd.KornellConfig.apiEndpoint);
		return $wnd.KornellConfig.apiEndpoint;
	}-*/;

	private static void useDefaultUrl() {
		apiURL = "http://localhost:8080";
	}

	public String getApiUrl() {
		while (apiURL == null) {
			GWT.log("Could not find API URL. Looking up again");
			discoverApiUrl();
		}
		return apiURL;
	}

	protected ExceptionalRequestBuilder GET(String... path) {
		String url = StringUtils.composeURL(getApiUrl(), path);
		ExceptionalRequestBuilder reqBuilder = new ExceptionalRequestBuilder(
				RequestBuilder.GET, url);
		setAuthenticationHeaders(reqBuilder);
		return reqBuilder;
	}

	protected ExceptionalRequestBuilder HEAD(String... path) {
		String url = StringUtils.composeURL(getApiUrl(), path);
		ExceptionalRequestBuilder reqBuilder = new ExceptionalRequestBuilder(
				RequestBuilder.HEAD, url);
		setAuthenticationHeaders(reqBuilder);
		return reqBuilder;
	}

	protected ExceptionalRequestBuilder PUT(String... path) {
		String url = StringUtils.composeURL(getApiUrl(), path);
		ExceptionalRequestBuilder reqBuilder = new ExceptionalRequestBuilder(
				RequestBuilder.PUT, url);
		setAuthenticationHeaders(reqBuilder);
		return reqBuilder;
	}

	protected void setAuthenticationHeaders(ExceptionalRequestBuilder reqBuilder) {
		String auth = ClientProperties.get("Authorization");
		if (auth != null && auth.length() > 0)
			reqBuilder.setHeader("Authorization", auth);
	}
}
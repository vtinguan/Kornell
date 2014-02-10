package kornell.api.client;

import kornell.core.util.StringUtils;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;

public class RESTClient {
	public String getApiUrl() {		
		return "/api";
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
		if (auth != null && auth.length() > 0) {
			reqBuilder.setHeader("Authorization", auth);
			reqBuilder.setHeader("X-KNL-A", auth);
		}
	}
}
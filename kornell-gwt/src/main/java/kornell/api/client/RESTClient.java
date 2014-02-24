package kornell.api.client;

import static kornell.core.util.StringUtils.composeURL;
import static kornell.core.util.StringUtils.isSome;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.http.client.RequestBuilder;

public class RESTClient {
	public String getApiUrl() {		
		return "/api";
	}

	protected ExceptionalRequestBuilder GET(String... path) {
		String url = composeURL(getApiUrl(), path);
		ExceptionalRequestBuilder reqBuilder = new ExceptionalRequestBuilder(
				RequestBuilder.GET, url);
		setAuthenticationHeaders(reqBuilder);
		return reqBuilder;
	}

	protected ExceptionalRequestBuilder HEAD(String... path) {
		String url = composeURL(getApiUrl(), path);
		ExceptionalRequestBuilder reqBuilder = new ExceptionalRequestBuilder(
				RequestBuilder.HEAD, url);
		setAuthenticationHeaders(reqBuilder);
		return reqBuilder;
	}

	protected ExceptionalRequestBuilder PUT(String... path) {
		String url = composeURL(getApiUrl(), path);
		ExceptionalRequestBuilder reqBuilder = new ExceptionalRequestBuilder(
				RequestBuilder.PUT, url);
		setAuthenticationHeaders(reqBuilder);
		return reqBuilder;
	}

	protected void setAuthenticationHeaders(ExceptionalRequestBuilder reqBuilder) {
		String auth = ClientProperties.get("X-KNL-A");
		if (isSome(auth)) {
			reqBuilder.setHeader("X-KNL-A", auth);
		}
	}
}
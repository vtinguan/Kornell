package kornell.api.client;

import static kornell.core.util.StringUtils.composeURL;
import static kornell.core.util.StringUtils.isSome;

import java.util.logging.Logger;

import kornell.core.entity.AuthClientType;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.http.client.RequestBuilder;

public class RESTClient {
	
	Logger logger = Logger.getLogger(RESTClient.class.getName());
	
	private String apiURL = "/api";
	
	public String getApiUrl() {		
		return apiURL;
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

	protected ExceptionalRequestBuilder POST(String... path) {
		String url = composeURL(getApiUrl(), path);
		ExceptionalRequestBuilder reqBuilder = new ExceptionalRequestBuilder(
				RequestBuilder.POST, url);
		setAuthenticationHeaders(reqBuilder);
		return reqBuilder;
	}
	
	protected ExceptionalRequestBuilder POST_LOGIN(String username, String password, String... path) {
		String url = composeURL(getApiUrl(), path);
		ExceptionalRequestBuilder reqBuilder = new ExceptionalRequestBuilder(
				RequestBuilder.POST, url);
		reqBuilder.setHeader("Content-Type","application/x-www-form-urlencoded");
		
		StringBuilder sb = new StringBuilder();
		sb.append("userkey=" + username + "&");
		sb.append("password=" + password + "&");
		sb.append("institutionUUID=" + Dean.getInstance().getInstitution().getUUID() + "&");
		sb.append("clientType=" + AuthClientType.web.toString());
		
		reqBuilder.setRequestData(sb.toString());
		return reqBuilder;
	}

	protected ExceptionalRequestBuilder DELETE(String... path) {
		String url = composeURL(getApiUrl(), path);
		ExceptionalRequestBuilder reqBuilder = new ExceptionalRequestBuilder(
				RequestBuilder.DELETE, url);
		setAuthenticationHeaders(reqBuilder);
		return reqBuilder;
	}

	protected void setAuthenticationHeaders(ExceptionalRequestBuilder reqBuilder) {
		String auth = ClientProperties.get(ClientProperties.X_KNL_TOKEN);
		if (isSome(auth)) {
			reqBuilder.setHeader(ClientProperties.X_KNL_TOKEN, auth);
		}
	}
}
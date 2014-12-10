package kornell.api.client;

import static kornell.core.util.StringUtils.composeURL;
import static kornell.core.util.StringUtils.isSome;

import java.util.logging.Logger;

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

	protected ExceptionalRequestBuilder DELETE(String... path) {
		String url = composeURL(getApiUrl(), path);
		ExceptionalRequestBuilder reqBuilder = new ExceptionalRequestBuilder(
				RequestBuilder.DELETE, url);
		setAuthenticationHeaders(reqBuilder);
		return reqBuilder;
	}

	protected void setAuthenticationHeaders(ExceptionalRequestBuilder reqBuilder) {
		String auth = ClientProperties.get(ClientProperties.X_KNL_A);
		if (isSome(auth)) {
			try {
				String decoded = ClientProperties.base64Decode(auth.replaceAll("\\u00a0"," ").split(" ")[1]);
	      String[] parts = decoded.split(":");
	      if(parts.length == 2 || parts[2].equals("null")){
	      	decoded = parts[0] + ":" + parts[1] + ":" + (Dean.getInstance() != null && Dean.getInstance().getInstitution() != null ? Dean.getInstance().getInstitution().getUUID() : "null");
	      	auth = "Basic " + ClientProperties.base64Encode(decoded);
	      	ClientProperties.set(ClientProperties.X_KNL_A, auth);
	      }
      } catch (Exception e) {
	      // TODO: handle exception
      }
			reqBuilder.setHeader(ClientProperties.X_KNL_A, auth);
		}
	}
}
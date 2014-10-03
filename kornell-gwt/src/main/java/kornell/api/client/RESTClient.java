package kornell.api.client;

import static kornell.core.util.StringUtils.composeURL;
import static kornell.core.util.StringUtils.isSome;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.http.client.RequestBuilder;

public class RESTClient {
	
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
				String prefix = "Basic ";
				int index = auth.indexOf(prefix);
				if(index >= 0){
					String decoded = ClientProperties.base64Decode(auth.split(" ")[1]);
		      String[] parts = decoded.split(":");
		      if(parts.length < 3 && Dean.getInstance().getInstitution() != null){
		      	decoded += ":" + Dean.getInstance().getInstitution().getUUID();
		      	auth = prefix + ClientProperties.base64Encode(decoded);
		      	ClientProperties.set(ClientProperties.X_KNL_A, auth);
		      }	      
				}
      } catch (Exception e) {
	      // TODO: handle exception
      }
			reqBuilder.setHeader(ClientProperties.X_KNL_A, auth);
		}
	}
}
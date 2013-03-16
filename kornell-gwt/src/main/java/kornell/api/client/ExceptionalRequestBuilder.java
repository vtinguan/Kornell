package kornell.api.client;

import javax.management.RuntimeErrorException;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;

public class ExceptionalRequestBuilder extends RequestBuilder {

	public ExceptionalRequestBuilder(Method httpMethod, String url) {
		super(httpMethod, url);
	}

	@Override
	public Request sendRequest(String requestData, RequestCallback callback) {
		try {
			return super.sendRequest(requestData, callback);
		} catch (RequestException e) {
			return handle(e);
		}
	}

	private Request handle(RequestException e) {
		GWT.log(e.getMessage(), e);
		//TODO: what Request should be returned...
		return null;
	}
	
	public ExceptionalRequestBuilder addHeader(String header, String value){
		setHeader(header, value);
		return this;
	}

}

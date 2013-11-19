package kornell.api.client;

import kornell.core.event.ActomEntered;
import kornell.core.event.EventFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

public class ExceptionalRequestBuilder extends RequestBuilder {

	protected static final RequestCallback NOOP = new RequestCallback() {

		@Override
		public void onResponseReceived(Request request, Response response) {
			GWT.log("NOOP ResponseReceived");
		}

		@Override
		public void onError(Request request, Throwable exception) {
			GWT.log("NOOP onError");
		}
	};

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
		// TODO: what Request should be returned?
		return null;
	}

	public ExceptionalRequestBuilder addHeader(String header, String value) {
		setHeader(header, value);
		return this;
	}

	public void go(Callback callback) {
		setCallback(callback);
		go();
	}

	public void go() {
		try {
			if (getCallback() == null) {
				setCallback(NOOP);
			}
			send();
		} catch (RequestException e) {
			handle(e);
		}
	}

	// TODO: Move All creation to clientfactory
	EventFactory eventFactory = GWT.create(EventFactory.class);

	private <T> ExceptionalRequestBuilder withBody(T object) {
		AutoBean<T> autoBean = AutoBeanUtils.getAutoBean(object);
		String reqData = AutoBeanCodex.encode(autoBean).getPayload();
		setRequestData(reqData);
		return this;
	}

	public <T> ExceptionalRequestBuilder withEntityBody(T object) {	
		return withBody(object);
	}
	
	public String contentTypeOf(Object o){
		return MediaTypes.get().typeOf(o.getClass());
	}

	public ExceptionalRequestBuilder withContentType(String contentType) {
		setHeader("Content-Type", contentType);
		return this;
	}

}

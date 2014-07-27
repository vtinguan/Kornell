package kornell.api.client;

import kornell.core.to.RegistrationsTO;

public class RegistrationsClient extends RESTClient {

	public void getUnsigned(Callback<RegistrationsTO> callback) {
		GET("/registrations").sendRequest("", callback);
	}
	
}

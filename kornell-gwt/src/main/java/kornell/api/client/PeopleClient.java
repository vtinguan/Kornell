package kornell.api.client;

import kornell.core.to.PeopleTO;

import com.google.gwt.http.client.URL;

public class PeopleClient extends KornellClient {

	public void findBySearchTerm(String search, String institutionUUID,
			Callback<PeopleTO> callback) {
		GET("/people/?search="
				+ URL.encodePathSegment(search)
				+ "&institutionUUID="
				+ institutionUUID)
				.sendRequest("", callback);
	}
	
}

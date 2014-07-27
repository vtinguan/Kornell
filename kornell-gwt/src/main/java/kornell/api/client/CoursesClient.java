package kornell.api.client;

import kornell.core.to.CoursesTO;

public class CoursesClient extends RESTClient {
	
	public void findByInstitution(String institutionUUID, Callback<CoursesTO> callback) {
		GET("/courses?institutionUUID=" + institutionUUID).go(callback);
	}
	
}

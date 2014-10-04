package kornell.api.client;

import kornell.core.to.InfosTO;

public class InfosClient extends RESTClient {

	private String enrollmentUUID;

	public InfosClient(EnrollmentClient enrollmentClient) {
		this.enrollmentUUID=enrollmentClient.getEnrollmentUUID();
	}
	
	public void get(Callback<InfosTO> cb){
		GET("enrollments", enrollmentUUID, "infos").go(cb);	
	}
}

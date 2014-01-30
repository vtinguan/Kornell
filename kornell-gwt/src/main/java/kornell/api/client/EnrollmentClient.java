package kornell.api.client;

import kornell.core.lom.Contents;

public class EnrollmentClient extends RESTClient{

	private String enrollmentUUID;

	public EnrollmentClient(String enrollmentUUID) {
		this.enrollmentUUID = enrollmentUUID;
	}
	
	public void contents(Callback<Contents> callback) {
		GET("enrollments",enrollmentUUID,"contents").go(callback);
	}
	
	public ActomClient actom(String actomKey) {
		return new ActomClient(this,actomKey);
	}
	
	public String getEnrollmentUUID(){
		return enrollmentUUID;
	}

}

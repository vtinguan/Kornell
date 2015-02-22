package kornell.api.client;

import kornell.core.entity.Enrollment;
import kornell.core.lom.Contents;

public class EnrollmentClient extends RESTClient{

	private String enrollmentUUID;

	public EnrollmentClient(String enrollmentUUID) {
		this.enrollmentUUID = enrollmentUUID;
	}
	
	public void contents(Callback<Contents> callback) {
		GET("enrollments",enrollmentUUID,"contents").go(callback);
	}
	
	public void delete(Callback<Enrollment> callback) {
		DELETE("enrollments",enrollmentUUID).go(callback);
	}
	
	public ActomClient actom(String actomKey) {
		return new ActomClient(this,actomKey);
	}
	
	public String getEnrollmentUUID(){
		return enrollmentUUID;
	}
	
	public void isApproved(Callback<Boolean> callback){
		GET("enrollments",enrollmentUUID,"approved").go(callback);		
	}

}

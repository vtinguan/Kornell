package kornell.api.client;

import kornell.core.entity.Enrollment;
import kornell.core.lom.Contents;
import kornell.core.to.ActionTO;
import kornell.core.to.CourseDetailsTO;
import kornell.core.to.EnrollmentLaunchTO;

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
	
	//TODO: DRY GET("enrollments",enrollmentUUID)
	public void isApproved(Callback<Boolean> callback){
		GET("enrollments",enrollmentUUID,"approved").go(callback);		
	}

	public void launch(Callback<EnrollmentLaunchTO> callback) {
		GET("enrollments",enrollmentUUID,"launch").go(callback);
	}
	
	

}

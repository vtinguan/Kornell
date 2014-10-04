package kornell.api.client;

import kornell.core.entity.Enrollment;
import kornell.core.lom.Contents;
import kornell.core.to.ActionTO;
import kornell.core.to.CourseDetailsTO;

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

	//TODO: DRY GET("enrollments",enrollmentUUID)
	public void launch(Callback<ActionTO> callback) {
		GET("enrollments",enrollmentUUID,"launch").go(callback);
	}

	public InfosClient infos() {
		return new InfosClient(this);
	}

	public void findDetails(Callback<CourseDetailsTO> cb) {
		GET("enrollments",enrollmentUUID,"details").go(cb);
	}

}

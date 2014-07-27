package kornell.api.client;


public class ReportClient extends RESTClient {

	public void courseClassCertificateExists(String courseClassUUID, Callback<String> callback){
		GET("/report/courseClassCertificateExists?courseClassUUID="+courseClassUUID).go(callback);		
	}
	
	public void generateCourseClassCertificate(String courseClassUUID, Callback<String> callback){
		GET("/report/certificate?courseClassUUID="+courseClassUUID).go(callback);		
	}
	
}

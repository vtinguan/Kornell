package kornell.api.client;

import java.util.logging.Logger;

import kornell.core.entity.Enrollment;
import kornell.core.entity.Enrollments;
import kornell.core.to.EnrollmentRequestsTO;
import kornell.core.to.EnrollmentsTO;

import com.google.gwt.core.client.GWT;

public class EnrollmentsClient extends RESTClient {
	
	public void getEnrollmentsByCourseClass(String courseClassUUID, Callback<EnrollmentsTO> cb) {
		GET("/enrollments/?courseClassUUID=" + courseClassUUID).sendRequest(null, cb);
	}

	public void createEnrollments(EnrollmentRequestsTO enrollmentRequests, Callback<Enrollments> cb) {
		PUT("/enrollments/requests").withContentType(EnrollmentRequestsTO.TYPE).withEntityBody(enrollmentRequests).go(cb);
	}

	public void updateEnrollment(Enrollment enrollment, Callback<Enrollment> cb) {
		PUT("/enrollments/" + enrollment.getUUID()).withContentType(Enrollment.TYPE).withEntityBody(enrollment).go(cb);
	}

	public void createEnrollment(Enrollment enrollment, Callback<Enrollment> cb) {
		POST("/enrollments").withContentType(Enrollment.TYPE).withEntityBody(enrollment).go(cb);
	}
	
	public void notesUpdated(String courseClassUUID, String notes) {
		PUT("/enrollments/" + courseClassUUID + "/notesUpdated").sendRequest(notes,
				new Callback<Void>() {
					@Override
					public void ok(Void v) {
						logger.info("notes updated");
					}
				});
	}

}

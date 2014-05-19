package kornell.api.client;

import static kornell.core.util.StringUtils.composeURL;
import kornell.core.entity.CourseClass;
import kornell.core.entity.Enrollment;
import kornell.core.entity.Enrollments;
import kornell.core.entity.Institution;
import kornell.core.entity.People;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.CourseVersionsTO;
import kornell.core.to.CoursesTO;
import kornell.core.to.EnrollmentRequestsTO;
import kornell.core.to.RegistrationRequestTO;
import kornell.core.to.RegistrationsTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.URL;

public class KornellClient extends RESTClient implements LogoutEventHandler {

	protected KornellClient() {}

	// TODO: Is this safe?
	public void getUser(String username, Callback<UserInfoTO> cb) {
		GET("/user/" + username).sendRequest(null, cb);
	}

	public void checkUser(String email, Callback<UserInfoTO> cb) {
		GET("/user/check/" + email).sendRequest(null, cb);
	}

	public void requestRegistration(RegistrationRequestTO registrationRequestTO, Callback<UserInfoTO> cb) {
		PUT("/user/registrationRequest").withContentType(RegistrationRequestTO.TYPE).withEntityBody(registrationRequestTO).go(cb);
	}

	public void requestPasswordChange(String email, String institutionName, Callback<Void> cb) {
		GET("/user/requestPasswordChange/" + URL.encodePathSegment(email) + "/" + institutionName).sendRequest(null, cb);
	}

	public void changePassword(String password, String passwordChangeUUID, Callback<UserInfoTO> cb) {
		GET("/user/changePassword/" + URL.encodePathSegment(password) + "/" + passwordChangeUUID).sendRequest(null, cb);
	}

	public void changeTargetPassword(String targetPersonUUID, String password, Callback<Void> cb) {
		PUT("/user/changePassword/" + targetPersonUUID + "/?password=" + URL.encodePathSegment(password)).sendRequest(null, cb);
	}

	public void hasPowerOver(String targetPersonUUID, Callback<Boolean> cb) {
		GET("/user/hasPowerOver/" + targetPersonUUID).sendRequest(null, cb);
	}

	public void updateUser(UserInfoTO userInfo, Callback<UserInfoTO> cb) {
		PUT("/user/" + userInfo.getPerson().getUUID()).withContentType(UserInfoTO.TYPE).withEntityBody(userInfo).go(cb);
	}

	public void sendWelcomeEmail(String userUUID, Callback<Void> cb) {
		GET("/email/welcome/" + userUUID).sendRequest(null, cb);
	}

	public void getCourseClassesTO(Callback<CourseClassesTO> cb) {
		GET("/courseClasses").sendRequest(null, cb);
	}

	public void getCourseClassesTOByInstitution(String institutionUUID, Callback<CourseClassesTO> cb) {
		GET("/courseClasses?institutionUUID="+institutionUUID).sendRequest(null, cb);
	}

	public void getAdministratedCourseClassesTOByInstitution(String institutionUUID, Callback<CourseClassesTO> cb) {
		GET("/courseClasses/administrated?institutionUUID="+institutionUUID).sendRequest(null, cb);
	}

	// TODO: extract those inner classes
	public class InstitutionClient {
		private String uuid;

		public InstitutionClient(String uuid) {
			this.uuid = uuid;
		}

		public void acceptTerms(Callback<Void> cb) {
			PUT("/institutions/" + uuid + "/acceptTerms").go(cb);
		}

		public void get(Callback<Institution> cb) {
			GET("/institutions/" + uuid).sendRequest(null, cb);
		}

		public void update(Institution institution, Callback<Institution> cb) {
			PUT("/institutions/" + uuid).withContentType(Institution.TYPE).withEntityBody(institution).go(cb);
		}
		
	}
	
	public void courseClassCertificateExists(String courseClassUUID, Callback<String> callback){
		GET("/report/courseClassCertificateExists?courseClassUUID="+courseClassUUID).go(callback);		
	}
	
	public void generateCourseClassCertificate(String courseClassUUID, Callback<String> callback){
		GET("/report/certificate?courseClassUUID="+courseClassUUID).go(callback);		
	}

	public void findInstitutionByName(String name, Callback<Institution> cb) {
		GET("/institutions/?name=" + name).sendRequest(null, cb);
	}
	
	public class RegistrationsClient {
		public void getUnsigned(Callback<RegistrationsTO> callback) {
			GET("/registrations").sendRequest("", callback);
		}
	}
	
	public class CoursesClient {
		public void findByInstitution(String institutionUUID, Callback<CoursesTO> callback) {
			GET("/courses?institutionUUID=" + institutionUUID).go(callback);
		}
	}
	
	public class CourseVersionsClient {
		public void findByCourse(String courseUUID, Callback<CourseVersionsTO> callback) {
			GET("/courseVersions?courseUUID=" + courseUUID).go(callback);
		}
	}
	
	public class CourseClassesClient {
		public void create(CourseClass courseClass, Callback<CourseClass> callback) {
			PUT("/courseClasses").withContentType(CourseClass.TYPE).withEntityBody(courseClass).go(callback);
		}
	}
	
	public class PeopleClient {
		public void findBySearchTerm(String search, String institutionUUID, Callback<People> callback) {
			GET("/people/?search="+URL.encodePathSegment(search)+"&institutionUUID="+institutionUUID).sendRequest("", callback);
		}
	}

	public RegistrationsClient registrations() {
		// TODO: Consider lifecycle
		return new RegistrationsClient();
	}

	public CoursesClient courses() {
		return new CoursesClient();
	}

	public CourseVersionsClient courseVersions() {
		return new CourseVersionsClient();
	}

	public CourseClassesClient courseClasses() {
		return new CourseClassesClient();
	}

	public InstitutionClient institution(String uuid) {
		return new InstitutionClient(uuid);
	}

	public PeopleClient people() {
		return new PeopleClient();
	}
	
	public void getEnrollmentsByCourseClass(String courseClassUUID, Callback<Enrollments> cb) {
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
						GWT.log("notes updated");
					}
				});
	}

	@Override
	public void onLogout() {
		forgetCredentials();
	}

	private void forgetCredentials() {
		ClientProperties.remove("X-KNL-A");
	}

	public CourseClassClient courseClass(String courseClassUUID) {
		return new CourseClassClient(courseClassUUID);
	}

	static final EventsClient eventsClient = new EventsClient();

	public EventsClient events() {
		return eventsClient;
	}

	@SuppressWarnings("rawtypes")
	// TODO: Remove raw type
	public void check(String src, Callback callback) {
		HEAD(src).go(callback);
	}

	public EnrollmentClient enrollment(String enrollmentUUID){
		return new EnrollmentClient(enrollmentUUID);
	}

}

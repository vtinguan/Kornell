package kornell.api.client;

import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.util.ClientProperties;

public class KornellClient extends RESTClient implements LogoutEventHandler {

	protected KornellClient() {}

	public UserClient user() {
		// TODO: Consider lifecycle
		return new UserClient();
	}

	public InstitutionsClient institutions() {
		return new InstitutionsClient();
	}
	
	public ReportClient report() {
		return new ReportClient();
	}
	
	public EmailClient email() {
		return new EmailClient();
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

	public CourseClassClient courseClass(String courseClassUUID) {
		return new CourseClassClient(courseClassUUID);
	}

	public EnrollmentClient enrollment(String enrollmentUUID){
		return new EnrollmentClient(enrollmentUUID);
	}

	public EnrollmentsClient enrollments(){
		return new EnrollmentsClient();
	}

	public ChatThreadsClient chatThreads() {
		return new ChatThreadsClient();
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

	@Override
	public void onLogout() {
		forgetCredentials();
	}

	private void forgetCredentials() {
		ClientProperties.remove(ClientProperties.X_KNL_A);
	}

}

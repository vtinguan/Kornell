package kornell.gui.client.presentation.admin.home;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kornell.api.client.Callback;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.Enrollments;
import kornell.core.entity.Person;
import kornell.core.util.UUID;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.util.loading.LoadingPopup;
import kornell.gui.client.session.UserSession;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class DeanHomePresenter implements DeanHomeView.Presenter {
	private final ClientFactory clientFactory;
	private DeanHomeView view;
	private KornellConstants constants = GWT.create(KornellConstants.class);
		
	public DeanHomePresenter(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		view = getView();
		view.setPresenter(this);
		
		//get courses for institution
		//get enrollments for course
		getEnrollments(getCourseUUID());
	}
	
	private List<Enrollment> getEnrollments(String courseUUID){
		LoadingPopup.show();
		clientFactory.getKornellClient().getEnrollmentsByCourse(courseUUID, new Callback<Enrollments>(){
			@Override
			public void ok(Enrollments enrollments){
				view.setEnrollmentList(enrollments.getEnrollments());	
				LoadingPopup.hide();
			}
		});
		return null;
	}
	
	public String getCourseUUID(){
		return constants.getDefaultCourseUUID();
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}


	private DeanHomeView getView() {
		return clientFactory.getDeanHomeView();
	}

	@Override
	public void changeEnrollmentState(final Enrollment enrollment, final EnrollmentState toState){
		LoadingPopup.show();
		UserSession.current(new Callback<UserSession>() {
			@Override
			public void ok(UserSession session) {
				String personUUID =  session.getPersonUUID();
				clientFactory.getKornellClient().events()
				.enrollmentStateChanged(enrollment.getUUID(), personUUID, enrollment.getState(), toState)
				.fire(new Callback<Void>() {
					@Override
					public void ok(Void to) {
						GWT.log("ok");
						getEnrollments(getCourseUUID());					
					}
				});;
			}
		});
	}

	@Override
	public boolean showActionButton(String actionName, EnrollmentState state){
		if("Aceitar".equals(actionName) || "Negar".equals(actionName)){
			return EnrollmentState.requested.equals(state);
		} else if("Cancelar".equals(actionName)){
			return EnrollmentState.preEnrolled.equals(state) ||
					EnrollmentState.enrolled.equals(state);
		} else if("Matricular".equals(actionName)){
			return EnrollmentState.denied.equals(state) ||
					EnrollmentState.cancelled.equals(state);
		}
		return false;
	}

	@Override
	public void onAddEnrollmentButtonClicked(String fullName, String email) {
		Window.alert("Inserir: \""+fullName+"\"<"+email+">");
		List<Enrollment> enrollmentsList = new ArrayList<Enrollment>();
		enrollmentsList.add(createEnrollment(fullName, email));
		saveEnrollments(createEnrollments(enrollmentsList));
	}

	private Enrollment createEnrollment(String fullName, String email) {
		Enrollment enrollment = clientFactory.getEntityFactory().newEnrollment().as();
		enrollment.setCourseUUID(getCourseUUID());
		enrollment.setEnrolledOn(new Date());
		enrollment.setState(EnrollmentState.preEnrolled);
		enrollment.setUUID(UUID.random());
		
		Person person = clientFactory.getEntityFactory().newPerson().as();
		person.setFullName(fullName);
		person.setEmail(email);
		enrollment.setPerson(person);
		
		return enrollment;
	}

	private Enrollments createEnrollments(List<Enrollment> enrollmentsList) {
		Enrollments enrollments = clientFactory.getEntityFactory().newEnrollments().as();
		enrollments.setEnrollments(enrollmentsList);
		return enrollments;
	}

	@Override
	public void onAddEnrollmentBatchButtonClicked(String txtAddEnrollmentBatch) {
		Window.alert("Inserir: "+txtAddEnrollmentBatch);
		List<Enrollment> enrollmentsList = new ArrayList<Enrollment>();
		saveEnrollments(createEnrollments(createEnrollmentsList(txtAddEnrollmentBatch)));
	}

	private List<Enrollment> createEnrollmentsList(String txtAddEnrollmentBatch) {
		List<Enrollment> enrollmentsList = new ArrayList<Enrollment>();
		String[] enrollmentsA = txtAddEnrollmentBatch.split("\n");
		String enrollmentStr, fullName, email;
		String[] enrollmentStrA;
		for (int i = 0; i < enrollmentsA.length; i++) {
			enrollmentStr = enrollmentsA[i];
			enrollmentStrA = enrollmentStr.split(";");
			fullName = (enrollmentStrA.length > 1 ? enrollmentStrA[0] : "");
			email = (enrollmentStrA.length > 1 ? enrollmentStrA[1] : enrollmentStrA[0]); 
			enrollmentsList.add(createEnrollment(fullName, email));
			GWT.log(fullName + " - " + email);
		}
		return enrollmentsList;
	}

	private void saveEnrollments(Enrollments enrollments) {
		LoadingPopup.show();
		clientFactory.getKornellClient().createEnrollments(enrollments, new Callback<Enrollments>() {
			@Override
			public void ok(Enrollments to) {
				getEnrollments(getCourseUUID());
			}
		});
	}
}
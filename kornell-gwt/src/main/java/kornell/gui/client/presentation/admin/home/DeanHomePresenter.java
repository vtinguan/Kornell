package kornell.gui.client.presentation.admin.home;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.UserSession;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.Enrollments;
import kornell.core.entity.Institution;
import kornell.core.entity.Person;
import kornell.core.to.EnrollmentRequestTO;
import kornell.core.to.EnrollmentRequestsTO;
import kornell.core.util.UUID;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.course.CourseClassPlace;
import kornell.gui.client.presentation.util.LoadingPopup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class DeanHomePresenter implements DeanHomeView.Presenter {
	private final ClientFactory clientFactory;
	private DeanHomeView view;
	private KornellConstants constants = GWT.create(KornellConstants.class);
		
	public DeanHomePresenter(ClientFactory factory) {
		clientFactory = factory;
		//TODO refactor permissions per session/activity
		UserSession.current(new Callback<UserSession>() {
			@Override
			public void ok(UserSession session) {
				init(session.isDean());
			}
		});
	}
	
	private void init(boolean isDean) {
		if(isDean){
			view = getView();
			view.setPresenter(this);
			getEnrollments(clientFactory.getCurrentCourseClass().getCourseClass().getUUID());
		}
		else {
			GWT.log("Hey, only deans are allowed to see this! " + this.getClass().getName());
			clientFactory.getPlaceController()
				.goTo(clientFactory.getDefaultPlace());
		}
	}

	private List<Enrollment> getEnrollments(String courseClassUUID){
		LoadingPopup.show();
		clientFactory.getKornellClient().getEnrollmentsByCourseClass(courseClassUUID, new Callback<Enrollments>(){
			@Override
			public void ok(Enrollments enrollments){
				view.setEnrollmentList(enrollments.getEnrollments());	
				LoadingPopup.hide();
			}
		});
		return null;
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
						getEnrollments(clientFactory.getCurrentCourseClass().getCourseClass().getUUID());					
					}
				});
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
		List<EnrollmentRequestTO> enrollmentsList = new ArrayList<EnrollmentRequestTO>();
		enrollmentsList.add(createEnrollment(fullName, email));
		saveEnrollments(createEnrollments(enrollmentsList));
	}

	@Override
	public void onAddEnrollmentBatchButtonClicked(String txtAddEnrollmentBatch) {
		Window.alert("Inserir: "+txtAddEnrollmentBatch);
		saveEnrollments(createEnrollments(createEnrollmentsList(txtAddEnrollmentBatch)));
	}

	private EnrollmentRequestsTO createEnrollments(List<EnrollmentRequestTO> enrollmentRequestsList) {
		EnrollmentRequestsTO enrollmentRequestsTO = clientFactory.getTOFactory().newEnrollmentRequestsTO().as();
		enrollmentRequestsTO.setEnrollmentRequests(enrollmentRequestsList);
		return enrollmentRequestsTO;
	}

	private List<EnrollmentRequestTO> createEnrollmentsList(String txtAddEnrollmentBatch) {
		List<EnrollmentRequestTO> enrollmentsList = new ArrayList<EnrollmentRequestTO>();
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

	private EnrollmentRequestTO createEnrollment(String fullName, String email) {
		EnrollmentRequestTO enrollmentRequestTO = clientFactory.getTOFactory().newEnrollmentRequestTO().as();

		enrollmentRequestTO.setInstitutionUUID(clientFactory.getInstitution().getUUID());
		enrollmentRequestTO.setCourseClassUUID(clientFactory.getCurrentCourseClass().getCourseClass().getUUID());
		enrollmentRequestTO.setEmail(email);
		enrollmentRequestTO.setFullName(fullName);
		
		return enrollmentRequestTO;
	}

	private void saveEnrollments(EnrollmentRequestsTO enrollmentRequests) {
		LoadingPopup.show();
		clientFactory.getKornellClient().createEnrollments(enrollmentRequests, new Callback<Enrollments>() {
			@Override
			public void ok(Enrollments to) {
				getEnrollments(clientFactory.getCurrentCourseClass().getCourseClass().getUUID());
			}
		});
	}

	@Override
	public void onGoToCourseButtonClicked() {
		CourseClassPlace place = new CourseClassPlace(clientFactory.getCurrentCourseClass().getCourseClass().getUUID());
		clientFactory.setDefaultPlace(place);
		clientFactory.getPlaceController().goTo(place);
	}
	
	private void updateInstitution() {
		clientFactory.getInstitution().setAssetsURL(clientFactory.getInstitution().getAssetsURL()+"x");
		clientFactory.getUserSession().institution(clientFactory.getInstitution().getUUID()).update(clientFactory.getInstitution(),
				new Callback<Institution>() {
					@Override
					public void ok(Institution institution) {
						GWT.log(institution.getName());
					}
				});
	}
	

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}


	private DeanHomeView getView() {
		return clientFactory.getDeanHomeView();
	}
}
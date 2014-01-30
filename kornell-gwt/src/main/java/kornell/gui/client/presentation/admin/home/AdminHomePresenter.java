package kornell.gui.client.presentation.admin.home;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.UserSession;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.Enrollments;
import kornell.core.entity.Institution;
import kornell.core.to.CourseClassTO;
import kornell.core.to.EnrollmentRequestTO;
import kornell.core.to.EnrollmentRequestsTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

public class AdminHomePresenter implements AdminHomeView.Presenter {
	private final ClientFactory clientFactory;
	private AdminHomeView view;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private String batchEnrollmentErrors;
	private List<EnrollmentRequestTO> batchEnrollments;
	FormHelper formHelper;

	public AdminHomePresenter(ClientFactory factory) {
		clientFactory = factory;
		formHelper = new FormHelper();
		// TODO refactor permissions per session/activity
		UserSession.current(new Callback<UserSession>() {
			@Override
			public void ok(UserSession session) {
				init(session.isInstitutionAdmin());
			}
		});
	}

	private void init(boolean isDean) {
		if (isDean) {
			view = getView();
			view.setPresenter(this);
			
			CourseClassTO to = Dean.getInstance().getCourseClassTO(); 
			
			view.setCourseClassName(to.getCourseClass().getName());
			view.setCourseName(to.getCourseVersionTO().getCourse().getTitle());
			
			getEnrollments(to.getCourseClass().getUUID());
		} else {
			GWT.log("Hey, only admins are allowed to see this! "
					+ this.getClass().getName());
			clientFactory.getPlaceController().goTo(
					clientFactory.getDefaultPlace());
		}
	}

	private List<Enrollment> getEnrollments(String courseClassUUID) {
		LoadingPopup.show();
		clientFactory.getKornellClient().getEnrollmentsByCourseClass(
				courseClassUUID, new Callback<Enrollments>() {
					@Override
					public void ok(Enrollments enrollments) {
						view.setEnrollmentList(enrollments.getEnrollments());
						LoadingPopup.hide();
					}
				});
		return null;
	}

	@Override
	public void changeEnrollmentState(final Enrollment enrollment,
			final EnrollmentState toState) {
		LoadingPopup.show();
		UserSession.current(new Callback<UserSession>() {
			@Override
			public void ok(UserSession session) {
				String personUUID = session.getUserInfo().getPerson().getUUID();
				clientFactory
						.getKornellClient()
						.events()
						.enrollmentStateChanged(enrollment.getUUID(),
								personUUID, enrollment.getState(), toState)
						.fire(new Callback<Void>() {
							@Override
							public void ok(Void to) {
								getEnrollments(Dean.getInstance()
										.getCourseClassTO().getCourseClass()
										.getUUID());
							}
						});
			}
		});
	}

	@Override
	public boolean showActionButton(String actionName, EnrollmentState state) {
		if ("Aceitar".equals(actionName) || "Negar".equals(actionName)) {
			return EnrollmentState.requested.equals(state);
		} else if ("Cancelar".equals(actionName)) {
			return EnrollmentState.preEnrolled.equals(state)
					|| EnrollmentState.enrolled.equals(state);
		} else if ("Matricular".equals(actionName)) {
			return EnrollmentState.denied.equals(state)
					|| EnrollmentState.cancelled.equals(state);
		}
		return false;
	}

	@Override
	public void onAddEnrollmentButtonClicked(String fullName, String email) {
		batchEnrollments = new ArrayList<EnrollmentRequestTO>();
		batchEnrollments.add(createEnrollment(fullName, email));
		if (formHelper.isEmailValid(email)) {
			saveEnrollments(createEnrollments());
		} else {
			KornellNotification.show("Email inválido.", AlertType.ERROR);
		}
	}

	@Override
	public void onAddEnrollmentBatchButtonClicked(String txtAddEnrollmentBatch) {
		populateEnrollmentsList(txtAddEnrollmentBatch);
		if (batchEnrollmentErrors == null || !"".equals(batchEnrollmentErrors)) {
			view.setModalErrors(batchEnrollmentErrors);
			view.showModal();
		} else {
			saveEnrollments(createEnrollments());
		}
	}

	private EnrollmentRequestsTO createEnrollments() {
		EnrollmentRequestsTO enrollmentRequestsTO = clientFactory
				.getTOFactory().newEnrollmentRequestsTO().as();
		enrollmentRequestsTO.setEnrollmentRequests(batchEnrollments);
		return enrollmentRequestsTO;
	}

	private void populateEnrollmentsList(String txtAddEnrollmentBatch) {
		String[] enrollmentsA = txtAddEnrollmentBatch.split("\n");
		String fullName, email;
		String[] enrollmentStrA;
		batchEnrollments = new ArrayList<EnrollmentRequestTO>();
		batchEnrollmentErrors = "";
		for (int i = 0; i < enrollmentsA.length; i++) {
			if ("".equals(enrollmentsA[i].trim()))
				continue;
			enrollmentStrA = enrollmentsA[i].split(";");
			fullName = (enrollmentStrA.length > 1 ? enrollmentStrA[0] : "");
			email = (enrollmentStrA.length > 1 ? enrollmentStrA[1]
					: enrollmentStrA[0]);
			GWT.log("*** Validating: " + fullName + " - " + email);
			if (formHelper.isEmailValid(email)) {
				batchEnrollments.add(createEnrollment(fullName, email));
			} else {
				batchEnrollmentErrors += enrollmentsA[i] + "\n";
			}
		}
	}

	private EnrollmentRequestTO createEnrollment(String fullName, String email) {
		EnrollmentRequestTO enrollmentRequestTO = clientFactory.getTOFactory()
				.newEnrollmentRequestTO().as();

		enrollmentRequestTO.setInstitutionUUID(Dean.getInstance()
				.getInstitution().getUUID());
		enrollmentRequestTO.setCourseClassUUID(Dean.getInstance()
				.getCourseClassTO().getCourseClass().getUUID());
		enrollmentRequestTO.setEmail(email);
		enrollmentRequestTO.setFullName(fullName);

		return enrollmentRequestTO;
	}

	private void saveEnrollments(EnrollmentRequestsTO enrollmentRequests) {
		if (enrollmentRequests.getEnrollmentRequests().size() == 0) {
			KornellNotification
					.show("Verifique se os nomes/emails dos usuários estão corretos. Nenhuma matrícula encontrada.",
							AlertType.WARNING);
			return;
		} else if (enrollmentRequests.getEnrollmentRequests().size() > 5) {
			KornellNotification
					.show("Solicitação de matrículas enviada para o servidor. Você receberá uma confirmação quando a operação for concluída (Tempo estimado: "
							+ enrollmentRequests.getEnrollmentRequests().size()
							* 2 + " segundos).", AlertType.INFO);
		} else {
			LoadingPopup.show();
		}
		clientFactory.getKornellClient().createEnrollments(enrollmentRequests,
				new Callback<Enrollments>() {
					@Override
					public void ok(Enrollments to) {
						getEnrollments(Dean.getInstance().getCourseClassTO()
								.getCourseClass().getUUID());
						KornellNotification
								.show("Matrículas feitas com sucesso.");
					}
				});
	}

	@Override
	public void onModalOkButtonClicked() {
		saveEnrollments(createEnrollments());
	}

	@Override
	public void onGoToCourseButtonClicked() {
		clientFactory.getPlaceController().goTo(
				new ClassroomPlace(Dean.getInstance().getCourseClassTO()
						.getEnrollment().getUUID()));
	}

	private void updateInstitution() {
		clientFactory
				.getUserSession()
				.institution(Dean.getInstance().getInstitution().getUUID())
				.update(Dean.getInstance().getInstitution(),
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

	private AdminHomeView getView() {
		return clientFactory.getViewFactory().getDeanHomeView();
	}

	@Override
	public void onUserClicked(String uuid) {
		ProfilePlace place = new ProfilePlace(uuid, false);
		clientFactory.getPlaceController().goTo(place);
	}
}
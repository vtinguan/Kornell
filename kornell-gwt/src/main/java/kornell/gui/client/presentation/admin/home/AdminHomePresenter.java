package kornell.gui.client.presentation.admin.home;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.Enrollments;
import kornell.core.entity.Institution;
import kornell.core.entity.RoleType;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.EnrollmentRequestTO;
import kornell.core.to.EnrollmentRequestsTO;
import kornell.core.to.TOFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;

public class AdminHomePresenter implements AdminHomeView.Presenter {
	private AdminHomeView view;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private List<Enrollment> enrollments;
	private String batchEnrollmentErrors;
	private List<EnrollmentRequestTO> batchEnrollments;
	FormHelper formHelper;
	private KornellSession session;
	private PlaceController placeController;
	private Place defaultPlace;
	TOFactory toFactory;
	private ViewFactory viewFactory;
	private Boolean enrollWithCPF = false;
	private Integer maxEnrollments = 0;
	private Integer numEnrollments = 0;
	private CourseClassesTO courseClassesTO;

	public AdminHomePresenter(KornellSession session,
			PlaceController placeController, Place defaultPlace,
			TOFactory toFactory, ViewFactory viewFactory) {
		this.session = session;
		this.placeController = placeController;
		this.defaultPlace = defaultPlace;
		this.toFactory = toFactory;
		this.viewFactory = viewFactory;
		formHelper = new FormHelper();
		// TODO refactor permissions per session/activity
		init();
	}

	private void init() {
		if (session.hasRole(RoleType.courseClassAdmin) || session.isInstitutionAdmin()) {
			view = getView();
			view.setPresenter(this);

			session.getAdministratedCourseClassesTOByInstitution(Dean.getInstance().getInstitution().getUUID(), new Callback<CourseClassesTO>() {
				@Override
				public void ok(CourseClassesTO to) {
					courseClassesTO = to;
					view.setCourseClasses(courseClassesTO.getCourseClasses());
					updateCourseClass(courseClassesTO.getCourseClasses().get(0));
				}
			});
		} else {
			GWT.log("Hey, only admins are allowed to see this! "
					+ this.getClass().getName());
			placeController.goTo(defaultPlace);
		}
	}

	private List<Enrollment> getEnrollments(String courseClassUUID) {
		LoadingPopup.show();
		session.getEnrollmentsByCourseClass(courseClassUUID,
				new Callback<Enrollments>() {

					@Override
					public void ok(Enrollments e) {
						numEnrollments = e.getEnrollments().size();
						maxEnrollments = Dean.getInstance().getCourseClassTO().getCourseClass().getMaxEnrollments();
						enrollments = e.getEnrollments();
						view.setEnrollmentList(e.getEnrollments());
						view.showEnrollmentsPanel(true);
						LoadingPopup.hide();
					}
				});
		return null;
	}
	
	@Override
	public void updateCourseClass(final String courseClassUUID){

		session.getAdministratedCourseClassesTOByInstitution(Dean.getInstance().getInstitution().getUUID(), new Callback<CourseClassesTO>() {
			@Override
			public void ok(CourseClassesTO to) {
				courseClassesTO = to;
				view.setCourseClasses(courseClassesTO.getCourseClasses());
				
				for (CourseClassTO courseClassTO : courseClassesTO.getCourseClasses()) {
					if(courseClassTO.getCourseClass().getUUID().equals(courseClassUUID)){
						updateCourseClass(courseClassTO);
						break;
					}
				}
			}
		});
	}

	private void updateCourseClass(CourseClassTO courseClassTO) {
		enrollWithCPF = courseClassTO.getCourseClass().isEnrollWithCPF();
		Dean.getInstance().setCourseClassTO(courseClassTO);
		view.prepareAddNewCourseClass(false);
		view.showEnrollmentsPanel(false);
		view.setCourseClassName(courseClassTO.getCourseClass().getName());
		view.setCourseName(courseClassTO.getCourseVersionTO().getCourse().getTitle());
		view.setUserEnrollmentIdentificationType(enrollWithCPF);
		view.setSelectedCourseClass(courseClassTO.getCourseClass().getUUID());
		view.setHomeTabActive();
		getEnrollments(courseClassTO.getCourseClass().getUUID());
	}

	@Override
	public void changeEnrollmentState(final Enrollment enrollment,
			final EnrollmentState toState) {
		LoadingPopup.show();

		String personUUID = session.getCurrentUser().getPerson().getUUID();
		session.events()
				.enrollmentStateChanged(enrollment.getUUID(), personUUID,
						enrollment.getState(), toState)
				.fire(new Callback<Void>() {
					@Override
					public void ok(Void to) {
						getEnrollments(Dean.getInstance().getCourseClassTO()
								.getCourseClass().getUUID());
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
	public void onAddEnrollmentButtonClicked(String fullName, String username) {
		if("".equals(fullName) && "".equals(username)){
			return;
		}
		if(enrollWithCPF){
			username = formHelper.stripCPF(username);
		}
		batchEnrollments = new ArrayList<EnrollmentRequestTO>();
		batchEnrollments.add(createEnrollment(fullName, username));
		if(!formHelper.isLengthValid(fullName, 2, 50)){
			KornellNotification.show("O nome deve ter no mínimo 2 caracteres.", AlertType.ERROR);
		} else if(!isUsernameValid(username)){
			KornellNotification.show((enrollWithCPF?"CPF":"Email") +" inválido.", AlertType.ERROR);
		} else {
			saveEnrollments(createEnrollments());
		}
	}

	private boolean isUsernameValid(String email) {
		return (!enrollWithCPF && formHelper.isEmailValid(email)) || 
			(enrollWithCPF && formHelper.isCPFValid(email));
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

		EnrollmentRequestsTO enrollmentRequestsTO = toFactory
				.newEnrollmentRequestsTO().as();
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
			if (isUsernameValid(email)) {
				batchEnrollments.add(createEnrollment(fullName, email));
			} else {
				batchEnrollmentErrors += enrollmentsA[i] + "\n";
			}
		}
	}

	private EnrollmentRequestTO createEnrollment(String fullName, String email) {
		EnrollmentRequestTO enrollmentRequestTO = toFactory
				.newEnrollmentRequestTO().as();

		enrollmentRequestTO.setInstitutionUUID(Dean.getInstance()
				.getInstitution().getUUID());
		enrollmentRequestTO.setCourseClassUUID(Dean.getInstance()
				.getCourseClassTO().getCourseClass().getUUID());
		enrollmentRequestTO.setFullName(fullName);
		if(enrollWithCPF)
			enrollmentRequestTO.setCPF(formHelper.stripCPF(email));
		else
			enrollmentRequestTO.setEmail(email);

		return enrollmentRequestTO;
	}

	private void saveEnrollments(EnrollmentRequestsTO enrollmentRequests) {
		if (enrollmentRequests.getEnrollmentRequests().size() == 0) {
			KornellNotification
					.show("Verifique se os nomes/"+ (enrollWithCPF?"cpfs":"emails") +" dos usuários estão corretos. Nenhuma matrícula encontrada.",
							AlertType.WARNING);
			return;
		} else if((enrollmentRequests.getEnrollmentRequests().size() + numEnrollments) > maxEnrollments){
			KornellNotification
			.show("Não foi possível concluir a requisição. Verifique a quantidade de matrículas disponíveis nesta turma",
					AlertType.ERROR, 5000);
			return;
		} else if (!enrollWithCPF && enrollmentRequests.getEnrollmentRequests().size() > 5) {
			KornellNotification
					.show("Solicitação de matrículas enviada para o servidor. Você receberá uma confirmação quando a operação for concluída (Tempo estimado: "
							+ enrollmentRequests.getEnrollmentRequests().size()
							* 2 + " segundos).", AlertType.INFO, 6000);
		} else {
			LoadingPopup.show();
		}
		session.createEnrollments(enrollmentRequests,
				new Callback<Enrollments>() {
					@Override
					public void ok(Enrollments to) {
						getEnrollments(Dean.getInstance().getCourseClassTO()
								.getCourseClass().getUUID());
						KornellNotification
								.show("Matrículas feitas com sucesso.", 1500);
					}
				});
	}

	@Override
	public void onModalOkButtonClicked() {
		saveEnrollments(createEnrollments());
	}

	@Override
	public void onGoToCourseButtonClicked() {
		placeController.goTo(new ClassroomPlace(Dean.getInstance()
				.getCourseClassTO().getEnrollment().getUUID()));
	}

	private void updateInstitution() {
		session.institution(Dean.getInstance().getInstitution().getUUID())
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
		return viewFactory.getAdminHomeView();
	}

	@Override
	public void onUserClicked(String uuid) {
		ProfilePlace place = new ProfilePlace(uuid, false);
		placeController.goTo(place);
	}

	@Override
	public List<Enrollment> getEnrollments() {
		return enrollments;
	}
}
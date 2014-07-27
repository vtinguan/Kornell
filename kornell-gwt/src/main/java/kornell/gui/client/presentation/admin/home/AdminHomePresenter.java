package kornell.gui.client.presentation.admin.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentCategory;
import kornell.core.entity.EnrollmentProgressDescription;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.Enrollments;
import kornell.core.entity.Institution;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.EnrollmentRequestTO;
import kornell.core.to.EnrollmentRequestsTO;
import kornell.core.to.EnrollmentTO;
import kornell.core.to.EnrollmentsTO;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class AdminHomePresenter implements AdminHomeView.Presenter {
	private AdminHomeView view;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private List<EnrollmentTO> enrollmentTOs;
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
	private boolean hasOverriddenEnrollments = false, overriddenEnrollmentsModalShown = false, confirmedEnrollmentsModal = false;
  private EnrollmentRequestsTO enrollmentRequestsTO;
  private List<EnrollmentTO> enrollmentsToOverride;

	public AdminHomePresenter(KornellSession session,
			PlaceController placeController, Place defaultPlace,
			TOFactory toFactory, ViewFactory viewFactory) {
		this.session = session;
		this.placeController = placeController;
		this.defaultPlace = defaultPlace;
		this.toFactory = toFactory;
		this.viewFactory = viewFactory;
		formHelper = new FormHelper();
		enrollmentRequestsTO = toFactory.newEnrollmentRequestsTO().as();
		// TODO refactor permissions per session/activity
		init();
	}

	private void init() {
		if (RoleCategory.hasRole(session.getCurrentUser().getRoles(),
				RoleType.courseClassAdmin) || session.isInstitutionAdmin()) {
			view = getView();
			view.setPresenter(this);

			session.courseClasses().getAdministratedCourseClassesTOByInstitution(Dean
					.getInstance().getInstitution().getUUID(),
					new Callback<CourseClassesTO>() {
						@Override
						public void ok(CourseClassesTO to) {
							courseClassesTO = to;
							view.setCourseClasses(courseClassesTO
									.getCourseClasses());
							updateCourseClassUI(courseClassesTO
									.getCourseClasses().get(0));
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
		session.enrollments().getEnrollmentsByCourseClass(courseClassUUID,
				new Callback<EnrollmentsTO>() {

					@Override
					public void ok(EnrollmentsTO e) {
						numEnrollments = e.getEnrollmentTOs().size();
						maxEnrollments = Dean.getInstance().getCourseClassTO()
								.getCourseClass().getMaxEnrollments();
						enrollmentTOs = e.getEnrollmentTOs();
						view.setEnrollmentList(e.getEnrollmentTOs());
						view.showEnrollmentsPanel(true);
						LoadingPopup.hide();
					}
				});
		return null;
	}

	@Override
	public void updateCourseClass(final String courseClassUUID) {

		session.courseClasses().getAdministratedCourseClassesTOByInstitution(Dean.getInstance().getInstitution().getUUID(), 
				new Callback<CourseClassesTO>() {
			@Override
			public void ok(CourseClassesTO to) {
				courseClassesTO = to;
				view.setCourseClasses(courseClassesTO.getCourseClasses());
				if(courseClassesTO.getCourseClasses().size() == 0){
					updateCourseClassUI(null);
				} else {
					for (CourseClassTO courseClassTO : courseClassesTO.getCourseClasses()) {
						if (courseClassUUID == null || courseClassTO.getCourseClass().getUUID().equals(courseClassUUID)) {
							updateCourseClassUI(courseClassTO);
							break;
						}
					}
				}
			}
		});
	}

	private void updateCourseClassUI(CourseClassTO courseClassTO) {
		view.showTabsPanel(courseClassTO != null);
		view.prepareAddNewCourseClass(false);
		view.showEnrollmentsPanel(false);
		view.setHomeTabActive();
		if(courseClassTO == null)
			return;
		enrollWithCPF = courseClassTO.getCourseClass().isEnrollWithCPF();
		Dean.getInstance().setCourseClassTO(courseClassTO);
		view.setCourseClassName(courseClassTO.getCourseClass().getName());
		view.setCourseName(courseClassTO.getCourseVersionTO().getCourse()
				.getTitle());
		view.setUserEnrollmentIdentificationType(enrollWithCPF);
		view.setSelectedCourseClass(courseClassTO.getCourseClass().getUUID());
		getEnrollments(courseClassTO.getCourseClass().getUUID());
	}

	@Override
	public void changeEnrollmentState(final EnrollmentTO enrollmentTO,
			final EnrollmentState toState) {
		LoadingPopup.show();

		String personUUID = session.getCurrentUser().getPerson().getUUID();
		session.events()
				.enrollmentStateChanged(enrollmentTO.getEnrollment().getUUID(), personUUID,
						enrollmentTO.getEnrollment().getState(), toState)
				.fire(new Callback<Void>() {
					@Override
					public void ok(Void to) {
						getEnrollments(Dean.getInstance().getCourseClassTO()
								.getCourseClass().getUUID());
					}
				});

	}

	@Override
	public void changeCourseClassState(final CourseClassTO courseClassTO,
			final CourseClassState toState) {
		LoadingPopup.show();

		String personUUID = session.getCurrentUser().getPerson().getUUID();
		session.events()
				.courseClassStateChanged(courseClassTO.getCourseClass().getUUID(), personUUID,
						courseClassTO.getCourseClass().getState(), toState)
				.fire(new Callback<Void>() {
					@Override
					public void ok(Void to) {
						LoadingPopup.hide();
						KornellNotification.show("Turma excluída com sucesso!");
						updateCourseClass(null);
					}
					
					@Override
					public void unauthorized(String errorMessage){
						LoadingPopup.hide();
						GWT.log(this.getClass().getName() + " - " + errorMessage);
					}
				});

	}

	@Override
	public boolean showActionButton(String actionName, EnrollmentTO enrollmentTO) {
		boolean isEnabled = CourseClassState.active.equals(Dean.getInstance()
				.getCourseClassTO().getCourseClass().getState());
		EnrollmentState state = enrollmentTO.getEnrollment().getState();
		EnrollmentProgressDescription progressDescription = EnrollmentCategory.getEnrollmentProgressDescription(enrollmentTO.getEnrollment());
		if ("Aceitar".equals(actionName) || "Negar".equals(actionName)) {
			return isEnabled && EnrollmentState.requested.equals(state);
		} else if ("Cancelar".equals(actionName)) {
			return isEnabled && EnrollmentState.enrolled.equals(state);
		} else if ("Matricular".equals(actionName)) {
			return isEnabled && (EnrollmentState.denied.equals(state)
					|| EnrollmentState.cancelled.equals(state));
		} else if ("Excluir".equals(actionName)){
			return isEnabled && EnrollmentProgressDescription.notStarted.equals(progressDescription);
		} else if("Perfil".equals(actionName)){
			return true;
		} else if("Certificado".equals(actionName)){
			return EnrollmentCategory.isFinished(enrollmentTO.getEnrollment());
		}
		return false;
	}

	@Override
	public void onAddEnrollmentButtonClicked(String fullName, String username) {
		if ("".equals(fullName) && "".equals(username)) {
			return;
		}
		if (enrollWithCPF) {
			username = formHelper.stripCPF(username);
		}
		batchEnrollments = new ArrayList<EnrollmentRequestTO>();
		batchEnrollments.add(createEnrollment(fullName, username, false));
		if (!formHelper.isLengthValid(fullName, 2, 50)) {
			KornellNotification.show("O nome deve ter no mínimo 2 caracteres.",
					AlertType.ERROR);
		} else if (!isUsernameValid(username)) {
			KornellNotification.show((enrollWithCPF ? "CPF" : "Email")
					+ " inválido.", AlertType.ERROR);
		} else {
			prepareCreateEnrollments(false);
		}
	}
	

	private boolean isUsernameValid(String email) {
		return (!enrollWithCPF && formHelper.isEmailValid(email))
				|| (enrollWithCPF && formHelper.isCPFValid(email));
	}

	@Override
	public void onAddEnrollmentBatchButtonClicked(String txtAddEnrollmentBatch) {
		populateEnrollmentsList(txtAddEnrollmentBatch);
		if (batchEnrollmentErrors == null || !"".equals(batchEnrollmentErrors)) {
			view.setModalErrors("Erros ao inserir matrículas", "As seguintes linhas contém erros:", batchEnrollmentErrors, "Deseja ignorar essas linhas e continuar?");
			overriddenEnrollmentsModalShown = false;
			view.showModal(true);
		} else {
			prepareCreateEnrollments(true);
		}
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
			if (isUsernameValid(email)) {
				batchEnrollments.add(createEnrollment(fullName, email, false));
			} else {
				batchEnrollmentErrors += enrollmentsA[i] + "\n";
			}
		}
	} 

	private EnrollmentRequestTO createEnrollment(String fullName, String email, boolean cancelEnrollment) {
		EnrollmentRequestTO enrollmentRequestTO = toFactory
				.newEnrollmentRequestTO().as();
		
		enrollmentRequestTO.setCancelEnrollment(cancelEnrollment);
		enrollmentRequestTO.setInstitutionUUID(Dean.getInstance()
				.getInstitution().getUUID());
		enrollmentRequestTO.setCourseClassUUID(Dean.getInstance()
				.getCourseClassTO().getCourseClass().getUUID());
		enrollmentRequestTO.setFullName(fullName);
		if (enrollWithCPF || email.indexOf('@') == -1)
			enrollmentRequestTO.setCPF(formHelper.stripCPF(email));
		else
			enrollmentRequestTO.setEmail(email);
		
	

		return enrollmentRequestTO;
	}

	private void prepareCreateEnrollments(boolean isBatch) {
		enrollmentRequestsTO.setEnrollmentRequests(batchEnrollments);
		if (CourseClassState.inactive.equals(Dean.getInstance()
				.getCourseClassTO().getCourseClass().getState())) {
			KornellNotification
					.show("Não é possível matricular alunos em uma turma desabilidada.",
							AlertType.ERROR);
			return;
		} else if (enrollmentRequestsTO.getEnrollmentRequests().size() == 0) {
				KornellNotification
						.show("Verifique se os nomes/"
								+ (enrollWithCPF ? "cpfs" : "emails")
								+ " dos usuários estão corretos. Nenhuma matrícula encontrada.",
								AlertType.WARNING);
		} else if ((enrollmentRequestsTO.getEnrollmentRequests().size() + numEnrollments) > maxEnrollments) {
			KornellNotification
					.show("Não foi possível concluir a requisição. Verifique a quantidade de matrículas disponíveis nesta turma",
							AlertType.ERROR, 5000);
		} else {
			hasOverriddenEnrollments = false;
			if(isBatch && Dean.getInstance().getCourseClassTO().getCourseClass().isOverrideEnrollments()){
				String validation = validateEnrollmentsOverride();
				if(confirmedEnrollmentsModal || "".equals(validation)){
					createEnrollments();
				} else {
					hasOverriddenEnrollments = true;
					overriddenEnrollmentsModalShown = true;
					confirmedEnrollmentsModal = false;
					view.setModalErrors("ATENÇÃO! Sobrescrita de matrículas!", "Os seguintes participantes terão suas matrículas canceladas:", validation, "Deseja continuar?");
					view.showModal(true);
				}
			} else {
				createEnrollments();
			}
		}
		
	}
	
	private String validateEnrollmentsOverride(){		
		Map<String, EnrollmentRequestTO> enrollmentRequestsMap = new HashMap<String, EnrollmentRequestTO>();
		for (EnrollmentRequestTO enrollmentRequestTO : enrollmentRequestsTO.getEnrollmentRequests()) {
			String username = enrollmentRequestTO.getCPF() != null ? 
					enrollmentRequestTO.getCPF() :
						enrollmentRequestTO.getEmail();
				enrollmentRequestsMap.put(username, enrollmentRequestTO);
    }

		String validation = "";
		
		enrollmentsToOverride = new ArrayList<EnrollmentTO>();
		for (Iterator<EnrollmentTO> iterator = enrollmentTOs.iterator(); iterator.hasNext();) {
	    EnrollmentTO enrollmentTO = (EnrollmentTO) iterator.next();
			String username = enrollmentTO.getPerson().getCPF() != null ? 
					enrollmentTO.getPerson().getCPF() :
						enrollmentTO.getPerson().getEmail();
			//if the user was already enrolled and is not on the new list, cancel enrollment
	    if(!enrollmentRequestsMap.containsKey(username) && EnrollmentState.enrolled.equals(enrollmentTO.getEnrollment().getState())){
	    	enrollmentsToOverride.add(enrollmentTO);
	    	validation += username + "\n";
	    }
    }
		
		return validation;
	}
	
	private void createEnrollments() {
		
		if(confirmedEnrollmentsModal && enrollmentsToOverride != null && enrollmentsToOverride.size() > 0){
			for (EnrollmentTO enrollmentToOverrideTO : enrollmentsToOverride) {
				String username = enrollmentToOverrideTO.getPerson().getCPF() != null ? 
						enrollmentToOverrideTO.getPerson().getCPF() :
							enrollmentToOverrideTO.getPerson().getEmail();
				EnrollmentRequestTO enrollmentRequestTO = createEnrollment(enrollmentToOverrideTO.getPerson().getFullName(), username, true);
				enrollmentRequestsTO.getEnrollmentRequests().add(enrollmentRequestTO);  
      }
			
		}
		
		if (!enrollWithCPF
				&& enrollmentRequestsTO.getEnrollmentRequests().size() > 5) {
			KornellNotification
					.show("Solicitação de matrículas enviada para o servidor. Você receberá uma confirmação quando a operação for concluída (Tempo estimado: "
							+ enrollmentRequestsTO.getEnrollmentRequests().size() + " segundos).", AlertType.INFO, 6000);
		}
		LoadingPopup.show();
		session.enrollments().createEnrollments(enrollmentRequestsTO,
				new Callback<Enrollments>() {
					@Override
					public void ok(Enrollments to) {
						getEnrollments(Dean.getInstance().getCourseClassTO()
								.getCourseClass().getUUID());
						confirmedEnrollmentsModal = false;
						KornellNotification.show(
								"Matrículas feitas com sucesso.", 1500);
						LoadingPopup.hide();
					}
					
					@Override
					public void unauthorized(String error){
						GWT.log("Error AdminHomePresenter: " + error);
						KornellNotification.show("Erro ao criar matrícula(s).", AlertType.ERROR, 2500);
						LoadingPopup.hide();
					}
				});
	}
	
		

	@Override
	public void onModalOkButtonClicked() {
		view.showModal(false);
		if(overriddenEnrollmentsModalShown){
			confirmedEnrollmentsModal = true;
		}
		prepareCreateEnrollments(true);
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
	public void onUserClicked(EnrollmentTO enrollmentTO) {
		ProfilePlace place = new ProfilePlace(enrollmentTO.getPerson().getUUID(), false);
		placeController.goTo(place);
	}

	@Override
	public void onGenerateCertificate(EnrollmentTO enrollmentTO) {
		KornellNotification.show("Aguarde um instante...", AlertType.INFO, 2000);
		Window.Location.assign(session.getApiUrl() + "/report/certificate/"
				+ enrollmentTO.getPerson().getUUID() + "/"
				+ enrollmentTO.getEnrollment().getCourseClassUUID());
	}
	
	@Override
	public List<EnrollmentTO> getEnrollments() {
		return enrollmentTOs;
	}

	@Override
  public void deleteEnrollment(EnrollmentTO enrollmentTO) {
		session.enrollment(enrollmentTO.getEnrollment().getUUID()).delete(new Callback<Enrollment>() {
			@Override
			public void ok(Enrollment to) {
				KornellNotification.show("Matrícula excluída com sucesso.", AlertType.SUCCESS, 2000);
				getEnrollments(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID());
			}
		});
  }
}
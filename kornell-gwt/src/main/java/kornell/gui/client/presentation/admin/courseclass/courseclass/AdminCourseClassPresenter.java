package kornell.gui.client.presentation.admin.courseclass.courseclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentCategory;
import kornell.core.entity.EnrollmentProgressDescription;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.Enrollments;
import kornell.core.entity.InstitutionType;
import kornell.core.entity.RegistrationType;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.error.KornellErrorTO;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.EnrollmentRequestTO;
import kornell.core.to.EnrollmentRequestsTO;
import kornell.core.to.EnrollmentTO;
import kornell.core.to.EnrollmentsTO;
import kornell.core.to.TOFactory;
import kornell.core.util.StringUtils;
import kornell.gui.client.KornellConstantsHelper;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.mvp.PlaceUtils;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;
import kornell.gui.client.util.ClientProperties;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class AdminCourseClassPresenter implements AdminCourseClassView.Presenter {
	Logger logger = Logger.getLogger(AdminCourseClassPresenter.class.getName());
	private AdminCourseClassView view;
	private List<EnrollmentTO> enrollmentTOs;
	private String batchEnrollmentErrors;
	private List<EnrollmentRequestTO> batchEnrollments;
	FormHelper formHelper;
	private KornellSession session;
	private PlaceController placeController;
	private Place defaultPlace;
	TOFactory toFactory;
	private ViewFactory viewFactory;
	private Integer maxEnrollments = 0;
	private Integer numEnrollments = 0;
	private CourseClassesTO courseClassesTO;
	private boolean overriddenEnrollmentsModalShown = false, confirmedEnrollmentsModal = false;
	private EnrollmentRequestsTO enrollmentRequestsTO;
	private List<EnrollmentTO> enrollmentsToOverride;
	private Map<String, EnrollmentsTO> enrollmentsCacheMap;
	private EventBus bus;

	private static final String PREFIX = ClientProperties.PREFIX + "AdminHome";

	public AdminCourseClassPresenter(KornellSession session, EventBus bus, PlaceController placeController,
			Place defaultPlace, TOFactory toFactory, ViewFactory viewFactory) {
		this.session = session;
		this.bus = bus;
		this.placeController = placeController;
		this.defaultPlace = defaultPlace;
		this.toFactory = toFactory;
		this.viewFactory = viewFactory;
		formHelper = new FormHelper();
		enrollmentRequestsTO = toFactory.newEnrollmentRequestsTO().as();
		enrollmentsCacheMap = new HashMap<String, EnrollmentsTO>();
		// TODO refactor permissions per session/activity

		Timer cacheCleanerTimer = new Timer() {
			public void run() {
				clearEnrollmentsCache();
			}
		};

		// Schedule the timer to run every 15 minutes
		cacheCleanerTimer.scheduleRepeating(15 * 60 * 1000);
		init();

		bus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				if (event.getNewPlace() instanceof AdminCourseClassPlace)
					init();
			}
		});
	}

	private void init() {
		if (RoleCategory.hasRole(session.getCurrentUser().getRoles(), RoleType.courseClassAdmin)
				|| session.isInstitutionAdmin()) {
			view = getView();
			view.setPresenter(this);
			String selectedCourseClass;
			if (placeController.getWhere() instanceof AdminCourseClassPlace
					&& ((AdminCourseClassPlace) placeController.getWhere()).getCourseClassUUID() != null) {
				selectedCourseClass = ((AdminCourseClassPlace) placeController.getWhere()).getCourseClassUUID();
			} else {
				selectedCourseClass = ClientProperties.get(getLocalStoragePropertyName());
			}
			updateCourseClass(selectedCourseClass);

			clearEnrollmentsCache();
		} else {
			logger.warning("Hey, only admins are allowed to see this! " + this.getClass().getName());
			placeController.goTo(defaultPlace);
		}
	}

	private void getEnrollments(final String courseClassUUID) {
		ClientProperties.set(getLocalStoragePropertyName(), courseClassUUID);
		EnrollmentsTO enrollments = getCachedEnrollments(courseClassUUID);
		if (enrollments != null) {
			showEnrollments(enrollments, true);
		} else {
			LoadingPopup.show();
			session.enrollments().getEnrollmentsByCourseClass(courseClassUUID, new Callback<EnrollmentsTO>() {
				@Override
				public void ok(EnrollmentsTO enrollments) {
					LoadingPopup.hide();
					if (courseClassUUID.equals(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID())) {
						showEnrollments(enrollments, true);
						updateCachedEnrollments(courseClassUUID, enrollments);
					}
				}
			});
		}
	}

	private synchronized EnrollmentsTO getCachedEnrollments(String courseClassUUID) {
		return enrollmentsCacheMap == null ? null : enrollmentsCacheMap.get(courseClassUUID);
	}

	private synchronized void updateCachedEnrollments(String courseClassUUID, EnrollmentsTO enrollments) {
		if (courseClassUUID != null && enrollments != null) {
			enrollmentsCacheMap.put(courseClassUUID, enrollments);
		}
	}

	private synchronized void clearEnrollmentsCache() {
		enrollmentsCacheMap.clear();
	}

	private void showEnrollments(EnrollmentsTO e, boolean refreshView) {
		numEnrollments = e.getEnrollmentTOs().size();
		maxEnrollments = Dean.getInstance().getCourseClassTO().getCourseClass().getMaxEnrollments();
		enrollmentTOs = e.getEnrollmentTOs();
		view.setEnrollmentList(e.getEnrollmentTOs(), refreshView);
		view.showEnrollmentsPanel(true);
	}

	@Override
	public void updateCourseClass(final String courseClassUUID) {
		if (!enrollmentsCacheMap.containsKey(courseClassUUID)) {
			view.showEnrollmentsPanel(false);
		}
		LoadingPopup.show();
		session.courseClasses().getAdministratedCourseClassesTOByInstitution(
				Dean.getInstance().getInstitution().getUUID(), new Callback<CourseClassesTO>() {
					@Override
					public void ok(CourseClassesTO to) {
						courseClassesTO = to;
						LoadingPopup.hide();
						if (courseClassesTO.getCourseClasses().size() == 0) {
							updateCourseClassUI(null);
						} else {
							for (CourseClassTO courseClassTO : courseClassesTO.getCourseClasses()) {
								if (courseClassUUID == null
										|| courseClassTO.getCourseClass().getUUID().equals(courseClassUUID)) {
									updateCourseClassUI(courseClassTO);
									return;
								}
							}
							if (courseClassesTO != null && courseClassesTO.getCourseClasses().size() > 0) {
								updateCourseClassUI(courseClassesTO.getCourseClasses().get(0));
							} else {
								updateCourseClassUI(null);
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
		if (courseClassTO == null)
			return;
		Dean.getInstance().setCourseClassTO(courseClassTO);
		view.setCourseClassName(courseClassTO.getCourseClass().getName());
		view.setCourseName(courseClassTO.getCourseVersionTO().getCourse().getTitle());
		view.setUserEnrollmentIdentificationType(courseClassTO.getCourseClass().getRegistrationType());
		getEnrollments(courseClassTO.getCourseClass().getUUID());
	}

	private String getLocalStoragePropertyName() {
		return PREFIX + ClientProperties.SEPARATOR + Dean.getInstance().getInstitution().getUUID()
				+ ClientProperties.SEPARATOR + ClientProperties.SELECTED_COURSE_CLASS;
	}

	@Override
	public void changeEnrollmentState(final EnrollmentTO enrollmentTO, final EnrollmentState toState) {
		LoadingPopup.show();

		enrollmentsCacheMap.remove(enrollmentTO.getEnrollment().getCourseClassUUID());

		String personUUID = session.getCurrentUser().getPerson().getUUID();
		LoadingPopup.show();
		session.events()
				.enrollmentStateChanged(enrollmentTO.getEnrollment().getUUID(), personUUID,
						enrollmentTO.getEnrollment().getState(), toState).fire(new Callback<Void>() {
					@Override
					public void ok(Void to) {
						LoadingPopup.hide();
						getEnrollments(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID());
						view.setCanPerformEnrollmentAction(true);
						KornellNotification.show("Alteração feita com sucesso.", 2000);
					}
				});

	}

	@Override
	public void changeCourseClassState(final CourseClassTO courseClassTO, final CourseClassState toState) {
		LoadingPopup.show();

		enrollmentsCacheMap.remove(courseClassTO.getCourseClass().getUUID());

		String personUUID = session.getCurrentUser().getPerson().getUUID();
		session.events()
				.courseClassStateChanged(courseClassTO.getCourseClass().getUUID(), personUUID,
						courseClassTO.getCourseClass().getState(), toState).fire(new Callback<Void>() {
					@Override
					public void ok(Void to) {
						LoadingPopup.hide();
						KornellNotification.show("Turma excluída com sucesso!");
						updateCourseClass(null);
					}

					@Override
					public void unauthorized(KornellErrorTO kornellErrorTO) {
						LoadingPopup.hide();
						KornellNotification.show("Erro ao tentar excluir a turma.", AlertType.ERROR);
						logger.severe(this.getClass().getName() + " - "
								+ KornellConstantsHelper.getUnauthorizedMessage(kornellErrorTO));
					}
				});

	}

	@Override
	public boolean showActionButton(String actionName, EnrollmentTO enrollmentTO) {
		boolean isEnabled = CourseClassState.active.equals(Dean.getInstance().getCourseClassTO().getCourseClass()
				.getState());
		EnrollmentState state = enrollmentTO.getEnrollment().getState();
		EnrollmentProgressDescription progressDescription = EnrollmentCategory
				.getEnrollmentProgressDescription(enrollmentTO.getEnrollment());
		if ("Aceitar".equals(actionName) || "Negar".equals(actionName)) {
			return isEnabled && EnrollmentState.requested.equals(state);
		} else if ("Cancelar".equals(actionName)) {
			return isEnabled && EnrollmentState.enrolled.equals(state);
		} else if ("Matricular".equals(actionName)) {
			return isEnabled && (EnrollmentState.denied.equals(state) || EnrollmentState.cancelled.equals(state));
		} else if ("Excluir".equals(actionName)) {
			return isEnabled && EnrollmentProgressDescription.notStarted.equals(progressDescription);
		} else if ("Perfil".equals(actionName)) {
			return true;
		} else if ("Certificado".equals(actionName)) {
			return EnrollmentCategory.isFinished(enrollmentTO.getEnrollment());
		}
		return false;
	}

	@Override
	public void onAddEnrollmentButtonClicked(String fullName, String username) {
		if ("".equals(fullName) && "".equals(username)) {
			return;
		}
		if (RegistrationType.cpf.equals(Dean.getInstance().getCourseClassTO().getCourseClass().getRegistrationType())) {
			username = FormHelper.stripCPF(username);
		}
		batchEnrollments = new ArrayList<EnrollmentRequestTO>();
		batchEnrollments.add(createEnrollment(fullName, username, false));
		if (!formHelper.isLengthValid(fullName, 2, 50)) {
			KornellNotification.show("O nome deve ter no mínimo 2 caracteres.", AlertType.ERROR);
		} else if (!isUsernameValid(username)) {
			KornellNotification.show(
					formHelper.getRegistrationTypeAsText(Dean.getInstance().getCourseClassTO().getCourseClass()
							.getRegistrationType())
							+ " inválido.", AlertType.ERROR);
		} else {
			prepareCreateEnrollments(false);
		}
	}

	private boolean isUsernameValid(String username) {
		switch (Dean.getInstance().getCourseClassTO().getCourseClass().getRegistrationType()) {
		case email:
			return FormHelper.isEmailValid(username);
		case cpf:
			return FormHelper.isCPFValid(username);
		case username:
			return FormHelper.isUsernameValid(username);
		default:
			return false;
		}
	}

	@Override
	public void onAddEnrollmentBatchButtonClicked(String txtAddEnrollmentBatch) {
		populateEnrollmentsList(txtAddEnrollmentBatch);
		if (batchEnrollmentErrors == null || !"".equals(batchEnrollmentErrors)) {
			view.setModalErrors("Erros ao inserir matrículas", "As seguintes linhas contém erros:",
					batchEnrollmentErrors, "Deseja ignorar essas linhas e continuar?");
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
			enrollmentStrA = enrollmentsA[i].indexOf(';') >= 0 ? enrollmentsA[i].split(";") : enrollmentsA[i]
					.split("\\t");
			fullName = (enrollmentStrA.length > 1 ? enrollmentStrA[0] : "");
			email = (enrollmentStrA.length > 1 ? enrollmentStrA[1] : enrollmentStrA[0]).replace((char) 160, (char) 32)
					.trim();
			if (isUsernameValid(email)) {
				batchEnrollments.add(createEnrollment(fullName, email, false));
			} else {
				batchEnrollmentErrors += enrollmentsA[i] + "\n";
			}
		}
	}

	private EnrollmentRequestTO createEnrollment(String fullName, String username, boolean cancelEnrollment) {
		fullName.trim();
		username.trim();
		String usr;
		EnrollmentRequestTO enrollmentRequestTO = toFactory.newEnrollmentRequestTO().as();

		enrollmentRequestTO.setCancelEnrollment(cancelEnrollment);
		enrollmentRequestTO.setInstitutionUUID(Dean.getInstance().getInstitution().getUUID());
		if(InstitutionType.DASHBOARD.equals(Dean.getInstance().getInstitution().getInstitutionType())){
			enrollmentRequestTO.setCourseVersionUUID(Dean.getInstance().getCourseClassTO().getCourseVersionTO().getCourseVersion().getUUID());
		} else {
			enrollmentRequestTO.setCourseClassUUID(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID());
		}
		enrollmentRequestTO.setFullName(fullName);
		enrollmentRequestTO.setRegistrationType(Dean.getInstance().getCourseClassTO().getCourseClass()
				.getRegistrationType());
		switch (Dean.getInstance().getCourseClassTO().getCourseClass().getRegistrationType()) {
		case email:
			enrollmentRequestTO.setUsername(username);
			break;
		case cpf:
			usr = FormHelper.stripCPF(username);
			enrollmentRequestTO.setUsername(usr);
			enrollmentRequestTO.setPassword(usr);
			break;
		case username:
			usr = !cancelEnrollment && username.indexOf(FormHelper.USERNAME_SEPARATOR) == -1 ? Dean.getInstance().getCourseClassTO()
					.getRegistrationPrefix()
					+ FormHelper.USERNAME_SEPARATOR + username : username;
			enrollmentRequestTO.setUsername(usr);
			enrollmentRequestTO.setPassword(username);
			enrollmentRequestTO.setInstitutionRegistrationPrefixUUID(Dean.getInstance().getCourseClassTO()
					.getCourseClass().getInstitutionRegistrationPrefixUUID());
			break;
		default:
			break;
		}
		enrollmentRequestTO.setRegistrationType(Dean.getInstance().getCourseClassTO().getCourseClass()
				.getRegistrationType());
		return enrollmentRequestTO;
	}

	private void prepareCreateEnrollments(boolean isBatch) {
		enrollmentRequestsTO.setEnrollmentRequests(batchEnrollments);
		if (CourseClassState.inactive.equals(Dean.getInstance().getCourseClassTO().getCourseClass().getState())) {
			KornellNotification.show("Não é possível matricular participantes em uma turma desabilidada.",
					AlertType.ERROR);
			return;
		} else if (enrollmentRequestsTO.getEnrollmentRequests().size() == 0) {
			KornellNotification
					.show("Verifique se os nomes/"
							+ formHelper.getRegistrationTypeAsText(
									Dean.getInstance().getCourseClassTO().getCourseClass().getRegistrationType())
									.toLowerCase() + " dos participantes estão corretos. Nenhuma matrícula encontrada.",
							AlertType.WARNING);
		} else if ((enrollmentRequestsTO.getEnrollmentRequests().size() + numEnrollments) > maxEnrollments) {
			KornellNotification
					.show("Não foi possível concluir a requisição. Verifique a quantidade de matrículas disponíveis nesta turma",
							AlertType.ERROR, 5000);
		} else {
			if (isBatch && Dean.getInstance().getCourseClassTO().getCourseClass().isOverrideEnrollments()) {
				String validation = validateEnrollmentsOverride();
				if (confirmedEnrollmentsModal || "".equals(validation)) {
					createEnrollments();
				} else {
					overriddenEnrollmentsModalShown = true;
					confirmedEnrollmentsModal = false;
					view.setModalErrors("ATENÇÃO! Sobrescrita de matrículas!",
							"Os seguintes participantes terão suas matrículas canceladas:", validation,
							"Deseja continuar?");
					view.showModal(true);
				}
			} else {
				createEnrollments();
			}
		}

	}

	private String validateEnrollmentsOverride() {
		Map<String, EnrollmentRequestTO> enrollmentRequestsMap = new HashMap<String, EnrollmentRequestTO>();
		for (EnrollmentRequestTO enrollmentRequestTO : enrollmentRequestsTO.getEnrollmentRequests()) {
			enrollmentRequestsMap.put(enrollmentRequestTO.getUsername(), enrollmentRequestTO);
		}

		String validation = "";

		enrollmentsToOverride = new ArrayList<EnrollmentTO>();
		for (Iterator<EnrollmentTO> iterator = enrollmentTOs.iterator(); iterator.hasNext();) {
			EnrollmentTO enrollmentTO = (EnrollmentTO) iterator.next();
			String username = enrollmentTO.getUsername();
			// if the user was already enrolled and is not on the new list,
			// cancel enrollment
			if (!enrollmentRequestsMap.containsKey(username)
					&& EnrollmentState.enrolled.equals(enrollmentTO.getEnrollment().getState())) {
				enrollmentsToOverride.add(enrollmentTO);
				validation += username + 
						(StringUtils.isSome(enrollmentTO.getFullName()) ? 
								" (" + enrollmentTO.getFullName() + ")\n" :
									"");
			}
		}

		return validation;
	}

	private void createEnrollments() {

		if (confirmedEnrollmentsModal && enrollmentsToOverride != null && enrollmentsToOverride.size() > 0) {
			for (EnrollmentTO enrollmentToOverrideTO : enrollmentsToOverride) {
				EnrollmentRequestTO enrollmentRequestTO = createEnrollment(enrollmentToOverrideTO.getFullName(),
						enrollmentToOverrideTO.getUsername(), true);
				enrollmentRequestsTO.getEnrollmentRequests().add(enrollmentRequestTO);
			}

		}
		LoadingPopup.show();
		final int requestsThreshold = 100;
		if(enrollmentRequestsTO.getEnrollmentRequests().size() > requestsThreshold){
			if(StringUtils.isSome(session.getCurrentUser().getPerson().getEmail())){
				KornellNotification.show("Solicitação de matrículas enviada para o servidor. Você receberá uma email em \"" + session.getCurrentUser().getPerson().getEmail() + "\" assim que a operação for concluída.", AlertType.INFO, 20000);
			} else {
				KornellNotification.show("Favor configurar um email no seu perfil para que possa receber as mensagens de confirmação de matrículas em lote.", AlertType.INFO, 8000);
			}
			LoadingPopup.hide();
			confirmedEnrollmentsModal = false;
			view.clearEnrollmentFields();
		} else if (RegistrationType.email.equals(Dean.getInstance().getCourseClassTO().getCourseClass().getRegistrationType())
				&& enrollmentRequestsTO.getEnrollmentRequests().size() > 5) {
			KornellNotification
					.show("Solicitação de matrículas enviada para o servidor. Você receberá uma confirmação quando a operação for concluída (Tempo estimado: "
							+ enrollmentRequestsTO.getEnrollmentRequests().size() + " segundos).", AlertType.INFO, 6000);
		}

		enrollmentsCacheMap.remove(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID());
		session.enrollments().createEnrollments(enrollmentRequestsTO, new Callback<Enrollments>() {
			@Override
			public void ok(Enrollments to) {
				if(enrollmentRequestsTO.getEnrollmentRequests().size() <= requestsThreshold){
					getEnrollments(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID());
					confirmedEnrollmentsModal = false;
					KornellNotification.show("Matrículas feitas com sucesso.", 1500);
					view.clearEnrollmentFields();
					LoadingPopup.hide();
					PlaceUtils.reloadCurrentPlace(bus, placeController);
				}
			}

			@Override
			public void unauthorized(KornellErrorTO kornellErrorTO) {
				logger.severe("Error AdminHomePresenter: "
						+ KornellConstantsHelper.getUnauthorizedMessage(kornellErrorTO));
				KornellNotification.show("Erro ao criar matrícula(s).", AlertType.ERROR, 2500);
				LoadingPopup.hide();
			}
		});
	}

	@Override
	public void onModalOkButtonClicked() {
		view.showModal(false);
		if (overriddenEnrollmentsModalShown) {
			confirmedEnrollmentsModal = true;
		}
		prepareCreateEnrollments(true);
	}

	@Override
	public void onGoToCourseButtonClicked() {
		placeController.goTo(new ClassroomPlace(Dean.getInstance().getCourseClassTO().getEnrollment().getUUID()));
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	private AdminCourseClassView getView() {
		return viewFactory.getAdminCourseClassView();
	}

	@Override
	public void onUserClicked(EnrollmentTO enrollmentTO) {
		ProfilePlace place = new ProfilePlace(enrollmentTO.getPersonUUID(), false);
		placeController.goTo(place);
	}

	@Override
	public void onGenerateCertificate(EnrollmentTO enrollmentTO) {
		KornellNotification.show("Aguarde um instante...", AlertType.INFO, 2000);
		Window.Location.assign(session.getApiUrl() + "/report/certificate/" + enrollmentTO.getPersonUUID() + "/"
				+ enrollmentTO.getEnrollment().getCourseClassUUID());
	}

	@Override
	public List<EnrollmentTO> getEnrollments() {
		return enrollmentTOs;
	}

	@Override
	public void deleteEnrollment(EnrollmentTO enrollmentTO) {
		enrollmentsCacheMap.remove(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID());
		session.enrollment(enrollmentTO.getEnrollment().getUUID()).delete(new Callback<Enrollment>() {
			@Override
			public void ok(Enrollment to) {
				KornellNotification.show("Matrícula excluída com sucesso.", AlertType.SUCCESS, 2000);
				getEnrollments(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID());
				view.setCanPerformEnrollmentAction(true);
			}
		});
	}

	@Override
	public void upsertCourseClass(CourseClass courseClass) {
		if (courseClass.getUUID() == null) {
			courseClass.setCreatedBy(session.getCurrentUser().getPerson().getUUID());
			session.courseClasses().create(courseClass, new Callback<CourseClass>() {
				@Override
				public void ok(CourseClass courseClass) {
					LoadingPopup.hide();
					KornellNotification.show("Turma criada com sucesso!");
					CourseClassTO courseClassTO2 = Dean.getInstance().getCourseClassTO();
					if (courseClassTO2 != null)
						courseClassTO2.setCourseClass(courseClass);
					PlaceUtils.reloadCurrentPlace(bus, placeController);
				}

				@Override
				public void conflict(KornellErrorTO kornellErrorTO) {
					LoadingPopup.hide();
					KornellNotification.show(KornellConstantsHelper.getConflictMessage(kornellErrorTO),
							AlertType.ERROR, 2500);
				}
			});
		} else {
			enrollmentsCacheMap.remove(courseClass.getUUID());
			session.courseClass(courseClass.getUUID()).update(courseClass, new Callback<CourseClass>() {
				@Override
				public void ok(CourseClass courseClass) {
					LoadingPopup.hide();
					KornellNotification.show("Alterações salvas com sucesso!");
					Dean.getInstance().getCourseClassTO().setCourseClass(courseClass);
					updateCourseClass(courseClass.getUUID());
				}

				@Override
				public void conflict(KornellErrorTO kornellErrorTO) {
					LoadingPopup.hide();
					KornellNotification.show(KornellConstantsHelper.getConflictMessage(kornellErrorTO),
							AlertType.ERROR, 2500);
				}
			});
		}
	}
}
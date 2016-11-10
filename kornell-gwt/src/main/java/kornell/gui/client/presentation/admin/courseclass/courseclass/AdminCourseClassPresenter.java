package kornell.gui.client.presentation.admin.courseclass.courseclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseClassState;
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
import kornell.core.to.EnrollmentRequestTO;
import kornell.core.to.EnrollmentRequestsTO;
import kornell.core.to.EnrollmentTO;
import kornell.core.to.EnrollmentsTO;
import kornell.core.to.SimplePeopleTO;
import kornell.core.to.SimplePersonTO;
import kornell.core.to.TOFactory;
import kornell.core.util.StringUtils;
import kornell.gui.client.KornellConstantsHelper;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.mvp.PlaceUtils;
import kornell.gui.client.presentation.admin.courseclass.courseclasses.AdminCourseClassesPlace;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.util.ClientProperties;
import kornell.gui.client.util.EnumTranslator;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.LoadingPopup;

public class AdminCourseClassPresenter implements AdminCourseClassView.Presenter {
    Logger logger = Logger.getLogger(AdminCourseClassPresenter.class.getName());
    private AdminCourseClassView view;
    private List<EnrollmentTO> enrollmentTOs;
    private String batchEnrollmentErrors;
    private List<EnrollmentRequestTO> batchEnrollments;
    private FormHelper formHelper;
    private KornellSession session;
    private PlaceController placeController;
    private Place defaultPlace;
    private TOFactory toFactory;
    private ViewFactory viewFactory;
    private boolean overriddenEnrollmentsModalShown = false, confirmedEnrollmentsModal = false;
    private EnrollmentRequestsTO enrollmentRequestsTO;
    private List<SimplePersonTO> enrollmentsToOverride;
    private EventBus bus;
    private String pageSize = "20";
    private String pageNumber = "1";
    private String searchTerm = "";

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
        // TODO refactor permissions per session/activity
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
                || RoleCategory.hasRole(session.getCurrentUser().getRoles(), RoleType.observer)
                || RoleCategory.hasRole(session.getCurrentUser().getRoles(), RoleType.tutor)
                || session.isInstitutionAdmin()) {
            view = getView();
            view.showEnrollmentsPanel(false);
            view.setPresenter(this);
            view.clearPagination();
            String selectedCourseClass;
            if (placeController.getWhere() instanceof AdminCourseClassPlace
                    && ((AdminCourseClassPlace) placeController.getWhere()).getCourseClassUUID() != null) {
                selectedCourseClass = ((AdminCourseClassPlace) placeController.getWhere()).getCourseClassUUID();
            } else {
                selectedCourseClass = ClientProperties.get(getLocalStoragePropertyName());
            }
            updateCourseClass(selectedCourseClass);
        } else {
            logger.warning("Hey, only admins are allowed to see this! " + this.getClass().getName());
            placeController.goTo(defaultPlace);
        }
    }

    private void getEnrollments(final String courseClassUUID) {
        ClientProperties.set(getLocalStoragePropertyName(), courseClassUUID);
        LoadingPopup.show();
        session.enrollments().getEnrollmentsByCourseClass(courseClassUUID, pageSize, pageNumber, searchTerm, new Callback<EnrollmentsTO>() {
            @Override
            public void ok(EnrollmentsTO enrollments) {
                LoadingPopup.hide();
                if (courseClassUUID.equals(session.getCurrentCourseClass().getCourseClass().getUUID())) {
                    showEnrollments(enrollments, true);
                }
            } 
        });
    }

    private void showEnrollments(EnrollmentsTO e, boolean refreshView) {
        enrollmentTOs = e.getEnrollmentTOs();
        view.setEnrollmentList(e.getEnrollmentTOs(), e.getCount(), e.getCountCancelled(), e.getSearchCount(), refreshView);
        view.showEnrollmentsPanel(true);
    }

    @Override
    public void updateCourseClass(final String courseClassUUID) {
        if(placeController.getWhere() instanceof AdminCourseClassPlace){
            LoadingPopup.show();
            view.showEnrollmentsPanel(false);
            session.courseClass(courseClassUUID).getTO(new Callback<CourseClassTO>() {
                @Override            
                public void ok(CourseClassTO courseClassTO) {
                    LoadingPopup.hide();
                    updateCourseClassUI(courseClassTO);
                }
            });
        } else {
            updateCourseClassUI(null);
        }
    }

    @Override
    public void updateCourseClassUI(CourseClassTO courseClassTO) {
        view.showTabsPanel(courseClassTO != null);
        view.prepareAddNewCourseClass(false);
        view.showEnrollmentsPanel(false);
        view.setHomeTabActive();
        if (courseClassTO == null)
            return;
        session.setCurrentCourseClass(courseClassTO);        
        view.setCourseClassTO(courseClassTO);
        view.setUserEnrollmentIdentificationType(courseClassTO.getCourseClass().getRegistrationType());
        view.setCanPerformEnrollmentAction(true);
        getEnrollments(courseClassTO.getCourseClass().getUUID());
    }

    private String getLocalStoragePropertyName() {
        return PREFIX + ClientProperties.SEPARATOR + session.getInstitution().getUUID()
                + ClientProperties.SEPARATOR + ClientProperties.SELECTED_COURSE_CLASS;
    }

    @Override
    public void changeEnrollmentState(final EnrollmentTO enrollmentTO, final EnrollmentState toState) {
        if(session.isCourseClassAdmin()){
            LoadingPopup.show();

            String personUUID = session.getCurrentUser().getPerson().getUUID();
            LoadingPopup.show();
            session.events()
            .enrollmentStateChanged(enrollmentTO.getEnrollment().getUUID(), personUUID,
                    enrollmentTO.getEnrollment().getState(), toState).fire(new Callback<Void>() {
                        @Override
                        public void ok(Void to) {
                            LoadingPopup.hide();
                            getEnrollments(session.getCurrentCourseClass().getCourseClass().getUUID());
                            view.setCanPerformEnrollmentAction(true);
                            KornellNotification.show("Alteração feita com sucesso.", 2000);
                        }
                    });
        }
    }

    @Override
    public void changeCourseClassState(final CourseClassTO courseClassTO, final CourseClassState toState) {
        LoadingPopup.show();

        String personUUID = session.getCurrentUser().getPerson().getUUID();
        session.events()
        .courseClassStateChanged(courseClassTO.getCourseClass().getUUID(), personUUID,
                courseClassTO.getCourseClass().getState(), toState).fire(new Callback<Void>() {
                    @Override
                    public void ok(Void to) {
                        LoadingPopup.hide();
                        if(CourseClassState.inactive.equals(toState)){
                            KornellNotification.show("Turma desabilitada com sucesso!");
                            updateCourseClass(courseClassTO.getCourseClass().getUUID());
                        } else {
                            KornellNotification.show("Turma excluída com sucesso!");
                            placeController.goTo(new AdminCourseClassesPlace());
                        }
                    }

                    @Override
                    public void unauthorized(KornellErrorTO kornellErrorTO) {
                        LoadingPopup.hide();
                        KornellNotification.show("Erro ao tentar excluir a turma.", AlertType.ERROR);
                        logger.severe(this.getClass().getName() + " - "
                                + KornellConstantsHelper.getErrorMessage(kornellErrorTO));
                    }
                });

    }

    @Override
    public boolean showActionButton(String actionName, EnrollmentTO enrollmentTO) {
        boolean isEnabled = CourseClassState.active.equals(session.getCurrentCourseClass().getCourseClass()
                .getState());
        EnrollmentState state = enrollmentTO.getEnrollment().getState();
        EnrollmentProgressDescription progressDescription = EnrollmentCategory
                .getEnrollmentProgressDescription(enrollmentTO.getEnrollment());
        if ("Aceitar".equals(actionName) || "Negar".equals(actionName)) {
            return isEnabled && EnrollmentState.requested.equals(state) && session.isCourseClassAdmin();
        } else if ("Cancelar".equals(actionName)) {
            return isEnabled && EnrollmentState.enrolled.equals(state) && session.isCourseClassAdmin();
        } else if ("Matricular".equals(actionName)) {
            return isEnabled && (EnrollmentState.denied.equals(state) || EnrollmentState.cancelled.equals(state)) && session.isCourseClassAdmin();
        } else if ("Excluir".equals(actionName)) {
            return isEnabled && EnrollmentProgressDescription.notStarted.equals(progressDescription) && session.isCourseClassAdmin();
        } else if ("Perfil".equals(actionName)) {
            return session.isCourseClassAdmin() || session.isCourseClassTutor();
        } else if ("Certificado".equals(actionName)) {
            return EnrollmentCategory.isFinished(enrollmentTO.getEnrollment()) && (session.isCourseClassAdmin() || session.isCourseClassObserver() || session.isCourseClassTutor());
        } else if("Transferir".equals(actionName)){
            return session.isCourseClassAdmin();
        }
        return false;
    }

    @Override
    public void onAddEnrollmentButtonClicked(String fullName, String username) {
        if ("".equals(fullName) && "".equals(username)) {
            return;
        }
        username = username.replaceAll("\\u200B", "").trim();
        if (RegistrationType.cpf.equals(session.getCurrentCourseClass().getCourseClass().getRegistrationType())) {
            username = FormHelper.stripCPF(username);
        }
        batchEnrollments = new ArrayList<EnrollmentRequestTO>();
        batchEnrollments.add(createEnrollment(fullName, username, null, false));
        if (!formHelper.isLengthValid(fullName, 2, 50)) {
            KornellNotification.show("O nome deve ter no mínimo 2 caracteres.", AlertType.ERROR);
        } else if (!isUsernameValid(username)) {
            KornellNotification.show(
            		EnumTranslator.translateEnum(session.getCurrentCourseClass().getCourseClass()
                            .getRegistrationType())
                            + " inválido.", AlertType.ERROR);
        } else {
            prepareCreateEnrollments(false);
        }
    }

    private boolean isUsernameValid(String username) {
        switch (session.getCurrentCourseClass().getCourseClass().getRegistrationType()) {
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
        populateEnrollmentsList(txtAddEnrollmentBatch, false);
        if (batchEnrollmentErrors == null || !"".equals(batchEnrollmentErrors)) {
            view.setModalErrors("Erros ao inserir matrículas", "As seguintes linhas contém erros:",
                    batchEnrollmentErrors, "Deseja ignorar essas linhas e continuar?");
            overriddenEnrollmentsModalShown = false;
            view.showModal(true, "error");
        } else {
            prepareCreateEnrollments(true);
        }
    }

	@Override
	public void onBatchCancelModalOkButtonClicked(String txtCancelEnrollmentBatch) {
        populateEnrollmentsList(txtCancelEnrollmentBatch, true);
        enrollmentRequestsTO.setEnrollmentRequests(batchEnrollments);

        for (EnrollmentRequestTO enrollmentRequestTO : enrollmentRequestsTO.getEnrollmentRequests()) {
        	enrollmentRequestTO.setCancelEnrollment(true);
        }
        
        LoadingPopup.show();
        final int requestsThreshold = 200;
        if(enrollmentRequestsTO.getEnrollmentRequests().size() > requestsThreshold){
            KornellNotification.show("Solicitação de cancelamento de matrículas enviada para o servidor.", AlertType.WARNING, 20000);
            LoadingPopup.hide();
            view.clearEnrollmentFields();
        }

        if(session.isCourseClassAdmin(session.getCurrentCourseClass().getCourseClass().getUUID())) {
            session.enrollments().createEnrollments(enrollmentRequestsTO, new Callback<Enrollments>() {
                @Override
                public void ok(Enrollments to) {
                    getEnrollments(session.getCurrentCourseClass().getCourseClass().getUUID());
                    KornellNotification.show("Matrículas canceladas com sucesso.", 1500);
                    view.clearEnrollmentFields();
                    LoadingPopup.hide();
                    PlaceUtils.reloadCurrentPlace(bus, placeController);
                }

                @Override
                public void unauthorized(KornellErrorTO kornellErrorTO) {
                    logger.severe("Error AdminHomePresenter: "
                            + KornellConstantsHelper.getErrorMessage(kornellErrorTO));
                    KornellNotification.show("Erro ao cancelar matrícula(s).", AlertType.ERROR, 2500);
                    LoadingPopup.hide();
                }
            });
        }
	}

    private void populateEnrollmentsList(String txtAddEnrollmentBatch, boolean isBatchCancel) {
        String[] enrollmentsA = txtAddEnrollmentBatch.split("\n");
        String fullName, username, email;
        String[] enrollmentStrA;
        batchEnrollments = new ArrayList<EnrollmentRequestTO>();
        batchEnrollmentErrors = "";
        for (int i = 0; i < enrollmentsA.length; i++) {
            if ("".equals(enrollmentsA[i].trim()))
                continue;
            enrollmentStrA = enrollmentsA[i].indexOf(';') >= 0 ? enrollmentsA[i].split(";") : enrollmentsA[i]
                    .split("\\t");
            fullName = (enrollmentStrA.length > 1 ? enrollmentStrA[0] : "");
            username = (enrollmentStrA.length > 1 ? enrollmentStrA[1] : enrollmentStrA[0]).replace((char) 160, (char) 32).replaceAll("\\u200B", "")
                    .trim();
            email = (enrollmentStrA.length > 2 ? enrollmentStrA[2].replace((char) 160, (char) 32).replaceAll("\\u200B", "").trim() : null);
            if (isBatchCancel) {
            	fullName = fullName.trim();
            	username = username.trim();
                EnrollmentRequestTO enrollmentRequestTO = toFactory.newEnrollmentRequestTO().as();

                enrollmentRequestTO.setCancelEnrollment(true);
                enrollmentRequestTO.setInstitutionUUID(session.getInstitution().getUUID());
                if(InstitutionType.DASHBOARD.equals(session.getInstitution().getInstitutionType())){
                    enrollmentRequestTO.setCourseVersionUUID(session.getCurrentCourseClass().getCourseVersionTO().getCourseVersion().getUUID());
                }
                enrollmentRequestTO.setCourseClassUUID(session.getCurrentCourseClass().getCourseClass().getUUID());

                enrollmentRequestTO.setUsername(username);
                batchEnrollments.add(enrollmentRequestTO);
            } else if (isUsernameValid(username) && (email == null || FormHelper.isEmailValid(email))) {
                batchEnrollments.add(createEnrollment(fullName, username, email, false));
            } else {
                batchEnrollmentErrors += enrollmentsA[i] + "\n";
            }
        }
    }

    private EnrollmentRequestTO createEnrollment(String fullName, String username, String email, boolean cancelEnrollment) {
    	fullName = fullName.trim();
    	username = username.trim();
        String usr;
        EnrollmentRequestTO enrollmentRequestTO = toFactory.newEnrollmentRequestTO().as();

        enrollmentRequestTO.setCancelEnrollment(cancelEnrollment);
        enrollmentRequestTO.setInstitutionUUID(session.getInstitution().getUUID());
        if(InstitutionType.DASHBOARD.equals(session.getInstitution().getInstitutionType())){
            enrollmentRequestTO.setCourseVersionUUID(session.getCurrentCourseClass().getCourseVersionTO().getCourseVersion().getUUID());
        }
        enrollmentRequestTO.setCourseClassUUID(session.getCurrentCourseClass().getCourseClass().getUUID());

        enrollmentRequestTO.setFullName(fullName);
        enrollmentRequestTO.setRegistrationType(session.getCurrentCourseClass().getCourseClass()
                .getRegistrationType());
        switch (session.getCurrentCourseClass().getCourseClass().getRegistrationType()) {
        case email:
            enrollmentRequestTO.setUsername(username);
            break;
        case cpf:
            usr = FormHelper.stripCPF(username);
            enrollmentRequestTO.setUsername(usr);
            enrollmentRequestTO.setPassword(usr);
            enrollmentRequestTO.setEmail(email);
            break;
        case username:
            usr = !cancelEnrollment && username.indexOf(FormHelper.USERNAME_SEPARATOR) == -1 ? session.getCurrentCourseClass()
                    .getRegistrationPrefix()
                    + FormHelper.USERNAME_SEPARATOR + username : username;
            enrollmentRequestTO.setUsername(usr);
            enrollmentRequestTO.setPassword(username);
            enrollmentRequestTO.setInstitutionRegistrationPrefixUUID(session.getCurrentCourseClass()
                    .getCourseClass().getInstitutionRegistrationPrefixUUID());
            break;
        default:
            break;
        }
        enrollmentRequestTO.setRegistrationType(session.getCurrentCourseClass().getCourseClass()
                .getRegistrationType());
        return enrollmentRequestTO;
    }

    private void prepareCreateEnrollments(boolean isBatch) {
        enrollmentRequestsTO.setEnrollmentRequests(batchEnrollments);
        if (CourseClassState.inactive.equals(session.getCurrentCourseClass().getCourseClass().getState())) {
            KornellNotification.show("Não é possível matricular participantes em uma turma desabilidada.",
                    AlertType.ERROR);
            return;
        } else if (enrollmentRequestsTO.getEnrollmentRequests().size() == 0) {
            KornellNotification
            .show("Verifique se os nomes/"
                    + EnumTranslator.translateEnum(
                            session.getCurrentCourseClass().getCourseClass().getRegistrationType())
                            .toLowerCase() + " dos participantes estão corretos. Nenhuma matrícula encontrada.",
                            AlertType.WARNING);
        } else {
            if (isBatch && session.getCurrentCourseClass().getCourseClass().isOverrideEnrollments()) {
                session.enrollments().simpleEnrollmentsList(session.getCurrentCourseClass().getCourseClass().getUUID(), new Callback<SimplePeopleTO>() {
                    @Override
                    public void ok(SimplePeopleTO to) {
                        String validation = validateEnrollmentsOverride(to.getSimplePeopleTO());
                        if (confirmedEnrollmentsModal || "".equals(validation)) {
                            createEnrollments();
                        } else {
                            overriddenEnrollmentsModalShown = true;
                            confirmedEnrollmentsModal = false;
                            view.setModalErrors("ATENÇÃO! Sobrescrita de matrículas!",
                                    "Os seguintes participantes terão suas matrículas canceladas:", validation,
                                    "Deseja continuar?");
                            view.showModal(true, "error");
                        }
                    }                    
                });
            } else {
                createEnrollments();
            }
        }

    }

    private String validateEnrollmentsOverride(List<SimplePersonTO> simplePeople) {
        String validation = "";

        Map<String, EnrollmentRequestTO> enrollmentRequestsMap = new HashMap<String, EnrollmentRequestTO>();
        for (EnrollmentRequestTO enrollmentRequestTO : enrollmentRequestsTO.getEnrollmentRequests()) {
            enrollmentRequestsMap.put(enrollmentRequestTO.getUsername(), enrollmentRequestTO);
        }

        enrollmentsToOverride = new ArrayList<SimplePersonTO>();
        for (SimplePersonTO simplePersonTO : simplePeople) {
            String username = simplePersonTO.getUsername();
            // if the user was already enrolled and is not on the new list,
            // cancel enrollment
            if (!enrollmentRequestsMap.containsKey(username)) {
                enrollmentsToOverride.add(simplePersonTO);
                validation += username + 
                        (StringUtils.isSome(simplePersonTO.getFullName()) ? 
                                " (" + simplePersonTO.getFullName() + ")\n" :
                                "");
            }
        }
        
        return validation;
    }

    private void createEnrollments() {

        if (confirmedEnrollmentsModal && enrollmentsToOverride != null && enrollmentsToOverride.size() > 0) {
            for (SimplePersonTO simplePersonTO : enrollmentsToOverride) {
                EnrollmentRequestTO enrollmentRequestTO = createEnrollment(simplePersonTO.getFullName(),
                        simplePersonTO.getUsername(), null, true);
                enrollmentRequestsTO.getEnrollmentRequests().add(enrollmentRequestTO);
            }

        }
        LoadingPopup.show();
        //send email for over 50 enrollments on DEFAULT institutions, but 3 on DASHBOARD institutions
        //@TODO refactor this after improvements on child enrollment generation with hundreds of scorm attributes
        final int requestsThreshold = InstitutionType.DASHBOARD.equals(session.getInstitution().getInstitutionType()) ? 3 : 50;
        if(enrollmentRequestsTO.getEnrollmentRequests().size() > requestsThreshold){
            if(StringUtils.isSome(session.getCurrentUser().getPerson().getEmail())){
                KornellNotification.show("Solicitação de matrículas enviada para o servidor. Você receberá uma email em \"" + session.getCurrentUser().getPerson().getEmail() + "\" assim que a operação for concluída.", AlertType.WARNING, 20000);
            } else {
                KornellNotification.show("Favor configurar um email no seu perfil para que possa receber as mensagens de confirmação de matrículas em lote.", AlertType.WARNING, 8000);
            }
            LoadingPopup.hide();
            confirmedEnrollmentsModal = false;
            view.clearEnrollmentFields();
        } else if (RegistrationType.email.equals(session.getCurrentCourseClass().getCourseClass().getRegistrationType())
                && enrollmentRequestsTO.getEnrollmentRequests().size() > 5) {
            KornellNotification
            .show("Solicitação de matrículas enviada para o servidor. Você receberá uma confirmação quando a operação for concluída (Tempo estimado: "
                    + enrollmentRequestsTO.getEnrollmentRequests().size() + " segundos).", AlertType.WARNING, 6000);
        }

        if(session.isCourseClassAdmin(session.getCurrentCourseClass().getCourseClass().getUUID())) {
            session.enrollments().createEnrollments(enrollmentRequestsTO, new Callback<Enrollments>() {
                @Override
                public void ok(Enrollments to) {
                    if(enrollmentRequestsTO.getEnrollmentRequests().size() <= requestsThreshold){
                        getEnrollments(session.getCurrentCourseClass().getCourseClass().getUUID());
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
                            + KornellConstantsHelper.getErrorMessage(kornellErrorTO));
                    KornellNotification.show("Erro ao criar matrícula(s).", AlertType.ERROR, 2500);
                    LoadingPopup.hide();
                }
                
                @Override
                protected void conflict(KornellErrorTO kornellErrorTO) {
                	KornellNotification
                    .show(KornellConstantsHelper.getErrorMessage(kornellErrorTO), AlertType.ERROR, 5000);
                }
            });
        }
    }

    @Override
    public void onModalOkButtonClicked() {
        view.showModal(false, "");
        if (overriddenEnrollmentsModalShown) {
            confirmedEnrollmentsModal = true;
        }
        prepareCreateEnrollments(true);
    }

    @Override
    public void onModalTransferOkButtonClicked(final String enrollmentUUID, final String courseClassUUID) {
    	session.enrollments().getEnrollmentsByCourseClass(courseClassUUID, new Callback<EnrollmentsTO>() {
    		@Override
    		public void ok(final EnrollmentsTO enrollmentsTO) {
    			session.courseClass(courseClassUUID).getTO(new Callback<CourseClassTO>() {
    				@Override
    				public void ok(CourseClassTO courseClassTO) {
    					if ((enrollmentsTO.getEnrollmentTOs().size() + 1) > courseClassTO.getCourseClass().getMaxEnrollments()) {
    	    	    		KornellNotification
    	    	            .show("Não foi possível concluir a requisição. Verifique a quantidade de matrículas disponíveis nesta turma",
    	    	                    AlertType.ERROR, 5000);
    	    	    	} else {
    	    	    		view.showModal(false, "");  
    	    	            session.events().enrollmentTransfered(enrollmentUUID, courseClassUUID, session.getCurrentCourseClass().getCourseClass().getUUID(), session.getCurrentUser().getPerson().getUUID())
    	    	            .fire(new Callback<Void>() {
    	    	                @Override
    	    	                public void ok(Void to) {
    	    	                    LoadingPopup.hide();
    	    	                    getEnrollments(session.getCurrentCourseClass()
    	    	                            .getCourseClass().getUUID());
    	    	                    view.setCanPerformEnrollmentAction(true);
    	    	                    KornellNotification.show("Usuário transferido com sucesso.", 2000);
    	    	                }
    	    	                @Override
    	    	                protected void conflict(KornellErrorTO kornellErrorTO) {
    	    	                	KornellNotification.show(KornellConstantsHelper.getErrorMessage(kornellErrorTO), AlertType.ERROR);
    	    	                }
    	    	            });
    	    	    	}
    				}
				});
    		}
		});
    }

    @Override
    public void onGoToCourseButtonClicked() {
        placeController.goTo(new ClassroomPlace(session.getCurrentCourseClass().getEnrollment().getUUID()));
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
		KornellNotification.show("Aguarde um instante...", AlertType.WARNING, 2000);
		session.report().locationAssign("/report/certificate",
				enrollmentTO.getPersonUUID(),
				enrollmentTO.getEnrollment().getCourseClassUUID());
    }

    @Override
    public List<EnrollmentTO> getEnrollments() {
        return enrollmentTOs;
    }

    @Override
    public void deleteEnrollment(EnrollmentTO enrollmentTO) {
        if(session.isCourseClassAdmin()){
        	session.events().enrollmentStateChanged(
        			enrollmentTO.getEnrollment().getUUID(), 
        			session.getCurrentUser().getPerson().getUUID(), 
        			enrollmentTO.getEnrollment().getState(), 
        			EnrollmentState.deleted).fire(new Callback<Void>(){
				@Override
				public void ok(Void to) {
					KornellNotification.show("Matrícula excluída com sucesso.", AlertType.SUCCESS, 2000);
                    getEnrollments(session.getCurrentCourseClass().getCourseClass().getUUID());
                    view.setCanPerformEnrollmentAction(true);
				}
				@Override
                public void internalServerError(KornellErrorTO kornellErrorTO){
                    KornellNotification.show("Erro ao excluir matrícula. Usuário provavelmente já acessou a plataforma.", AlertType.ERROR, 2500);
                    view.setCanPerformEnrollmentAction(true);
                }
        	});
        }
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
                    CourseClassTO courseClassTO2 = session.getCurrentCourseClass();
                    if (courseClassTO2 != null)
                        courseClassTO2.setCourseClass(courseClass);
					placeController.goTo(new AdminCourseClassPlace(courseClass.getUUID()));
                }

                @Override
                public void conflict(KornellErrorTO kornellErrorTO) {
                    LoadingPopup.hide();
					KornellNotification.show(KornellConstantsHelper.getErrorMessage(kornellErrorTO),
                            AlertType.ERROR, 2500);
                }
            });
        } else {
            session.courseClass(courseClass.getUUID()).update(courseClass, new Callback<CourseClass>() {
                @Override
                public void ok(CourseClass courseClass) {
                    LoadingPopup.hide();
					KornellNotification.show("Alterações salvas com sucesso!");
                    session.getCurrentCourseClass().setCourseClass(courseClass);
                    updateCourseClass(courseClass.getUUID());
                }

                @Override
                public void conflict(KornellErrorTO kornellErrorTO) {
                    LoadingPopup.hide();
					KornellNotification.show(KornellConstantsHelper.getErrorMessage(kornellErrorTO),
                            AlertType.ERROR, 2500);
                }
            });
        }
    }

    @Override
    public String getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String getPageNumber() {
        return pageNumber;
    }

    @Override
    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public String getSearchTerm() {
        return searchTerm;
    }

    @Override
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;	
    }

    @Override
    public void updateData() {
        updateCourseClassUI(session.getCurrentCourseClass());
    }
}
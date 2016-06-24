package kornell.gui.client.presentation.admin.courseclass.courseclass.generic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Course;
import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.CourseVersion;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.InstitutionRegistrationPrefix;
import kornell.core.entity.RegistrationType;
import kornell.core.entity.RoleCategory;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseVersionsTO;
import kornell.core.to.CoursesTO;
import kornell.core.to.InstitutionRegistrationPrefixesTO;
import kornell.core.to.RolesTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.mvp.PlaceUtils;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassView.Presenter;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.forms.formfield.ListBoxFormField;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.LoadingPopup;

public class GenericCourseClassConfigView extends Composite {
    interface MyUiBinder extends UiBinder<Widget, GenericCourseClassConfigView> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    public static final EntityFactory entityFactory = GWT.create(EntityFactory.class);
    private static final String MODAL_DELETE = "delete";
    private static final String MODAL_DEACTIVATE = "deactivate";
    private static final String MODAL_PUBLIC = "public";
    private static final String MODAL_APPROVE_ENROLLMENTS_AUTOMATICALLY = "approveEnrollmentsAutomatically";
    private static final String MODAL_OVERRIDE_ENROLLMENTS = "overrideEnrollments";
    private static final String MODAL_INVISIBLE = "invisible";
    private static final String MODAL_ALLOW_BATCH_CANCELLATION = "allowBatchCancellation";
    private static final String MODAL_COURSE_CLASS_CHAT_ENABLED = "courseClassChatEnabled";
    private static final String MODAL_CHAT_DOCK_ENABLED = "chatDockEnabled";
    private static final String MODAL_TUTOR_CHAT_ENABLED = "tutorChatEnabled";

    private KornellSession session;
    private FormHelper formHelper = GWT.create(FormHelper.class);
    private boolean isCreationMode, canDelete, isInstitutionAdmin, allowPrefixEdit;
    boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;

    private Presenter presenter;

    @UiField
    HTMLPanel titleEdit;
    @UiField
    HTMLPanel titleCreate;
    @UiField
    Form form;
    @UiField
    FlowPanel profileFields;
    @UiField
    Button btnOK;
    @UiField
    Button btnCancel;
    @UiField
    Button btnDelete;

    @UiField
    Modal confirmModal;
    @UiField
    Label confirmText;
    @UiField
    Button btnModalOK;
    @UiField
    Button btnModalCancel;

    private CourseClassTO courseClassTO;
    private CourseClass courseClass;
    private KornellFormFieldWrapper course, courseVersion, name, publicClass, approveEnrollmentsAutomatically,
    requiredScore, registrationType, institutionRegistrationPrefix, maxEnrollments, overrideEnrollments, invisible, allowBatchCancellation, courseClassChatEnabled, chatDockEnabled, tutorChatEnabled;
    private List<KornellFormFieldWrapper> fields;
    private String modalMode;
    private ListBox institutionRegistrationPrefixes;
    private EventBus bus;
    private PlaceController placeCtrl;

    public GenericCourseClassConfigView(final KornellSession session, EventBus bus, PlaceController placeCtrl,
            Presenter presenter, CourseClassTO courseClassTO) {
        this.session = session;
        this.bus = bus;
        this.placeCtrl = placeCtrl;
        this.presenter = presenter;
        this.isInstitutionAdmin = session.isInstitutionAdmin();
        this.isCreationMode = (courseClassTO == null) && isInstitutionAdmin;
        this.allowPrefixEdit = session.getInstitution().isAllowRegistrationByUsername() && (isCreationMode || (presenter.getEnrollments().size() == 0) || StringUtils.isNone(courseClassTO.getCourseClass().getInstitutionRegistrationPrefixUUID()));
        this.canDelete = presenter.getEnrollments() == null || presenter.getEnrollments().size() == 0;
        initWidget(uiBinder.createAndBindUi(this));

        // i18n
        btnOK.setText("OK".toUpperCase());
        btnCancel.setText(isCreationMode ? "Cancelar".toUpperCase() : "Limpar".toUpperCase());
        btnDelete.setVisible(isInstitutionAdmin && !isCreationMode && CourseClassState.active.equals(session.getCurrentCourseClass().getCourseClass().getState()));
        btnDelete.setText(canDelete?"Excluir".toUpperCase():"Desabilitar".toUpperCase());

        btnModalOK.setText("OK".toUpperCase());
        btnModalCancel.setText("Cancelar".toUpperCase());

        this.courseClassTO = courseClassTO;

        initData();
    }

    public void initData() {
        profileFields.setVisible(false);
        this.fields = new ArrayList<KornellFormFieldWrapper>();
        courseClass = isCreationMode ? entityFactory.newCourseClass().as() : courseClassTO.getCourseClass();

        profileFields.clear();

        this.titleEdit.setVisible(!isCreationMode);
        this.titleCreate.setVisible(isCreationMode);

        btnOK.setVisible(isInstitutionAdmin|| isCreationMode);
        btnCancel.setVisible(isInstitutionAdmin);


        if (isCreationMode) {
            session.courses().get(false, new Callback<CoursesTO>() {
                @Override
                public void ok(CoursesTO to) {
                    createCoursesField(to);
                }
            });
        } else {
            createCoursesField(null);
        }

        name = new KornellFormFieldWrapper("Nome da Turma", formHelper.createTextBoxFormField(courseClass.getName()), isInstitutionAdmin);
        fields.add(name);
        profileFields.add(name);

        String requiredScoreStr = courseClass.getRequiredScore() == null ? "" : courseClass.getRequiredScore().toString();
        requiredScore = new KornellFormFieldWrapper("Nota para Aprovação", formHelper.createTextBoxFormField(requiredScoreStr), isInstitutionAdmin, null, "Se a nota for deixada em branco ou for zero, a avaliação não será exigida para que os alunos matriculados finalizem o curso.");
        fields.add(requiredScore);
        profileFields.add(requiredScore);

        String maxEnrollmentsStr = courseClass.getMaxEnrollments() == null ? "" : courseClass.getMaxEnrollments().toString();
        maxEnrollments = new KornellFormFieldWrapper("Quantidade de Matrículas", formHelper.createTextBoxFormField(maxEnrollmentsStr), isInstitutionAdmin);
        fields.add(maxEnrollments);
        profileFields.add(maxEnrollments);

        Boolean isPublicClass = courseClass.isPublicClass() == null ? false : courseClass.isPublicClass();
        publicClass = new KornellFormFieldWrapper("Turma pública?", formHelper.createCheckBoxFormField(isPublicClass), isInstitutionAdmin);
        fields.add(publicClass);
        profileFields.add(publicClass);
        ((CheckBox)publicClass.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if(event.getValue()){
                    showModal(MODAL_PUBLIC);
                    ((CheckBox)publicClass.getFieldWidget()).setValue(false);
                } else {
                    ((CheckBox)approveEnrollmentsAutomatically.getFieldWidget()).setValue(false);
                    ((CheckBox)approveEnrollmentsAutomatically.getFieldWidget()).setEnabled(false);
                }
            }
        });

        Boolean isApproveEnrollmentsAutomatically = courseClass.isApproveEnrollmentsAutomatically() == null ? false : courseClass.isApproveEnrollmentsAutomatically();
        approveEnrollmentsAutomatically = new KornellFormFieldWrapper("Auto-aprovar matrículas?", formHelper.createCheckBoxFormField(isApproveEnrollmentsAutomatically), isInstitutionAdmin);
        fields.add(approveEnrollmentsAutomatically);
        profileFields.add(approveEnrollmentsAutomatically);
        ((CheckBox)approveEnrollmentsAutomatically.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if(event.getValue()){
                    showModal(MODAL_APPROVE_ENROLLMENTS_AUTOMATICALLY);
                    ((CheckBox)approveEnrollmentsAutomatically.getFieldWidget()).setValue(false);
                }
            }
        });
        ((CheckBox)approveEnrollmentsAutomatically.getFieldWidget()).setEnabled(isPublicClass);

        Boolean isInvisible = courseClass.isInvisible() == null ? false : courseClass.isInvisible();
        invisible = new KornellFormFieldWrapper("Turma invisível?", formHelper.createCheckBoxFormField(isInvisible), isInstitutionAdmin);
        fields.add(invisible);
        profileFields.add(invisible);
        ((CheckBox)invisible.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if(event.getValue()){
                    showModal(MODAL_INVISIBLE);
                    ((CheckBox)invisible.getFieldWidget()).setValue(false);
                }
            }
        });



        Boolean isOverrideEnrollments = courseClass.isOverrideEnrollments() == null ? false : courseClass.isOverrideEnrollments();
        overrideEnrollments = new KornellFormFieldWrapper("Sobrescrever matrículas em lote?", formHelper.createCheckBoxFormField(isOverrideEnrollments), isInstitutionAdmin);
        fields.add(overrideEnrollments);
        profileFields.add(overrideEnrollments);
        ((CheckBox)overrideEnrollments.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if(event.getValue()){
                    showModal(MODAL_OVERRIDE_ENROLLMENTS);
                    ((CheckBox)overrideEnrollments.getFieldWidget()).setValue(false);
                }
            }
        });

        
        Boolean isAllowBatchCancellation = courseClass.isAllowBatchCancellation() == null ? false : courseClass.isAllowBatchCancellation();
        allowBatchCancellation = new KornellFormFieldWrapper("Permitir cancelamento em lote?", formHelper.createCheckBoxFormField(isAllowBatchCancellation), isInstitutionAdmin);
        fields.add(allowBatchCancellation);
        profileFields.add(allowBatchCancellation);
        ((CheckBox)allowBatchCancellation.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if(event.getValue()){
                    showModal(MODAL_ALLOW_BATCH_CANCELLATION);
                    ((CheckBox)allowBatchCancellation.getFieldWidget()).setValue(false);
                }
            }
        });

        if(session.isPlatformAdmin()){   
	        Boolean isTutorChatEnabled = courseClass.isTutorChatEnabled() == null ? false : courseClass.isTutorChatEnabled();
	        tutorChatEnabled = new KornellFormFieldWrapper("Permitir tutoria da turma?", formHelper.createCheckBoxFormField(isTutorChatEnabled), isInstitutionAdmin);
	        fields.add(tutorChatEnabled);
	        profileFields.add(tutorChatEnabled);
	        ((CheckBox)tutorChatEnabled.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
	            @Override
	            public void onValueChange(ValueChangeEvent<Boolean> event) {
	                if(event.getValue()){
	                    showModal(MODAL_TUTOR_CHAT_ENABLED);
	                    ((CheckBox)tutorChatEnabled.getFieldWidget()).setValue(false);
	                }
	            }
	        });
	        
	        Boolean isCourseClassChatEnabled = courseClass.isCourseClassChatEnabled() == null ? false : courseClass.isCourseClassChatEnabled();
	        courseClassChatEnabled = new KornellFormFieldWrapper("Chat global da turma?", formHelper.createCheckBoxFormField(isCourseClassChatEnabled), isInstitutionAdmin);
	        fields.add(courseClassChatEnabled);
	        profileFields.add(courseClassChatEnabled);
	        ((CheckBox)courseClassChatEnabled.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
	            @Override
	            public void onValueChange(ValueChangeEvent<Boolean> event) {
	                if(event.getValue()){
	                    showModal(MODAL_COURSE_CLASS_CHAT_ENABLED);
	                    ((CheckBox)courseClassChatEnabled.getFieldWidget()).setValue(false);
	                } else {
	                    ((CheckBox)chatDockEnabled.getFieldWidget()).setValue(false);
	                    ((CheckBox)chatDockEnabled.getFieldWidget()).setEnabled(false);
	                }
	            }
	        });
	        
	        Boolean isChatDockEnabled = courseClass.isChatDockEnabled() == null ? false : courseClass.isChatDockEnabled();
	        chatDockEnabled = new KornellFormFieldWrapper("Fixar chat da turma?", formHelper.createCheckBoxFormField(isChatDockEnabled), isInstitutionAdmin);
	        fields.add(chatDockEnabled);
	        profileFields.add(chatDockEnabled);
	        ((CheckBox)chatDockEnabled.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
	            @Override
	            public void onValueChange(ValueChangeEvent<Boolean> event) {
	                if(event.getValue()){
	                    showModal(MODAL_CHAT_DOCK_ENABLED);
	                    ((CheckBox)chatDockEnabled.getFieldWidget()).setValue(false);
	                }
	            }
	        });
            ((CheckBox)chatDockEnabled.getFieldWidget()).setEnabled(isCourseClassChatEnabled);
        }
        
        final ListBox registrationTypes = new ListBox();
        registrationTypes.addItem("Email", RegistrationType.email.toString());
        registrationTypes.addItem("CPF", RegistrationType.cpf.toString());
        if(session.getInstitution().isAllowRegistrationByUsername())
            registrationTypes.addItem("Usuário", RegistrationType.username.toString());
        if (!isCreationMode) {
            registrationTypes.setSelectedValue(courseClassTO.getCourseClass().getRegistrationType().toString());
        }
        registrationType = new KornellFormFieldWrapper("Tipo de Matrícula", new ListBoxFormField(registrationTypes), isInstitutionAdmin);
        fields.add(registrationType);
        profileFields.add(registrationType);

        if(session.getInstitution().isAllowRegistrationByUsername()){
            institutionRegistrationPrefixes = new ListBox();		
            if(!isCreationMode)
                institutionRegistrationPrefixes.setSelectedValue(courseClassTO.getCourseClass().getInstitutionRegistrationPrefixUUID());
            if(allowPrefixEdit){
                loadInstitutionPrefixes();
            } else if (!isCreationMode) {
                institutionRegistrationPrefixes.addItem(courseClassTO.getRegistrationPrefix());
            }
            institutionRegistrationPrefix = new KornellFormFieldWrapper("Prefixo", new ListBoxFormField(institutionRegistrationPrefixes), allowPrefixEdit);
            fields.add(institutionRegistrationPrefix);
            profileFields.add(institutionRegistrationPrefix);
            institutionRegistrationPrefix.setVisible(registrationType.getFieldPersistText().equals(RegistrationType.username.toString()));
            registrationTypes.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    institutionRegistrationPrefix.setVisible(registrationType.getFieldPersistText().equals(RegistrationType.username.toString()));
                }
            });
        }

        profileFields.add(formHelper.getImageSeparator());
    }

    private void loadInstitutionPrefixes() {
        session.institution(session.getInstitution().getUUID()).getRegistrationPrefixes(new Callback<InstitutionRegistrationPrefixesTO>() {
            @Override
            public void ok(InstitutionRegistrationPrefixesTO to) {
                for (InstitutionRegistrationPrefix institutionRegistrationPrefix : to.getInstitutionRegistrationPrefixes()) {
                    institutionRegistrationPrefixes.addItem(institutionRegistrationPrefix.getName(), institutionRegistrationPrefix.getUUID());
                }
            }
        });
    }

    private void createCoursesField(CoursesTO to) {
        final ListBox courses = new ListBox();
        if(to != null){
            for (Course course : to.getCourses()) {
                courses.addItem(course.getTitle(), course.getUUID());
            }
        } else {
            courses.addItem(courseClassTO.getCourseVersionTO().getCourse().getTitle(), courseClassTO.getCourseVersionTO().getCourse().getUUID());
        }
        courses.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                loadCourseVersions();
            }
        });
        if (!isCreationMode) {
            courses.setSelectedValue(courseClassTO.getCourseVersionTO().getCourse().getUUID());
        }
        course = new KornellFormFieldWrapper("Curso", new ListBoxFormField(courses), (isCreationMode || presenter.getEnrollments().size() == 0));


        fields.add(course);
        courses.setSelectedIndex(0);
        profileFields.insert(course, 0);
        loadCourseVersions();
    }

    private void loadCourseVersions() {
        if (isCreationMode && isInstitutionAdmin) {
            session.courseVersions().findByCourse(course.getFieldPersistText(), new Callback<CourseVersionsTO>() {
                @Override
                public void ok(CourseVersionsTO to) {
                    createCourseVersionsField(to);
                }
            });
        } else {
            createCourseVersionsField(null);
        }
    }

    private void createCourseVersionsField(CourseVersionsTO to) {
        final ListBox courseVersions = new ListBox();
        if(to != null){
            for (CourseVersion courseVersion : to.getCourseVersions()) {
                courseVersions.addItem(courseVersion.getName(), courseVersion.getUUID());
            }
        } else {
            courseVersions.addItem(courseClassTO.getCourseVersionTO().getCourseVersion().getName(), courseClassTO.getCourseVersionTO().getCourseVersion().getUUID());
        }
        if(courseVersion != null && profileFields.getElement().isOrHasChild(courseVersion.getElement())){
            fields.remove(courseVersion);
            profileFields.getElement().removeChild(courseVersion.getElement());
        }
        if (!isCreationMode) {
            courseVersions.setSelectedValue(courseClassTO.getCourseVersionTO().getCourseVersion().getUUID());
        }
        courseVersion = new KornellFormFieldWrapper("Versão do Curso", new ListBoxFormField(courseVersions), (isCreationMode || presenter.getEnrollments().size() == 0));

        fields.add(courseVersion);
        profileFields.insert(courseVersion, 1);


        profileFields.setVisible(true);
    }

    private boolean validateFields() {
        if (!formHelper.isListBoxSelected(((ListBox) course.getFieldWidget()))) {
            course.setError("Selecione o curso.");
        }

        if (!formHelper.isListBoxSelected(((ListBox) courseVersion.getFieldWidget()))) {
            courseVersion.setError("Selecione a versão do curso.");
        }

        if (!formHelper.isLengthValid(name.getFieldPersistText(), 2, 100)) {
            name.setError("Insira o nome da turma.");
        }

        if (requiredScore.getFieldPersistText().length() > 0 && !formHelper.isValidNumber(requiredScore.getFieldPersistText())) {
            requiredScore.setError("Número inválido.");
        }

        if (!formHelper.isLengthValid(maxEnrollments.getFieldPersistText(), 1, 10)) {
            maxEnrollments.setError("Insira a quantidade máxima de matrículas.");
        } else if (!formHelper.isValidNumber(maxEnrollments.getFieldPersistText())) {
            maxEnrollments.setError("Número inteiro inválido.");
        } else if (!isCreationMode && Integer.parseInt(maxEnrollments.getFieldPersistText()) < presenter.getEnrollments().size()){
            maxEnrollments.setError("Menor que o número atual de matrículas.");
        }

        return !formHelper.checkErrors(fields);
    }

    @UiHandler("btnOK")
    void doOK(ClickEvent e) {
        formHelper.clearErrors(fields);
        if (isInstitutionAdmin && validateFields()) {
            LoadingPopup.show();
            CourseClass courseClass = getCourseClassInfoFromForm();
            presenter.upsertCourseClass(courseClass);
        }
    }

    private CourseClass getCourseClassInfoFromForm() {
        courseClass.setInstitutionUUID(session.getInstitution().getUUID());
        courseClass.setName(name.getFieldPersistText());
        courseClass.setCourseVersionUUID(courseVersion.getFieldPersistText());
        courseClass.setPublicClass(publicClass.getFieldPersistText().equals("true"));
        courseClass.setApproveEnrollmentsAutomatically(approveEnrollmentsAutomatically.getFieldPersistText().equals("true"));
        courseClass.setMaxEnrollments(new Integer(maxEnrollments.getFieldPersistText()));
        courseClass.setRequiredScore(requiredScore.getFieldPersistText().length() > 0 ?
                new BigDecimal(requiredScore.getFieldPersistText()) :
                    null);
        courseClass.setOverrideEnrollments(overrideEnrollments.getFieldPersistText().equals("true"));
        courseClass.setInvisible(invisible.getFieldPersistText().equals("true"));
        courseClass.setAllowBatchCancellation(allowBatchCancellation != null ? allowBatchCancellation.getFieldPersistText().equals("true") : false);
        courseClass.setCourseClassChatEnabled(courseClassChatEnabled != null ? courseClassChatEnabled.getFieldPersistText().equals("true") : false);
        courseClass.setChatDockEnabled(chatDockEnabled != null ? chatDockEnabled.getFieldPersistText().equals("true") : false);
        courseClass.setTutorChatEnabled(tutorChatEnabled != null ? tutorChatEnabled.getFieldPersistText().equals("true") : false);
        courseClass.setRegistrationType(RegistrationType.valueOf(registrationType.getFieldPersistText()));
        if(allowPrefixEdit) {
            courseClass.setInstitutionRegistrationPrefixUUID(institutionRegistrationPrefix.getFieldPersistText());
        }
        return courseClass;
    }

    @UiHandler("btnCancel")
    void doCancel(ClickEvent e) {
        if(isCreationMode){
            PlaceUtils.reloadCurrentPlace(bus, placeCtrl);
        } else {
            initData();
        }
    }

    @UiHandler("btnDelete")
    void doDelete(ClickEvent e) {
    	if(!isInstitutionAdmin)
    		return;
        if(!isCreationMode && canDelete){
            showModal(MODAL_DELETE);
        } else {
            showModal(MODAL_DEACTIVATE);
        }
    }

    private void showModal(String mode) {
        this.modalMode = mode;
        if(MODAL_DELETE.equals(modalMode)){
            confirmText.setText("Tem certeza que deseja excluir esta turma?"
                    + "\nEsta operação não pode ser desfeita.");
        } else if (MODAL_DEACTIVATE.equals(modalMode)){
            confirmText.setText("Tem certeza que deseja desabilitar esta turma? Os participantes matriculados ainda poderão acessar os detalhes da turma e emitir o certificado, mas não terão acesso ao material relacionado à turma."
                    + "\nEsta operação não pode ser desfeita. Caso deseje evitar que essa turma apareça para os participantes, coloque-a como invisível.");
        } else if (MODAL_PUBLIC.equals(modalMode)){
            confirmText.setText("ATENÇÃO! Tem certeza que deseja tornar esta turma pública? Ela será visível e disponível para solicitação de matrícula para TODOS os alunos registrados nesta instituição.");
        } else if (MODAL_APPROVE_ENROLLMENTS_AUTOMATICALLY.equals(modalMode)){
            confirmText.setText("ATENÇÃO! Tem certeza que deseja aprovar as matrículas automaticamente? Toda vez que um participante solicitar uma matrícula, ele poderá iniciar o curso instantaneamente.");
        } else if (MODAL_OVERRIDE_ENROLLMENTS.equals(modalMode)){
            confirmText.setText("ATENÇÃO! Tem certeza que deseja habilitar a sobrescrita de matrículas? Toda vez que uma matrícula em lote for feita, todas as matrículas já existentes que não estão presentes no lote serão canceladas.");
        } else if (MODAL_INVISIBLE.equals(modalMode)){
            confirmText.setText("ATENÇÃO! Tem certeza que deseja tornar esta turma invisível? Nenhum participante que esteja matriculado poderá ver essa turma, nem será capaz de gerar o certificado caso tenha sido aprovado.");
        } else if (MODAL_ALLOW_BATCH_CANCELLATION.equals(modalMode)){
            confirmText.setText("ATENÇÃO! Tem certeza que deseja permitir o cancelamento em lote de matrículas? Um novo botão aparecerá na tela de matrículas, e qualquer administrador poderá cancelar uma lista de matrículas.");
        } else if (MODAL_COURSE_CLASS_CHAT_ENABLED.equals(modalMode)){
            confirmText.setText("ATENÇÃO! Tem certeza que deseja permitir a criação do chat global da turma? Um novo botão aparecerá na tela de detalhes do curso para os participantes, e todos os participantes da turma poderão conversar entre si.");
        } else if (MODAL_CHAT_DOCK_ENABLED.equals(modalMode)){
            confirmText.setText("ATENÇÃO! Tem certeza que deseja habilitar a fixação do chat? Um painel aparecerá dentro da turma, ao lado direito, com o chat da turma carregado.");
        } else if (MODAL_TUTOR_CHAT_ENABLED.equals(modalMode)){
            confirmText.setText("ATENÇÃO! Tem certeza que deseja permitir a criação de chats de tutoria na turma? Um novo botão aparecerá na tela de detalhes do curso para os participantes, e todos os participantes da turma poderão iniciar uma conversa com um tutor. Não se esqueça de configurar o(s) tutor(es)!");
        }
        confirmModal.show();
    }

    @UiHandler("btnModalOK")
    void onModalOkButtonClicked(ClickEvent e) {
        if(MODAL_DELETE.equals(modalMode)){
            presenter.changeCourseClassState(courseClassTO, CourseClassState.deleted);
        } else if (MODAL_DEACTIVATE.equals(modalMode)){
            presenter.changeCourseClassState(courseClassTO, CourseClassState.inactive);
        } else if (MODAL_PUBLIC.equals(modalMode)){
            ((CheckBox)publicClass.getFieldWidget()).setValue(true);
            ((CheckBox)invisible.getFieldWidget()).setValue(false);
            ((CheckBox)approveEnrollmentsAutomatically.getFieldWidget()).setEnabled(true);
        } else if (MODAL_APPROVE_ENROLLMENTS_AUTOMATICALLY.equals(modalMode)){
            ((CheckBox)approveEnrollmentsAutomatically.getFieldWidget()).setValue(true);
        } else if (MODAL_OVERRIDE_ENROLLMENTS.equals(modalMode)){
            ((CheckBox)overrideEnrollments.getFieldWidget()).setValue(true);
        } else if (MODAL_INVISIBLE.equals(modalMode)){
            ((CheckBox)invisible.getFieldWidget()).setValue(true);
            ((CheckBox)publicClass.getFieldWidget()).setValue(false);
            ((CheckBox)approveEnrollmentsAutomatically.getFieldWidget()).setValue(false);
            ((CheckBox)approveEnrollmentsAutomatically.getFieldWidget()).setEnabled(false);
        } else if (MODAL_ALLOW_BATCH_CANCELLATION.equals(modalMode)){
            ((CheckBox)allowBatchCancellation.getFieldWidget()).setValue(true);
        } else if (MODAL_COURSE_CLASS_CHAT_ENABLED.equals(modalMode)){
            ((CheckBox)courseClassChatEnabled.getFieldWidget()).setValue(true);
            ((CheckBox)chatDockEnabled.getFieldWidget()).setEnabled(true);
        } else if (MODAL_CHAT_DOCK_ENABLED.equals(modalMode)){
            ((CheckBox)chatDockEnabled.getFieldWidget()).setValue(true);
        } else if (MODAL_TUTOR_CHAT_ENABLED.equals(modalMode)){
        	if(courseClassTO != null && courseClassTO.getCourseClass() != null && courseClassTO.getCourseClass().getUUID() != null){
                LoadingPopup.show();
                session.courseClass(courseClassTO.getCourseClass().getUUID()).getTutors(RoleCategory.BIND_WITH_PERSON,
                        new Callback<RolesTO>() {
                    @Override
                    public void ok(RolesTO to) {
                        if(to.getRoleTOs().size() == 0){
                        	KornellNotification.show("Você precisa configurar os tutores na aba \"Administradores\" antes de habilitar esta opção.", AlertType.WARNING, 4000);
                        } else {
                            ((CheckBox)tutorChatEnabled.getFieldWidget()).setValue(true);
                        }
                        LoadingPopup.hide();
                    }
                });
        	} else {
            	KornellNotification.show("Para habilitar esta opção, você precisa configurar os tutores na aba \"Administradores\" após finalizar a criação da turma.", AlertType.WARNING, 4000);
        	}
        }
        confirmModal.hide();
    }

    @UiHandler("btnModalCancel")
    void onModalCancelButtonClicked(ClickEvent e) {
        this.modalMode = null;
        confirmModal.hide();
    }

}
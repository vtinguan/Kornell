package kornell.gui.client.presentation.admin.home.generic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Course;
import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.CourseVersion;
import kornell.core.entity.EntityFactory;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseVersionsTO;
import kornell.core.to.CoursesTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.home.AdminHomeView.Presenter;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;
import kornell.gui.client.util.view.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.view.formfield.ListBoxFormField;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class GenericCourseClassConfigView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseClassConfigView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	public static final EntityFactory entityFactory = GWT.create(EntityFactory.class);
	private static final String MODAL_DELETE = "delete";
	private static final String MODAL_DEACTIVATE = "deactivate";
	private static final String MODAL_OVERRIDE_ENROLLMENTS = "overrideEnrollments";

	private KornellSession session;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private FormHelper formHelper = GWT.create(FormHelper.class);
	private boolean isCreationMode, canDelete, isInstitutionAdmin;
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

	private UserInfoTO user;
	private CourseClassTO courseClassTO;
	private CourseClass courseClass;
	private KornellFormFieldWrapper course, courseVersion, name, publicClass,
			requiredScore, enrollWithCPF, maxEnrollments, overrideEnrollments;
	private FileUpload fileUpload;
	private List<KornellFormFieldWrapper> fields;
	private String modalMode;
	
	public GenericCourseClassConfigView(final KornellSession session,
			Presenter presenter, CourseClassTO courseClassTO) {
		this.session = session;
		this.presenter = presenter;
		this.user = session.getCurrentUser();
		this.isCreationMode = (courseClassTO == null);
		this.canDelete = presenter.getEnrollments() == null || presenter.getEnrollments().size() == 0;
		initWidget(uiBinder.createAndBindUi(this));

		// i18n
		btnOK.setText("OK".toUpperCase());
		btnCancel.setText(isCreationMode ? "Cancelar".toUpperCase() : "Limpar".toUpperCase());
		btnDelete.setVisible(!isCreationMode && CourseClassState.active.equals(Dean.getInstance()
				.getCourseClassTO().getCourseClass().getState()));
		btnDelete.setText(canDelete?"Excluir".toUpperCase():"Desabilitar".toUpperCase());
		

		btnModalOK.setText("OK".toUpperCase());
		btnModalCancel.setText("Cancelar".toUpperCase());

		this.titleEdit.setVisible(!isCreationMode);
		this.titleCreate.setVisible(isCreationMode);
		
		this.courseClassTO = courseClassTO;
		
		initData();
	}

	public void initData() {
		profileFields.setVisible(false);
		this.fields = new ArrayList<KornellFormFieldWrapper>();
		courseClass = isCreationMode ? entityFactory.newCourseClass().as() : courseClassTO.getCourseClass();
		isInstitutionAdmin = session.isInstitutionAdmin();

		profileFields.clear();
		
		btnOK.setVisible(isInstitutionAdmin|| isCreationMode);
		btnCancel.setVisible(isInstitutionAdmin);
		

		if (isCreationMode) {
			session.courses().findByInstitution(Dean.getInstance().getInstitution().getUUID(),
				new Callback<CoursesTO>() {
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
		requiredScore = new KornellFormFieldWrapper("Nota para Aprovação", formHelper.createTextBoxFormField(requiredScoreStr), isInstitutionAdmin);
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

		Boolean isEnrollWithCPF = courseClass.isEnrollWithCPF() == null ? false : courseClass.isEnrollWithCPF();
		enrollWithCPF = new KornellFormFieldWrapper("Matricular com CPF?", formHelper.createCheckBoxFormField(isEnrollWithCPF), isInstitutionAdmin);
		fields.add(enrollWithCPF);
		profileFields.add(enrollWithCPF);

		Boolean isOverrideEnrollments = courseClass.isOverrideEnrollments() == null ? false : courseClass.isOverrideEnrollments();
		overrideEnrollments = new KornellFormFieldWrapper("Sobrescrever matrículas em lote?", formHelper.createCheckBoxFormField(isOverrideEnrollments), isInstitutionAdmin);
		fields.add(overrideEnrollments);
		profileFields.add(overrideEnrollments);
		((CheckBox)overrideEnrollments.getFieldWidget()).addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if(!((CheckBox)overrideEnrollments.getFieldWidget()).getValue()){
					((CheckBox)overrideEnrollments.getFieldWidget()).setValue(false);
					showModal(MODAL_OVERRIDE_ENROLLMENTS);
				}
			}
		});
		
		profileFields.add(formHelper.getImageSeparator());

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
					GWT.log("fsdafasfas " + to.getCourseVersions().get(0).isDisabled());
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
		
		if (!formHelper.isLengthValid(maxEnrollments.getFieldPersistText(), 1, 20)) {
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
		courseClass.setInstitutionUUID(Dean.getInstance().getInstitution().getUUID());
		courseClass.setName(name.getFieldPersistText());
		courseClass.setCourseVersionUUID(courseVersion.getFieldPersistText());
		courseClass.setEnrollWithCPF(enrollWithCPF.getFieldPersistText().equals("true"));
		courseClass.setPublicClass(publicClass.getFieldPersistText().equals("true"));
		courseClass.setMaxEnrollments(new Integer(maxEnrollments.getFieldPersistText()));
		courseClass.setRequiredScore(requiredScore.getFieldPersistText().length() > 0 ?
				new BigDecimal(requiredScore.getFieldPersistText()) :
					null);
		courseClass.setOverrideEnrollments(overrideEnrollments.getFieldPersistText().equals("true"));
		return courseClass;
	}

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) {
		if(isCreationMode){
			presenter.updateCourseClass(null);
		} else {
			initData();
		}
	}

	@UiHandler("btnDelete")
	void doDelete(ClickEvent e) {
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
			confirmText.setText("Tem certeza que deseja desabilitar esta turma?"
					+ "\nEsta operação não pode ser desfeita.");
		} else if (MODAL_OVERRIDE_ENROLLMENTS.equals(modalMode)){
			confirmText.setText("ATENÇÃO! Tem certeza que deseja habilitar a sobrescrita de matrículas? Toda vez que uma matrícula em lote for feita, todas as matrículas já existentes que não estão presentes no lote serão canceladas.");
		}
		confirmModal.show();
	  // TODO Auto-generated method stub
	  
  }

	@UiHandler("btnModalOK")
	void onModalOkButtonClicked(ClickEvent e) {
		if(MODAL_DELETE.equals(modalMode)){
			presenter.changeCourseClassState(courseClassTO, CourseClassState.deleted);
		} else if (MODAL_DEACTIVATE.equals(modalMode)){
			presenter.changeCourseClassState(courseClassTO, CourseClassState.inactive);
		} else if (MODAL_OVERRIDE_ENROLLMENTS.equals(modalMode)){
			((CheckBox)overrideEnrollments.getFieldWidget()).setValue(true);
		}
		confirmModal.hide();
	}

	@UiHandler("btnModalCancel")
	void onModalCancelButtonClicked(ClickEvent e) {
		confirmModal.hide();
	}

}
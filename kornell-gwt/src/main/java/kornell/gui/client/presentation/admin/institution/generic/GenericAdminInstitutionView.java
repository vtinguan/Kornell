package kornell.gui.client.presentation.admin.institution.generic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClass;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.Institution;
import kornell.core.entity.RegistrationEnrollmentType;
import kornell.core.to.CourseClassTO;
import kornell.core.to.InstitutionRegistrationPrefixesTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionView;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.LoadingPopup;
import kornell.gui.client.util.view.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.view.formfield.ListBoxFormField;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
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
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericAdminInstitutionView extends Composite implements AdminInstitutionView {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminInstitutionView> {
	}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	public static final EntityFactory entityFactory = GWT.create(EntityFactory.class);
	private static final String MODAL_DELETE = "delete";
	private static final String MODAL_DEACTIVATE = "deactivate";
	private static final String MODAL_PUBLIC = "public";
	private static final String MODAL_OVERRIDE_ENROLLMENTS = "overrideEnrollments";
	private static final String MODAL_INVISIBLE = "invisible";

	private KornellSession session;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private FormHelper formHelper = GWT.create(FormHelper.class);
	private boolean isCreationMode, canDelete, isPlatformAdmin;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;

	private Presenter presenter;

	@UiField
	HTMLPanel titleEdit;
	@UiField
	Form form;
	@UiField
	FlowPanel institutionFields;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;

	@UiField
	Modal confirmModal;
	@UiField
	Label confirmText;
	@UiField
	Button btnModalOK;
	@UiField
	Button btnModalCancel;

	private UserInfoTO user;
	private Institution institution;
	private CourseClass courseClass;

	private KornellFormFieldWrapper name, fullName, terms, assetsURL, baseURL, demandsPersonContactDetails, validatePersonContactDetails, allowRegistration, allowRegistrationByUsername;
	
	private FileUpload fileUpload;
	private List<KornellFormFieldWrapper> fields;
	private String modalMode;
	private ListBox institutionRegistrationPrefixes;
	
	public GenericAdminInstitutionView(final KornellSession session, EventBus bus, PlaceController placeCtrl, ViewFactory viewFactory) {
		this.session = session;
		this.presenter = presenter;
		this.user = session.getCurrentUser();
		this.isPlatformAdmin = session.isPlatformAdmin();
		this.canDelete = false;
		initWidget(uiBinder.createAndBindUi(this));

		// i18n
		btnOK.setText("OK".toUpperCase());
		btnCancel.setText(isCreationMode ? "Cancelar".toUpperCase() : "Limpar".toUpperCase());		

		btnModalOK.setText("OK".toUpperCase());
		btnModalCancel.setText("Cancelar".toUpperCase());
		
		this.institution = Dean.getInstance().getInstitution();
		
		initData();
	}

	public void initData() {
		institutionFields.setVisible(false);
		this.fields = new ArrayList<KornellFormFieldWrapper>();

		institutionFields.clear();
		
		btnOK.setVisible(isPlatformAdmin|| isCreationMode);
		btnCancel.setVisible(isPlatformAdmin);
		

		name = new KornellFormFieldWrapper("Sub-domínio da Instituição", formHelper.createTextBoxFormField(institution.getName()), isPlatformAdmin);
		fields.add(name);
		institutionFields.add(name);
		
		fullName = new KornellFormFieldWrapper("Nome da Instituição", formHelper.createTextBoxFormField(institution.getFullName()), isPlatformAdmin);
		fields.add(fullName);
		institutionFields.add(fullName);
		
		assetsURL = new KornellFormFieldWrapper("URL dos Recursos", formHelper.createTextBoxFormField(institution.getAssetsURL()), isPlatformAdmin);
		fields.add(assetsURL);
		institutionFields.add(assetsURL);
		
		baseURL = new KornellFormFieldWrapper("URL Base", formHelper.createTextBoxFormField(institution.getBaseURL()), isPlatformAdmin);
		fields.add(baseURL);
		institutionFields.add(baseURL);

		demandsPersonContactDetails = new KornellFormFieldWrapper("Exige Detalhes de Contato", formHelper.createCheckBoxFormField(institution.isDemandsPersonContactDetails()), isPlatformAdmin);
		fields.add(demandsPersonContactDetails);
		institutionFields.add(demandsPersonContactDetails);
		((CheckBox)demandsPersonContactDetails.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					showModal(MODAL_PUBLIC);
					((CheckBox)demandsPersonContactDetails.getFieldWidget()).setValue(false);
				}
			}
		});

		validatePersonContactDetails = new KornellFormFieldWrapper("Validação dos Detalhes de Contato", formHelper.createCheckBoxFormField(institution.isValidatePersonContactDetails()), isPlatformAdmin);
		fields.add(validatePersonContactDetails);
		institutionFields.add(validatePersonContactDetails);
		((CheckBox)validatePersonContactDetails.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					showModal(MODAL_PUBLIC);
					((CheckBox)validatePersonContactDetails.getFieldWidget()).setValue(false);
				}
			}
		});

		allowRegistration = new KornellFormFieldWrapper("Permitir Registro", formHelper.createCheckBoxFormField(institution.isAllowRegistration()), isPlatformAdmin);
		fields.add(allowRegistration);
		institutionFields.add(allowRegistration);
		((CheckBox)allowRegistration.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					showModal(MODAL_PUBLIC);
					((CheckBox)allowRegistration.getFieldWidget()).setValue(false);
				}
			}
		});

		allowRegistrationByUsername = new KornellFormFieldWrapper("Permitir Registro por Usuário", formHelper.createCheckBoxFormField(institution.isAllowRegistrationByUsername()), isPlatformAdmin);
		fields.add(allowRegistrationByUsername);
		institutionFields.add(allowRegistrationByUsername);
		((CheckBox)allowRegistrationByUsername.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					showModal(MODAL_PUBLIC);
					((CheckBox)allowRegistrationByUsername.getFieldWidget()).setValue(false);
				}
			}
		});
		
		terms = new KornellFormFieldWrapper("Termos de Uso", formHelper.createTextAreaFormField(institution.getTerms(), 20), isPlatformAdmin);
		terms.addStyleName("heightAuto");
		fields.add(terms);
		institutionFields.add(terms);

		
		//name, fullName, terms, assetsURL, baseURL, demandsPersonContactDetails, validatePersonContactDetails, allowRegistration, allowRegistrationByUsername
		
		institutionFields.add(formHelper.getImageSeparator());

		institutionFields.setVisible(true);
	}

	private void loadInstitutionPrefixes() {
		session.institution(Dean.getInstance().getInstitution().getUUID()).getRegistrationPrefixes(new Callback<InstitutionRegistrationPrefixesTO>() {
			@Override
			public void ok(InstitutionRegistrationPrefixesTO to) {
				// TODO Auto-generated method stub
				for (String registrationPrefix : to.getInstitutionRegistrationPrefixes()) {
					institutionRegistrationPrefixes.addItem(registrationPrefix);
        }
			}
		});
  }

	private boolean validateFields() {		
		if (!formHelper.isLengthValid(name.getFieldPersistText(), 2, 20)) {
			name.setError("Insira o sub-domínio da instituição.");
		}
		if (!formHelper.isLengthValid(fullName.getFieldPersistText(), 2, 50)) {
			fullName.setError("Insira o nome da instituição.");
		}
		if (!formHelper.isLengthValid(assetsURL.getFieldPersistText(), 10, 200)) {
			assetsURL.setError("Insira a URL dos recursos.");
		}
		if (!formHelper.isLengthValid(baseURL.getFieldPersistText(), 10, 200)) {
			baseURL.setError("Insira a URL base.");
		}
		
		return !formHelper.checkErrors(fields);
	}

	@UiHandler("btnOK")
	void doOK(ClickEvent e) {
		formHelper.clearErrors(fields);
		if (isPlatformAdmin && validateFields()) {
			LoadingPopup.show();
			Institution institution = getInstitutionInfoFromForm();
			presenter.updateInstitution(institution);

		}
	}

	private Institution getInstitutionInfoFromForm() {
		institution.setName(name.getFieldPersistText());
		institution.setFullName(fullName.getFieldPersistText());
		institution.setTerms(terms.getFieldPersistText());
		institution.setAssetsURL(assetsURL.getFieldPersistText());
		institution.setBaseURL(baseURL.getFieldPersistText());
		institution.setDemandsPersonContactDetails(demandsPersonContactDetails.getFieldPersistText().equals("true"));
		institution.setValidatePersonContactDetails(validatePersonContactDetails.getFieldPersistText().equals("true"));
		institution.setAllowRegistration(allowRegistration.getFieldPersistText().equals("true"));
		institution.setAllowRegistrationByUsername(allowRegistrationByUsername.getFieldPersistText().equals("true"));
		return institution;
	}

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) {
		if(isCreationMode){
			//presenter.updateCourseClass(null);
		} else {
			initData();
		}
	}

	private void showModal(String mode) {
		this.modalMode = mode;
		if(MODAL_DELETE.equals(modalMode)){
			confirmText.setText("Tem certeza que deseja excluir esta turma?"
					+ "\nEsta operação não pode ser desfeita.");
		} else if (MODAL_DEACTIVATE.equals(modalMode)){
			confirmText.setText("Tem certeza que deseja desabilitar esta turma? Os participantes matriculados ainda poderão acessar os detalhes da turma e emitir o certificado, mas não terão acesso ao material relacionado à turma."
					+ "\nEsta operação não pode ser desfeita.");
		} else if (MODAL_PUBLIC.equals(modalMode)){
			confirmText.setText("ATENÇÃO! Tem certeza que deseja tornar esta turma pública? Ela será visível e disponível para solicitação de matrícula para TODOS os alunos registrados nesta instituição.");
		} else if (MODAL_OVERRIDE_ENROLLMENTS.equals(modalMode)){
			confirmText.setText("ATENÇÃO! Tem certeza que deseja habilitar a sobrescrita de matrículas? Toda vez que uma matrícula em lote for feita, todas as matrículas já existentes que não estão presentes no lote serão canceladas.");
		} else if (MODAL_INVISIBLE.equals(modalMode)){
			confirmText.setText("ATENÇÃO! Tem certeza que deseja tornar esta turma invisível? Nenhum participante que esteja matriculado poderá ver essa turma, nem será capaz de gerar o certificado caso tenha sido aprovado.");
		}
		confirmModal.show();
  }

	@UiHandler("btnModalOK")
	void onModalOkButtonClicked(ClickEvent e) {
		/*if(MODAL_DELETE.equals(modalMode)){
			//presenter.changeCourseClassState(courseClassTO, CourseClassState.deleted);
		} else if (MODAL_DEACTIVATE.equals(modalMode)){
			//presenter.changeCourseClassState(courseClassTO, CourseClassState.inactive);
		} else if (MODAL_PUBLIC.equals(modalMode)){
			((CheckBox)publicClass.getFieldWidget()).setValue(true);
			((CheckBox)invisible.getFieldWidget()).setValue(false);
		} else if (MODAL_OVERRIDE_ENROLLMENTS.equals(modalMode)){
			((CheckBox)overrideEnrollments.getFieldWidget()).setValue(true);
		} else if (MODAL_INVISIBLE.equals(modalMode)){
			((CheckBox)invisible.getFieldWidget()).setValue(true);
			((CheckBox)publicClass.getFieldWidget()).setValue(false);
		}*/
		confirmModal.hide();
	}

	@UiHandler("btnModalCancel")
	void onModalCancelButtonClicked(ClickEvent e) {
		this.modalMode = null;
		confirmModal.hide();
	}

	@Override
  public void setCourseClasses(List<CourseClassTO> courseClasses) {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void setPresenter(Presenter presenter) {
	  this.presenter = presenter;
  }

}
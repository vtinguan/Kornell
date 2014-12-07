package kornell.gui.client.presentation.admin.course.course.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.KornellSession;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.Institution;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.course.course.AdminCourseView;
import kornell.gui.client.presentation.admin.course.course.AdminCourseView.Presenter;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionPlace;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.LoadingPopup;
import kornell.gui.client.util.view.formfield.KornellFormFieldWrapper;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.PlaceChangeEvent;
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

public class GenericAdminCourseView extends Composite implements AdminCourseView {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminCourseView> {
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
	private boolean isCreationMode, isPlatformAdmin;
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

	private Institution institution;

	private KornellFormFieldWrapper name, fullName, terms, assetsURL, baseURL, demandsPersonContactDetails, validatePersonContactDetails, allowRegistration, allowRegistrationByUsername;
	
	private FileUpload fileUpload;
	private List<KornellFormFieldWrapper> fields;
	private String modalMode;
	private ListBox institutionRegistrationPrefixes;
	
	public GenericAdminCourseView(final KornellSession session, EventBus bus, PlaceController placeCtrl, ViewFactory viewFactory) {
		this.session = session;
		this.isPlatformAdmin = session.isPlatformAdmin();
		initWidget(uiBinder.createAndBindUi(this));

		// i18n
		btnOK.setText("Salvar".toUpperCase());
		btnCancel.setText(isCreationMode ? "Cancelar".toUpperCase() : "Limpar".toUpperCase());		

		btnModalOK.setText("OK".toUpperCase());
		btnModalCancel.setText("Cancelar".toUpperCase());
		
		this.institution = Dean.getInstance().getInstitution();
		
		initData();

		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						if(event.getNewPlace() instanceof AdminInstitutionPlace)
							initData();
					}
				});
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
				}
			}
		});
		
		terms = new KornellFormFieldWrapper("Termos de Uso", formHelper.createTextAreaFormField(institution.getTerms(), 20), isPlatformAdmin);
		terms.addStyleName("heightAuto");
		terms.addStyleName("marginBottom25");
		fields.add(terms);
		institutionFields.add(terms);

		
		//name, fullName, terms, assetsURL, baseURL, demandsPersonContactDetails, validatePersonContactDetails, allowRegistration, allowRegistrationByUsername
		
		institutionFields.add(formHelper.getImageSeparator());

		institutionFields.setVisible(true);
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
		initData();
	}
	
	@Override
  public void setPresenter(Presenter presenter) {
	  this.presenter = presenter;
  }

}
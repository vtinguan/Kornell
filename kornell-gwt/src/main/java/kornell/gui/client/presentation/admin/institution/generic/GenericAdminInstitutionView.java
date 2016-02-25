package kornell.gui.client.presentation.admin.institution.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.KornellSession;
import kornell.core.entity.BillingType;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.Institution;
import kornell.core.entity.InstitutionType;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionPlace;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionView;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.forms.formfield.ListBoxFormField;
import kornell.gui.client.util.forms.formfield.TextBoxFormField;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.LoadingPopup;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.PlaceChangeEvent;
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

public class GenericAdminInstitutionView extends Composite implements AdminInstitutionView {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminInstitutionView> {
	}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	public static final EntityFactory entityFactory = GWT.create(EntityFactory.class);

	private KornellSession session;
	private FormHelper formHelper = GWT.create(FormHelper.class);
	private boolean isCreationMode, isPlatformAdmin;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;

	private Presenter presenter;

	@UiField
	TabPanel tabsPanel;
	@UiField
	Tab editTab;
	@UiField
	Tab hostnamesTab;
	@UiField
	FlowPanel hostnamesPanel;
	@UiField
	Tab emailWhitelistTab;
	@UiField
	FlowPanel emailWhitelistPanel;
	@UiField
	Tab reportsTab;
	@UiField
	FlowPanel reportsPanel;
	@UiField
	Tab adminsTab;
	@UiField
	FlowPanel adminsPanel;
	
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

	private KornellFormFieldWrapper name, fullName, institutionType, terms, assetsURL, baseURL, billingType, demandsPersonContactDetails, validatePersonContactDetails, allowRegistration, allowRegistrationByUsername, useEmailWhitelist, timeZone;
	
	private List<KornellFormFieldWrapper> fields;
	private GenericInstitutionReportsView reportsView;
	private GenericInstitutionAdminsView adminsView;
	private GenericInstitutionHostnamesView hostnamesView;
	private GenericInstitutionEmailWhitelistView emailWhitelistView;
	private EventBus bus;
	
	public GenericAdminInstitutionView(final KornellSession session, EventBus bus, PlaceController placeCtrl, ViewFactory viewFactory) {
		this.session = session;
		this.bus = bus;
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

		if (session.isPlatformAdmin()) {
			hostnamesTab.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					buildHostnamesView();
				}
			});
			
			emailWhitelistTab.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					buildEmailWhitelistView();
				}
			});
			
			adminsTab.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					buildAdminsView();
				}
			});


			buildReportsView();
			reportsTab.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					buildReportsView();
				}
			});
		}
	}

	public void buildHostnamesView() {
		hostnamesView = new GenericInstitutionHostnamesView(session, presenter, institution);
		hostnamesPanel.clear();
		hostnamesPanel.add(hostnamesView);
	}
	
	public void buildEmailWhitelistView() {
		emailWhitelistView = new GenericInstitutionEmailWhitelistView(session, presenter, institution);
		emailWhitelistPanel.clear();
		emailWhitelistPanel.add(emailWhitelistView);
	}

	public void buildReportsView() {
		if (reportsView == null) {
			reportsView = new GenericInstitutionReportsView(session, bus, institution);
		}
		reportsPanel.clear();
		reportsPanel.add(reportsView);
	}

	public void buildAdminsView() {
		adminsView = new GenericInstitutionAdminsView(session, presenter, institution);
		adminsPanel.clear();
		adminsPanel.add(adminsView);
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
		
		final ListBox institutionTypes = new ListBox();
		institutionTypes.addItem("Padrão", InstitutionType.DEFAULT.toString());
		institutionTypes.addItem("Dashboard", InstitutionType.DASHBOARD.toString());
		if (!isCreationMode) {
			institutionTypes.setSelectedValue(institution.getInstitutionType().toString());
		}
		institutionType = new KornellFormFieldWrapper("Tipo de Instituição", new ListBoxFormField(institutionTypes), isPlatformAdmin);
		fields.add(institutionType);
		institutionFields.add(institutionType);
		
		assetsURL = new KornellFormFieldWrapper("URL dos Recursos", formHelper.createTextBoxFormField(institution.getAssetsURL()), isPlatformAdmin);
		fields.add(assetsURL);
		institutionFields.add(assetsURL);
		
		baseURL = new KornellFormFieldWrapper("URL Base", formHelper.createTextBoxFormField(institution.getBaseURL()), isPlatformAdmin);
		fields.add(baseURL);
		institutionFields.add(baseURL);
		
		final ListBox billingTypes = new ListBox();
		billingTypes.addItem("Mensal", BillingType.monthly.toString());
		billingTypes.addItem("Matrícula", BillingType.enrollment.toString());
		if (!isCreationMode) {
			billingTypes.setSelectedValue(institution.getBillingType().toString());
		}
		billingType = new KornellFormFieldWrapper("Tipo de Cobrança", new ListBoxFormField(billingTypes), isPlatformAdmin);
		fields.add(billingType);
		institutionFields.add(billingType);

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

		useEmailWhitelist = new KornellFormFieldWrapper("Configurar domínios para emails?", formHelper.createCheckBoxFormField(institution.isUseEmailWhitelist()), isPlatformAdmin);
		fields.add(useEmailWhitelist);
		institutionFields.add(useEmailWhitelist);
		((CheckBox)useEmailWhitelist.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
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


		final ListBox timeZones = formHelper.getTimeZonesList();
		if(institution.getTimeZone() != null){
			timeZones.setSelectedValue(institution.getTimeZone());
		}
		timeZone = new KornellFormFieldWrapper("Fuso horário", new ListBoxFormField(timeZones), isPlatformAdmin);
		fields.add(timeZone);
		institutionFields.add(timeZone);
		
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
		if(!formHelper.isLengthValid(timeZone.getFieldPersistText(), 2, 100)){
			timeZone.setError("Escolha o fuso horário.");
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
		institution.setBillingType(BillingType.valueOf(billingType.getFieldPersistText()));
		institution.setInstitutionType(InstitutionType.valueOf(institutionType.getFieldPersistText()));
		institution.setDemandsPersonContactDetails(demandsPersonContactDetails.getFieldPersistText().equals("true"));
		institution.setValidatePersonContactDetails(validatePersonContactDetails.getFieldPersistText().equals("true"));
		institution.setAllowRegistration(allowRegistration.getFieldPersistText().equals("true"));
		institution.setAllowRegistrationByUsername(allowRegistrationByUsername.getFieldPersistText().equals("true"));
		institution.setUseEmailWhitelist(useEmailWhitelist.getFieldPersistText().equals("true"));
		institution.setTimeZone(timeZone.getFieldPersistText());
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
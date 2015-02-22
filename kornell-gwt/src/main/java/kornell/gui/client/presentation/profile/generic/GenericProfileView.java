package kornell.gui.client.presentation.profile.generic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.InstitutionRegistrationPrefix;
import kornell.core.entity.Person;
import kornell.core.entity.RegistrationType;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.error.KornellErrorTO;
import kornell.core.to.UserInfoTO;
import kornell.core.util.TimeUtil;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.profile.ProfileView;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;
import kornell.gui.client.util.view.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.view.formfield.ListBoxFormField;
import kornell.gui.client.util.view.formfield.SimpleDatePicker;
import kornell.gui.client.util.view.formfield.SimpleDatePickerFormField;
import kornell.gui.client.util.view.formfield.TextBoxFormField;
import kornell.gui.client.validation.CPFValidator;
import kornell.gui.client.validation.EmailValidator;
import kornell.gui.client.validation.ValidationChangedHandler;

import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.event.shared.EventBus;

public class GenericProfileView extends Composite implements ProfileView,ValidationChangedHandler {
	
	interface MyUiBinder extends UiBinder<Widget, GenericProfileView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	Logger logger = Logger.getLogger(GenericProfileView.class.getName());

	private KornellSession session;
	private ViewFactory viewFactory;
	private PlaceController placeCtrl;
	private final EventBus bus;
	private FormHelper formHelper;
	private boolean isEditMode, isCurrentUser, isAdmin, hasPowerOver, showEmail = true, showCPF = true, showContactDetails, validateContactDetails;

	// TODO fix this
	private String IMAGE_PATH = "skins/first/icons/profile/";
	private String DISABLED_CLASS = "btnNotSelected";
	private String ENABLED_CLASS = "btnAction";
	private String CURSOR_DEFAULT_CLASS = "cursorDefault";
	private String CURSOR_POINTER_CLASS = "cursorPointer";
	
	@UiField Form form;
	@UiField FlowPanel profileFields;
	@UiField GenericPasswordChangeView passwordChangeWidget;
	@UiField GenericSendMessageView sendMessageWidget;
	
	private UserInfoTO user;
	
	
	private KornellFormFieldWrapper cpf, email, username, fullName, telephone, country, state, city, addressLine1, addressLine2, postalCode, company, position, sex, birthDate;
	private List<KornellFormFieldWrapper> fields;
	private Button btnChangePassword, btnSendMessage, btnEdit, btnClose, btnOK, btnCancel;
	private ClientFactory clientFactory;

	private String profileUserUUID;

	public GenericProfileView(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.viewFactory = clientFactory.getViewFactory();
		this.bus = clientFactory.getEventBus();
		this.session = clientFactory.getKornellSession();
		this.user = session.getCurrentUser();
		this.placeCtrl = clientFactory.getPlaceController();
		this.fields = new ArrayList<KornellFormFieldWrapper>();
		formHelper = new FormHelper();
		initWidget(uiBinder.createAndBindUi(this));

		// i18n
		btnEdit = new Button();
		btnEdit.setText("Editar");
		btnEdit.addStyleName("btnPlaceBar btnAction btnStandard");
		btnEdit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent e) {
				doEdit(e);
			}
		});
		
		btnClose = new Button();
		btnClose.setText("Fechar");
		btnClose.addStyleName("btnPlaceBar btnNotSelected btnStandard");
		btnClose.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent e) {
				doClose(e);
			}
		});

		btnOK = new Button();
		btnOK.setText("Salvar Alterações");
		btnOK.addStyleName("btnPlaceBar btnAction btnStandard");
		btnOK.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent e) {
				doOK(e);
			}
		});
		
		btnCancel = new Button();
		btnCancel.setText("Cancelar");
		btnCancel.addStyleName("btnPlaceBar btnNotSelected btnStandard");
		btnCancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent e) {
				doCancel(e);
			}
		});
		
		btnChangePassword = new Button();
		btnChangePassword.setText("Alterar Senha");
		btnChangePassword.addStyleName("btnPlaceBar btnSelected btnChangePassword");
		btnChangePassword.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				passwordChangeWidget.show();
			}
		});
		
		btnSendMessage = new Button();
		btnSendMessage.setText("Enviar Mensagem");
		btnSendMessage.addStyleName("btnPlaceBar btnNotSelected btnChangePassword");
		btnSendMessage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				sendMessageWidget.show();
			}
		});
		
		initData();
		
		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				if(event.getNewPlace() instanceof ProfilePlace){							
					initData();
				}
			}});
	}

	private void initData() {

		viewFactory.getMenuBarView().initPlaceBar(IconType.USER, "Perfil", "Mantenha seus dados atualizados");
		
		isCurrentUser = session.getCurrentUser().getPerson().getUUID().equals(((ProfilePlace) placeCtrl.getWhere()).getPersonUUID());
		isEditMode = ((ProfilePlace)placeCtrl.getWhere()).isEdit() && isCurrentUser;
		isAdmin = RoleCategory.hasRole(session.getCurrentUser().getRoles(),RoleType.courseClassAdmin) || session.isInstitutionAdmin() || session.isPlatformAdmin();
		
		form.addStyleName("shy");
		
		profileUserUUID = ((ProfilePlace) placeCtrl.getWhere()).getPersonUUID();

		session.user().hasPowerOver(profileUserUUID, new Callback<Boolean>() {
			@Override
			public void ok(Boolean hasPowerOverTargetUser) {
				List<IsWidget> widgets = new ArrayList<IsWidget>();
				if(!isCurrentUser){
					widgets.add(btnSendMessage);
				}
				if(hasPowerOverTargetUser){
					widgets.add(btnChangePassword);
				}
				widgets.add(btnCancel);
				//widgets.add(btnClose);
				widgets.add(btnEdit);
				widgets.add(btnOK);
				viewFactory.getMenuBarView().setPlaceBarWidgets(widgets);
				
				hasPowerOver = hasPowerOverTargetUser;
				session.user().getUser(profileUserUUID, new Callback<UserInfoTO>() {
					@Override
					public void ok(UserInfoTO to) {
						user = to;
						display();
					}
					@Override
					public void notFound(KornellErrorTO kornellErrorTO) {
						logger.severe(this.getClass().getName() + " - not found");
						user = null;
						display();
					}
				});
			}
		});
	}

	private boolean validateFields() {
		if(!formHelper.isLengthValid(fullName.getFieldPersistText(), 5, 50)){
			fullName.setError("Insira seu nome.");
		} 
		
		if(showContactDetails && validateContactDetails){
			if(!formHelper.isLengthValid(telephone.getFieldPersistText(), 7, 20)){
				telephone.setError("Insira seu telefone.");
			} else telephone.setError("");
			
			if(!formHelper.isLengthValid(country.getFieldPersistText(), 0, 2)){
				country.setError("Selecione seu país.");
			} else country.setError("");
			
			if("BR".equals(country.getFieldPersistText())){
				if(!formHelper.isListBoxSelected(((ListBox) state.getFieldWidget()))){
					state.setError("Selecione seu estado.");
				} else state.setError("");
			} else {
				if(!formHelper.isLengthValid(state.getFieldPersistText(), 2, 100)){
					state.setError("Insira seu estado.");
				} else state.setError("");
			}
			if(!formHelper.isLengthValid(city.getFieldPersistText(), 2, 100)){
				city.setError("Insira sua cidade.");
			} else city.setError("");
			
			if(!formHelper.isLengthValid(addressLine1.getFieldPersistText(), 2, 100)){
				addressLine1.setError("Insira seu endereço.");
			} else addressLine1.setError("");
			
			if(!formHelper.isLengthValid(postalCode.getFieldPersistText(), 2, 100)){
				postalCode.setError("Insira seu código postal.");
			} else postalCode.setError("");
			
		}

		return !checkErrors();
	}

	void doOK(ClickEvent e) { 
		formHelper.clearErrors(fields);

		if(isEditMode && validateFields()){
			LoadingPopup.show();
			session.user().updateUser(getUserInfoFromForm(), new Callback<UserInfoTO>(){
				@Override
				public void ok(UserInfoTO userInfo){
					LoadingPopup.hide();
					KornellNotification.show("Alterações salvas com sucesso!");
					btnOK.setEnabled(true);
					isEditMode = false;
					display();
					History.back();
					//placeCtrl.goTo(clientFactory.getDefaultPlace());
					session.getCurrentUser(true, new Callback<UserInfoTO>() {
						@Override
						public void ok(UserInfoTO to) {
							user = to;
						}
					});
				}
				@Override
				public void unauthorized(KornellErrorTO kornellErrorTO){
					LoadingPopup.hide();
					KornellNotification.show("Erros ao salvar usuário.", AlertType.ERROR);
				}
			});   
		}
	}

	private UserInfoTO getUserInfoFromForm() {
		//"clone" user
		String userPayload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(user)).getPayload();
		UserInfoTO userTmp = AutoBeanCodex.decode(clientFactory.getTOFactory(), kornell.core.to.UserInfoTO.class, userPayload).as();
		Person person = userTmp.getPerson();
		
		if(showCPF){
			person.setCPF(FormHelper.stripCPF(cpf.getFieldPersistText()));
		}
		if(showEmail){
			person.setEmail(email.getFieldPersistText());
		}
		person.setFullName(fullName.getFieldPersistText());
		person.setCompany(company.getFieldPersistText());
		person.setTitle(position.getFieldPersistText());
		person.setSex(sex.getFieldPersistText());
		person.setBirthDate(formHelper.getDateFromString(birthDate.getFieldPersistText()));

		if(showContactDetails){
			person.setTelephone(telephone.getFieldPersistText());
			person.setCountry(country.getFieldPersistText());
			person.setState(state.getFieldPersistText());
			person.setCity(city.getFieldPersistText());
			person.setAddressLine1(addressLine1.getFieldPersistText());
			person.setAddressLine2(addressLine2.getFieldPersistText());
			person.setPostalCode(postalCode.getFieldPersistText());
		}

    if("".equals(person.getCPF())) 
    	person.setCPF(null);
    if("".equals(person.getEmail())) 
    	person.setEmail(null);
    
    userTmp.setPerson(person);
		return userTmp;
	}

	void doCancel(ClickEvent e) {
		if(showContactDetails && validateContactDetails && session.getCurrentUser().getPerson().getCity() == null){
			bus.fireEvent(new LogoutEvent());
		} else {
			isEditMode = false;
			formHelper.clearErrors(fields);
			display();
		}
	}

	void doEdit(ClickEvent e) {
		isEditMode = true;
		formHelper.clearErrors(fields);
		display();
	}

	void doClose(ClickEvent e) {
		form.addStyleName("shy");
		History.back();
		//placeCtrl.goTo(clientFactory.getDefaultPlace());
	}


	private boolean checkErrors() {
		for (KornellFormFieldWrapper field : fields) 
			if(!"".equals(field.getError())){
				KornellNotification.show("Existem erros nos dados.", AlertType.WARNING);
				if(field.getFieldWidget() instanceof FocusWidget)
					((FocusWidget)field.getFieldWidget()).setFocus(true);
				return true;		
			}
		return false;
	}

	private void display() {

		showContactDetails = Dean.getInstance().getInstitution().isDemandsPersonContactDetails();
		validateContactDetails = Dean.getInstance().getInstitution().isValidatePersonContactDetails();
		if(RegistrationType.username.equals(user.getPerson().getRegistrationType())){
			InstitutionRegistrationPrefix institutionRegistrationPrefix = user.getInstitutionRegistrationPrefix(); 
			showEmail = institutionRegistrationPrefix.isShowEmailOnProfile();
			showCPF = institutionRegistrationPrefix.isShowCPFOnProfile();
			showContactDetails = showContactDetails && institutionRegistrationPrefix.isShowContactInformationOnProfile();
		}

		form.addStyleName("shy");
		
		btnOK.setVisible(isEditMode);
		btnCancel.setVisible(isEditMode);
		btnClose.setVisible(!isEditMode);
		btnChangePassword.setVisible(isEditMode && (isCurrentUser || hasPowerOver));
		btnEdit.setVisible(!isEditMode && (isCurrentUser || hasPowerOver));

		profileFields.clear();
		if(user == null){
			KornellNotification.show("Usuário não encontrado.", AlertType.ERROR);
			return;
		} 
				
		if(isEditMode && showContactDetails && validateContactDetails && session.getCurrentUser().getPerson().getCity() == null){
			KornellNotification.show("Por favor, conclua o preenchimento do seu cadastro.", AlertType.INFO, 5000);
		}

		//profileFields.add(getPictureUploadFormPanel());

		username = new KornellFormFieldWrapper("Usuário", formHelper.createTextBoxFormField(user.getUsername()), false);
		fields.add(username);
		profileFields.add(username);

		fullName = new KornellFormFieldWrapper("Nome Completo", formHelper.createTextBoxFormField(user.getPerson().getFullName()), isEditMode);
		fields.add(fullName);
		profileFields.add(fullName);
		
		KornellSession session = clientFactory.getKornellSession();
		if(showEmail){
			email = 
					new KornellFormFieldWrapper("Email", 
							formHelper.createTextBoxFormField(user.getPerson().getEmail()), 
							isEditMode,
							EmailValidator.unregisteredEmailValidator(profileUserUUID, session));
			requireValid(email);
			fields.add(email);
			profileFields.add(email);
		}
		if(showCPF && (isCurrentUser || isAdmin)){
			cpf = new KornellFormFieldWrapper
					("CPF", 
					formHelper.createTextBoxFormField(user.getPerson().getCPF()), 
					isEditMode,
					CPFValidator.unregisteredCPFValidator(profileUserUUID, session));
			requireValid(cpf);
			fields.add(cpf);
			profileFields.add(cpf);
		}

		company = new KornellFormFieldWrapper("Empresa", formHelper.createTextBoxFormField(user.getPerson().getCompany()), isEditMode);
		fields.add(company);
		profileFields.add(company);

		position = new KornellFormFieldWrapper("Cargo", formHelper.createTextBoxFormField(user.getPerson().getTitle()), isEditMode);
		fields.add(position);
		profileFields.add(position);

		if(isCurrentUser || isAdmin){
			final ListBox sexes = formHelper.getSexList();
			sexes.setSelectedValue(user.getPerson().getSex());
			sex = new KornellFormFieldWrapper("Sexo", new ListBoxFormField(sexes), isEditMode);
			fields.add(sex);
			profileFields.add(sex);

			SimpleDatePicker datePicker = new SimpleDatePicker();
			if(isEditMode || isCurrentUser || isAdmin){
				datePicker.setFields(TimeUtil.toJUD(user.getPerson().getBirthDate()));
			}
			birthDate = new KornellFormFieldWrapper("Data de Nascimento", new SimpleDatePickerFormField(datePicker), isEditMode);
			fields.add(birthDate);
			profileFields.add(birthDate);
		}

		if((isCurrentUser || isAdmin)&& showContactDetails){
			displayContactDetails();
		}
		
		form.removeStyleName("shy");
		setValidity(true);

		passwordChangeWidget.initData(session, user, isCurrentUser);
		sendMessageWidget.initData(session, user, isCurrentUser);
		
	}

	/*private FormPanel getPictureUploadFormPanel() {
		final FormPanel formPanel = new FormPanel();
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.setAction("http://"+ s3Policy.getBucketName() +".s3.amazonaws.com/");

		FlowPanel fileUploadWrapper = new FlowPanel();
		fileUploadWrapper.addStyleName("fileUploadWrapper");
		fileUploadWrapper.add(new Image(IMAGE_PATH + "profilePic.png"));   

		fileUploadWrapper.add(new Hidden("key", s3Policy.getKey()+"${filename}"));
		fileUploadWrapper.add(new Hidden("acl", "public-read"));
		fileUploadWrapper.add(new Hidden("success_action_redirect", s3Policy.getSuccessActionRedirect()));
		fileUploadWrapper.add(new Hidden("Content-Type", "image/jpeg"));
		fileUploadWrapper.add(new Hidden("x-amz-meta-uuid", "14365123651275"));
		fileUploadWrapper.add(new Hidden("x-amz-meta-tag", ""));
		fileUploadWrapper.add(new Hidden("AWSAccessKeyId", s3Policy.getAWSAccessKeyId()));
		fileUploadWrapper.add(new Hidden("Policy", s3Policy.getPolicy()));
		fileUploadWrapper.add(new Hidden("Signature", s3Policy.getSignature()));		    

		fileUpload = new FileUpload();
		fileUpload.setName("file");
		fileUploadWrapper.add(fileUpload);

		SubmitButton changeImageButton = new SubmitButton();
		changeImageButton.setName("submit");
		changeImageButton.setText("TROCAR IMAGEM");
		changeImageButton.setStyleName("btnAction btnStandard");
		fileUploadWrapper.add(changeImageButton);

		formPanel.addSubmitHandler(new FormPanel.SubmitHandler() {
			public void onSubmit(SubmitEvent event) {
				if("".equals(fileUpload.getFilename())){
					event.cancel();
				}
			}
		});
		formPanel.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				KornellNotification.show(event.getResults());
			}
		});

		formPanel.setWidget(fileUploadWrapper);
		return formPanel;
	}*/

	List<KornellFormFieldWrapper> requiredFields = new ArrayList<KornellFormFieldWrapper>();
	
	private void requireValid(KornellFormFieldWrapper field) {
		requiredFields.add(field);
		field.addValidationListener(this);
	}
	
	@Override
	public void onValidationChanged() {
		boolean isValid = true;
		for (Iterator<KornellFormFieldWrapper> it = requiredFields.iterator(); 
				it.hasNext();) {
			KornellFormFieldWrapper field = (KornellFormFieldWrapper) it.next();
			isValid = isValid && field.isValid();			
		}
		validateFields();
		setValidity(isValid);
	}
	
	void setValidity(boolean isValid){
		btnOK.setEnabled(isValid);
		if(isValid){
			btnOK.removeStyleName(DISABLED_CLASS);
			btnOK.addStyleName(ENABLED_CLASS);			
			btnOK.removeStyleName(CURSOR_DEFAULT_CLASS);
			btnOK.addStyleName(CURSOR_POINTER_CLASS);		
		}else{
			btnOK.addStyleName(DISABLED_CLASS);
			btnOK.removeStyleName(ENABLED_CLASS);
			btnOK.removeStyleName(CURSOR_POINTER_CLASS);
			btnOK.addStyleName(CURSOR_DEFAULT_CLASS);		
			KornellNotification.show("Existem erros nos dados.", AlertType.WARNING);
		}
	}
	

	private void displayContactDetails() {
		profileFields.add(getImageSeparator());

		telephone = new KornellFormFieldWrapper("Telefone", formHelper.createTextBoxFormField(user.getPerson().getTelephone()), isEditMode);
		fields.add(telephone);
		profileFields.add(telephone);

		final ListBox countries = formHelper.getCountriesList();
		if(isEditMode && user.getPerson().getCountry() == null){
			countries.setSelectedValue("BR");
		} else {
			countries.setSelectedValue(user.getPerson().getCountry());
		}
		countries.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if("BR".equals(countries.getValue())){
					state.initData(new ListBoxFormField(formHelper.getBrazilianStatesList()));
				} else {
					if(!(state.getFieldWidget() instanceof TextBox))
						state.initData(new TextBoxFormField(new TextBox()));
				}
			}
		});
		country = new KornellFormFieldWrapper("País", new ListBoxFormField(countries), isEditMode);
		fields.add(country);
		profileFields.add(country);

		if("BR".equals(countries.getValue())){
			//state.getFormField().clear();
			final ListBox states = formHelper.getBrazilianStatesList();
			if(user.getPerson().getState() != null){
				states.setSelectedValue(user.getPerson().getState());
			}
			state = new KornellFormFieldWrapper("Estado", new ListBoxFormField(states), isEditMode);
		} else {
			state = new KornellFormFieldWrapper("Estado", formHelper.createTextBoxFormField(user.getPerson().getState()), isEditMode);
		}
		fields.add(state);
		profileFields.add(state);

		city = new KornellFormFieldWrapper("Cidade", formHelper.createTextBoxFormField(user.getPerson().getCity()), isEditMode);
		fields.add(city);
		profileFields.add(city);

		addressLine1 = new KornellFormFieldWrapper("Endereço", formHelper.createTextBoxFormField(user.getPerson().getAddressLine1()), isEditMode);
		fields.add(addressLine1);
		profileFields.add(addressLine1);

		addressLine2 = new KornellFormFieldWrapper("Complemento", formHelper.createTextBoxFormField(user.getPerson().getAddressLine2()), isEditMode);
		fields.add(addressLine2);
		profileFields.add(addressLine2);

		postalCode = new KornellFormFieldWrapper("Código Postal", formHelper.createTextBoxFormField(user.getPerson().getPostalCode()), isEditMode);
		fields.add(postalCode);
		profileFields.add(postalCode);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		// TODO Auto-generated method stub
	}

	private Image getImageSeparator(){
		Image image = new Image("skins/first/icons/profile/separatorBar.png");
		image.addStyleName("profileSeparatorBar");
		return image;
	}

}

package kornell.gui.client.presentation.profile.generic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Institution;
import kornell.core.entity.Person;
import kornell.core.entity.Registration;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.to.UserInfoTO;
import kornell.core.util.TimeUtil;
import kornell.gui.client.ClientFactory;
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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.event.shared.EventBus;

public class GenericProfileView extends Composite implements ProfileView,ValidationChangedHandler {
	
	interface MyUiBinder extends UiBinder<Widget, GenericProfileView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private KornellSession session;
	private PlaceController placeCtrl;
	private final EventBus bus;
	private Institution institution;
	private FormHelper formHelper;
	private boolean isEditMode, isCurrentUser, isAdmin, hasPowerOver, showContactDetails;

	// TODO fix this
	private String IMAGE_PATH = "skins/first/icons/profile/";
	private String DISABLED_CLASS = "btnNotSelected";
	private String ENABLED_CLASS = "btnAction";
	private String CURSOR_DEFAULT_CLASS = "cursorDefault";
	private String CURSOR_POINTER_CLASS = "cursorPointer";
	
	@UiField Form form;
	@UiField FlowPanel profileFields;
	@UiField FlowPanel titlePanel;
	@UiField Image imgTitle;
	@UiField Label lblTitle;
	@UiField Button btnEdit;
	@UiField Button btnClose;
	@UiField Button btnOK;
	@UiField Button btnCancel;
	@UiField GenericPasswordChangeView passwordChangeWidget;
	
	private UserInfoTO user;
	
	
	private KornellFormFieldWrapper cpf, email, fullName, telephone, country, state, city, addressLine1, addressLine2, postalCode, company, position, sex, birthDate;
	private List<KornellFormFieldWrapper> fields;
	private Button btnChangePassword;
	private ClientFactory clientFactory;

	public GenericProfileView(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.bus = clientFactory.getEventBus();
		this.session = clientFactory.getKornellSession();
		this.user = session.getCurrentUser();
		this.placeCtrl = clientFactory.getPlaceController();
		this.institution = Dean.getInstance().getInstitution();
		this.fields = new ArrayList<KornellFormFieldWrapper>();
		formHelper = new FormHelper();
		initWidget(uiBinder.createAndBindUi(this));

		// i18n
		btnEdit.setText("Editar".toUpperCase());
		btnClose.setText("Fechar".toUpperCase());
		btnOK.setText("OK".toUpperCase());
		btnCancel.setText("Cancelar".toUpperCase());
		
		btnChangePassword = new Button();
		btnChangePassword.setText("Alterar Senha".toUpperCase());
		btnChangePassword.addStyleName("btnSelected btnStandard btnChangePassword");
		btnChangePassword.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				passwordChangeWidget.show();
			}
		});

		imgTitle.setUrl(IMAGE_PATH + "course.png");
		lblTitle.setText("Perfil");

		showContactDetails = Dean.getInstance().getInstitution().isDemandsPersonContactDetails();

		/*session.getS3PolicyTO(new Callback<S3PolicyTO>() {
			@Override
			public void ok(S3PolicyTO to) {
				s3Policy = to;
			}
		});*/

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
		
		isCurrentUser = session.getCurrentUser().getPerson().getUUID().equals(((ProfilePlace) placeCtrl.getWhere()).getPersonUUID());
		isEditMode = ((ProfilePlace)placeCtrl.getWhere()).isEdit() && isCurrentUser;
		boolean isInstitutionAdmin = false;
		for (Registration registration : session.getCurrentUser().getRegistrationsTO().getRegistrations()) {
			if(registration.getInstitutionUUID().equals(Dean.getInstance().getInstitution().getUUID())){
				isInstitutionAdmin = session.isInstitutionAdmin();
				break;
			}
		}
		isAdmin = RoleCategory.hasRole(session.getCurrentUser().getRoles(),RoleType.courseClassAdmin) || isInstitutionAdmin || session.isPlatformAdmin();
		
		form.addStyleName("shy");
		
		final String profileUserUUID = ((ProfilePlace) placeCtrl.getWhere()).getPersonUUID();

		session.user().hasPowerOver(profileUserUUID, new Callback<Boolean>() {
			@Override
			public void ok(Boolean hasPowerOverTargetUser) {
				if(hasPowerOverTargetUser){
					titlePanel.add(btnChangePassword);
				}
				hasPowerOver = hasPowerOverTargetUser;
				session.user().getUser(profileUserUUID, new Callback<UserInfoTO>() {
					@Override
					public void ok(UserInfoTO to) {
						user = to;
						display();
					}
					@Override
					public void unauthorized(String errorMessage) {
						GWT.log(this.getClass().getName() + " - " + errorMessage);
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
		} else fullName.setError("");
		
		if(showContactDetails){
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

	@UiHandler("btnOK")
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
					form.addStyleName("shy");
					placeCtrl.goTo(clientFactory.getDefaultPlace());
					session.getCurrentUser(true, new Callback<UserInfoTO>() {
						@Override
						public void ok(UserInfoTO to) {
							user = to;
						}
					});
				}
				@Override
				public void internalServerError(){
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
		
		person.setCPF(FormHelper.stripCPF(cpf.getFieldPersistText()));
		person.setEmail(email.getFieldPersistText());
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

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) {
		if(showContactDetails && session.getCurrentUser().getPerson().getCity() == null){
			bus.fireEvent(new LogoutEvent());
		} else {
			isEditMode = false;
			formHelper.clearErrors(fields);
			display();
		}
	}

	@UiHandler("btnEdit")
	void doEdit(ClickEvent e) {
		isEditMode = true;
		formHelper.clearErrors(fields);
		display();
	}

	@UiHandler("btnClose")
	void doClose(ClickEvent e) {
		form.addStyleName("shy");
		placeCtrl.goTo(clientFactory.getDefaultPlace());
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
		

		boolean isRegistered = false;
		for (Registration registration : user.getRegistrationsTO().getRegistrations()) {
			if(registration.getInstitutionUUID().equals(institution.getUUID()))
				isRegistered = true;
		}
		if(!isRegistered){
			KornellNotification.show("Este usuário não está registrado nesta instituição.", AlertType.ERROR);
			return;
		}
		
		if(isEditMode && showContactDetails && session.getCurrentUser().getPerson().getCity() == null){
			KornellNotification.show("Por favor, conclua o preenchimento do seu cadastro.", AlertType.INFO, 5000);
		}

		//profileFields.add(getPictureUploadFormPanel());

		fullName = new KornellFormFieldWrapper("Nome Completo", formHelper.createTextBoxFormField(user.getPerson().getFullName()), isEditMode && isAdmin);
		fields.add(fullName);
		profileFields.add(fullName);
		
		KornellSession session = clientFactory.getKornellSession();
		email = 
				new KornellFormFieldWrapper("Email", 
						formHelper.createTextBoxFormField(user.getPerson().getEmail()), 
						isEditMode,
						EmailValidator.unregisteredEmailValidator(session));
		cpf = new KornellFormFieldWrapper
				("CPF", 
				formHelper.createTextBoxFormField(user.getPerson().getCPF()), 
				isEditMode,
				CPFValidator.unregisteredCPFValidator(session));
		
		requireValid(cpf);
		requireValid(email);
		
		fields.add(email);
		fields.add(cpf);
		
		profileFields.add(email);
		profileFields.add(cpf);

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

		profileFields.add(formHelper.getImageSeparator());
		
		form.removeStyleName("shy");
		setValidity(true);
		
		passwordChangeWidget.initData(session, user, isCurrentUser);
		
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

		addressLine1 = new KornellFormFieldWrapper("Endereço Linha 1", formHelper.createTextBoxFormField(user.getPerson().getAddressLine1()), isEditMode);
		fields.add(addressLine1);
		profileFields.add(addressLine1);

		addressLine2 = new KornellFormFieldWrapper("Endereço Linha 2", formHelper.createTextBoxFormField(user.getPerson().getAddressLine2()), isEditMode);
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

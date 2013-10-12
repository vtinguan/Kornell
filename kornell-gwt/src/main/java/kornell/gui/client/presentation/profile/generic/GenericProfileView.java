package kornell.gui.client.presentation.profile.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.profile.ProfileView;
import kornell.gui.client.presentation.util.SimpleDatePicker;
import kornell.gui.client.presentation.util.ValidatorHelper;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericProfileView extends Composite implements ProfileView {
	interface MyUiBinder extends UiBinder<Widget, GenericProfileView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private KornellClient client;
	private PlaceController placeCtrl;
	private final EventBus bus;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private boolean isEditMode;
	private boolean isCurrentUser;
	SimpleDatePicker birthDate;
	
	// TODO fix this
	private String IMAGE_PATH = "skins/first/icons/profile/";
	@UiField FlowPanel profileFields;
	@UiField FlowPanel titlePanel;
	@UiField Image imgTitle;
	@UiField Label lblTitle;
	@UiField FlowPanel editPanel;
	@UiField Label lblEdit;
	@UiField Button btnOK;
	@UiField Button btnCancel;
	@UiField Image imgProfile;
	@UiField TextBox username;
	@UiField PasswordTextBox password;
	@UiField TextBox email;
	@UiField TextBox firstName;
	@UiField TextBox lastName;
	@UiField TextBox company;
	@UiField TextBox title;
	@UiField ListBox sex;
	@UiField FlowPanel birthDatePickerPanel;
	@UiField Label usernameError;
	@UiField Label emailError;
	@UiField Label passwordError;
	@UiField Label firstNameError;
	@UiField Label lastNameError;
	@UiField Label companyError;
	@UiField Label titleError;
	@UiField Label sexError;
	@UiField Label birthDateError;
	@UiField Label usernameTxt;
	@UiField Label passwordTxt;
	@UiField Label emailTxt;
	@UiField Label firstNameTxt;
	@UiField Label lastNameTxt;
	@UiField Label companyTxt;
	@UiField Label titleTxt;
	@UiField Label sexTxt;
	@UiField Label birthDateTxt;
	@UiField FlowPanel passwordPanel;
	@UiField FlowPanel emailPanel;
	@UiField FlowPanel sexPanel;
	@UiField FlowPanel birthDatePanel;
	@UiField Image passwordSeparator;
	@UiField Image emailSeparator;
	@UiField Image sexSeparator;
	@UiField Image birthDateSeparator;
	@UiField Label imageExtraInfo;
	@UiField Label usernameExtraInfo;
	@UiField Label usernameExtraInfo2;
	@UiField Label emailExtraInfo;
	@UiField Label titleExtraInfo;
	@UiField Label titleExtraInfo2;
	
	Map<Widget, Field> fieldsToErrorLabels;
	private UserInfoTO user;

	public GenericProfileView(EventBus bus, KornellClient client,
			final PlaceController placeCtrl) {
		this.bus = bus;
		this.client = client;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		initData();
		// i18n
		btnOK.setText("OK".toUpperCase());
		btnCancel.setText("Cancelar".toUpperCase());
	}

	private void initData() {
		client.getCurrentUser(new Callback<UserInfoTO>() {
			@Override
			protected void ok(UserInfoTO userTO) {
				user = userTO;
				isCurrentUser = ((ProfilePlace) placeCtrl.getWhere()).getUsername() == userTO.getUsername();
				display();
			}
		});
		this.setVisible(true);
	}

	private boolean validateFields() {
		ValidatorHelper validator = new ValidatorHelper();
		if (!validator.lengthValid(username.getText(), 3, 50)){
			fieldsToErrorLabels.get(username).getError().setText("Mínimo de 3 caracteres.");
		} else if (!validator.usernameValid(username.getText())){
			fieldsToErrorLabels.get(username).getError().setText("Campo inválido.");
		} else if (false){
			fieldsToErrorLabels.get(username).getError().setText("O nome de usuário já existe.");
		}

		if (!validator.emailValid(email.getText())){
			fieldsToErrorLabels.get(email).getError().setText("Email inválido.");
		} else if (false){
			fieldsToErrorLabels.get(email).getError().setText("O email já existe.");
		}

		if (!validator.passwordValid(password.getText())){
			fieldsToErrorLabels.get(password).getError().setText("Senha inválida.");
		}
		
		if(!validator.lengthValid(firstName.getText(), 2, 50)){
			fieldsToErrorLabels.get(firstName).getError().setText("Mínimo de 2 caracteres.");
		}
		
		if(!validator.lengthValid(lastName.getText(), 2, 50)){
			fieldsToErrorLabels.get(lastName).getError().setText("Mínimo de 2 caracteres.");
		}
		
		if(sex.getSelectedIndex() <= 0){
			fieldsToErrorLabels.get(sex).getError().setText("Escolha uma alternativa.");
		}
		
		if(!birthDate.isSelected()){
			fieldsToErrorLabels.get(birthDate).getError().setText("Insira sua data de nascimento.");
		}
		return checkErrors();
	}

	@UiHandler("lblEdit")
	void doEdit(ClickEvent e) {
		isEditMode = true;
		editPanel.setVisible(!isEditMode);
		displayFields();
	}

	@UiHandler("btnOK")
	void doOK(ClickEvent e) {
		clearErrors();
		if(validateFields()){
			isEditMode = false;
			editPanel.setVisible(!isEditMode);
		}
	}

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) {
		isEditMode = false;
		editPanel.setVisible(!isEditMode);
		clearErrors();
		displayFields();
	}

	private void clearErrors() {
		for (Field field : fieldsToErrorLabels.values()) {
			field.getError().setText("");
		}
	}

	private boolean checkErrors() {
		boolean errors = false;
		for (Field field : fieldsToErrorLabels.values()) {
			if(!"".equals(field.getError().getText())){
				errors = true;
			}
		}
		return errors;
	}
	
	private void showFields(){
		for (Field field : fieldsToErrorLabels.values()) {
			field.getField().setVisible(isEditMode);
			field.getError().setVisible(isEditMode);
			field.getValue().setVisible(!isEditMode);
		}
		passwordPanel.setVisible(isEditMode);
		passwordSeparator.setVisible(isEditMode);
		emailPanel.setVisible(isCurrentUser);
		emailSeparator.setVisible(isCurrentUser);
		sexPanel.setVisible(isCurrentUser);
		sexSeparator.setVisible(isCurrentUser);
		birthDatePanel.setVisible(isCurrentUser);
		birthDateSeparator.setVisible(isCurrentUser);
		
		imageExtraInfo.setVisible(isEditMode);
		usernameExtraInfo.setVisible(isEditMode);
		usernameExtraInfo2.setVisible(isEditMode);
		emailExtraInfo.setVisible(isEditMode);
		titleExtraInfo.setVisible(isEditMode);
		titleExtraInfo2.setVisible(isEditMode);
	}
	
	private void display() {
		isEditMode = true;
		isCurrentUser = true;
		// TODO i18n
		displayTitle();
		displayFields();
	}

	private void displayFields() {
		imgProfile.setUrl(IMAGE_PATH + "profilePic.png");
		if(isEditMode){
			username.setText(user.getUsername());
			password.setText(user.getUsername());
			email.setText(user.getPerson().getEmail());
			firstName.setText(user.getPerson().getFirstName());
			lastName.setText(user.getPerson().getLastName());
			company.setText(user.getPerson().getCompany());
			title.setText(user.getPerson().getTitle());
	        sex.addItem("");
	        sex.addItem("Feminino");
	        sex.addItem("Masculino");
	        sex.setSelectedIndex("F".equals(user.getPerson().getSex()) ? 1 : 2);
	        birthDatePickerPanel.clear();
			birthDate = new SimpleDatePicker(user.getPerson().getBirthDate());
			birthDatePickerPanel.add(birthDate);
		} else {
			usernameTxt.setText(user.getUsername());
			emailTxt.setText(user.getPerson().getEmail());
			firstNameTxt.setText(user.getPerson().getFirstName());
			lastNameTxt.setText(user.getPerson().getLastName());
			companyTxt.setText(user.getPerson().getCompany());
			titleTxt.setText(user.getPerson().getTitle());
			sexTxt.setText("F".equals(user.getPerson().getSex()) ? "Feminino" : "Masculino");
			birthDateTxt.setText(user.getPerson().getBirthDate().toString());
		}
		mapFieldsToErrorLabels();
	}

	private void displayTitle() {
		imgTitle.setUrl(IMAGE_PATH + "course.png");
		lblTitle.setText("Perfil");
		editPanel.setVisible(!isEditMode);
		lblEdit.setText("Editar");
	}

	private void mapFieldsToErrorLabels() {
		fieldsToErrorLabels = new HashMap<Widget, Field>();
		fieldsToErrorLabels.put(username, new Field(username, usernameError, usernameTxt));
		fieldsToErrorLabels.put(email, new Field(email, emailError, emailTxt));
		fieldsToErrorLabels.put(password, new Field(password, passwordError, passwordTxt));
		fieldsToErrorLabels.put(firstName, new Field(firstName, firstNameError, firstNameTxt));
		fieldsToErrorLabels.put(lastName, new Field(lastName, lastNameError, lastNameTxt));
		fieldsToErrorLabels.put(company, new Field(company, companyError, companyTxt));
		fieldsToErrorLabels.put(title, new Field(title, titleError, titleTxt));
		fieldsToErrorLabels.put(sex, new Field(sex, sexError, sexTxt));
		fieldsToErrorLabels.put(birthDate, new Field(birthDatePickerPanel, birthDateError, birthDateTxt));
		showFields();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		// TODO Auto-generated method stub
	}
	
	class Field{
		Widget field;
		Label error;
		Label value;
		public Field(Widget field, Label error, Label value) {
			this.field = field;
			this.error = error;
			this.value = value;
		}
		public Widget getField() {
			return field;
		}
		public Label getError() {
			return error;
		}
		public Label getValue() {
			return value;
		}
	}

}
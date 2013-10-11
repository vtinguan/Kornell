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
	private TextBox username, email, firstName, lastName, company, title;
	private PasswordTextBox password;
	ListBox sex;
	SimpleDatePicker birthDate;
	
	// TODO fix this
	private String IMAGE_PATH = "skins/first/icons/profile/";
	@UiField
	FlowPanel profileFields;
	@UiField
	FlowPanel titlePanel;
	@UiField
	Image imgTitle;
	@UiField
	Label lblTitle;
	@UiField
	FlowPanel editPanel;
	@UiField
	Label lblEdit;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;
	
	Map<Widget, Label> fieldsToErrorLabels;

	Label usernameError, emailError, passwordError, firstNameError, lastNameError, companyError, titleError, sexError, birthDateError;
	private UserInfoTO user;

	public GenericProfileView(EventBus bus, KornellClient client,
			final PlaceController placeCtrl) {
		this.bus = bus;
		this.client = client;
		this.placeCtrl = placeCtrl;
		usernameError = new Label();
		emailError = new Label();
		passwordError = new Label();
		firstNameError = new Label();
		lastNameError = new Label();
		companyError = new Label();
		titleError = new Label();
		sexError = new Label();
		birthDateError = new Label();
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
			fieldsToErrorLabels.get(username).setText("Mínimo de 3 caracteres.");
		} else if (!validator.usernameValid(username.getText())){
			fieldsToErrorLabels.get(username).setText("Campo inválido.");
		} else if (false){
			fieldsToErrorLabels.get(username).setText("O nome de usuário já existe.");
		}

		if (!validator.emailValid(email.getText())){
			fieldsToErrorLabels.get(email).setText("Email inválido.");
		} else if (false){
			fieldsToErrorLabels.get(email).setText("O email já existe.");
		}

		if (!validator.passwordValid(password.getText())){
			fieldsToErrorLabels.get(password).setText("Senha inválida.");
		}
		
		if(!validator.lengthValid(firstName.getText(), 3, 50)){
			fieldsToErrorLabels.get(firstName).setText("Mínimo de 3 caracteres.");
		}
		
		if(!validator.lengthValid(lastName.getText(), 3, 50)){
			fieldsToErrorLabels.get(lastName).setText("Mínimo de 3 caracteres.");
		}
		
		if(sex.getSelectedIndex() <= 0){
			fieldsToErrorLabels.get(sex).setText("Escolha uma alternativa.");
		}
		
		if(!birthDate.isSelected()){
			fieldsToErrorLabels.get(birthDate).setText("Insira sua data de nascimento.");
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
		for (Label errorLabel : fieldsToErrorLabels.values()) {
			errorLabel.setText("");
			errorLabel.setVisible(false);
		}
	}

	private boolean checkErrors() {
		boolean errors = false;
		for (Label errorLabel : fieldsToErrorLabels.values()) {
			if(!"".equals(errorLabel.getText())){
				errors = true;
				errorLabel.setVisible(true);
			}
		}
		return errors;
	}
	
	private void display() {
		isEditMode = true;
		isCurrentUser = true;
		// TODO i18n
		displayTitle();
		displayFields();
	}

	private void displayFields() {
		profileFields.clear();
		String mandatory = isEditMode ? " *" : "";

		List<String> labels = new ArrayList<String>();
		labels.add("Inclua aqui uma foto sua");

		List<String> extraInfoTitle = new ArrayList<String>();
		extraInfoTitle.add("Designação atribuída a você.");
		extraInfoTitle.add("Exemplo: Representante de Vendas");
		
		List<String> extraInfoUserName = new ArrayList<String>();
		extraInfoUserName.add("Designação atribuída a você.");
		extraInfoUserName.add("Exemplo: Representante de Vendas");

		List<String> extraInfoPassword = new ArrayList<String>();
		extraInfoPassword.add("Mínimo de 8 caracteres, com letras");
		extraInfoPassword.add("maiúsculas, minúsculas e números.");

		List<String> extraInfoEmail = new ArrayList<String>();
		extraInfoEmail.add("Insira um email válido.");
		

		displayImageField("Imagem do perfil", labels);
		displayField("Nome de usuário" + mandatory, extraInfoUserName,
				user.getUsername(), false, "username");
		displayField("Senha" + mandatory, extraInfoPassword,
				user.getUsername(), true, "password");
		displayField("Email" + mandatory, extraInfoEmail, user.getPerson().getEmail(),
				true, "email");
		displayField("Nome" + mandatory, null, user.getPerson().getFirstName(),
				false, "firstName");
		displayField("Sobrenome" + mandatory, null, user.getPerson()
				.getLastName(), false, "lastName");
		displayField("Empresa", null, user.getPerson().getCompany(), false, "company");
		displayField("Título", extraInfoTitle, user.getPerson().getTitle(),
				false, "title");
		displayField("Sexo" + mandatory, null, user.getPerson().getSex(), true, "sex");
		displayField("Nascimento" + mandatory, null, user.getPerson()
				.getBirthDate().toString(), true, "birthDate");
		
		mapFieldsToErrorLabels();
	}

	private void displayTitle() {
		imgTitle.setUrl(IMAGE_PATH + "course.png");
		lblTitle.setText("Perfil");
		editPanel.setVisible(!isEditMode);
		lblEdit.setText("Editar");
	}

	private void displayField(String labelName, List<String> extraInfo,
			String value, boolean isPrivate, String fieldType) {
		
		if(!isCurrentUser && isPrivate)
			return;

		FlowPanel fieldPanel = new FlowPanel();
		fieldPanel.addStyleName("fieldPanel");
		
		FlowPanel labelPanel = new FlowPanel();
		labelPanel.addStyleName("labelPanel");

		Label lblLabel = new Label(labelName);
		lblLabel.addStyleName("lblLabel");
		labelPanel.add(lblLabel);

		if (isEditMode && extraInfo != null) {
			labelPanel.addStyleName("labelPanel" + extraInfo.size());
			for (String extra : extraInfo) {
				Label lblExtraInfo = new Label(extra);
				lblExtraInfo.addStyleName("lblExtraInfo");
				labelPanel.add(lblExtraInfo);
			}
		}
		fieldPanel.add(labelPanel);
		
		fieldPanel.add(getValueWidget(value, fieldType));

		if(isCurrentUser && isPrivate){
			FlowPanel publicPanel = new FlowPanel();
			publicPanel.addStyleName("publicPanel");

			Image imgPublic = new Image(IMAGE_PATH + "notPublic.png");
			imgPublic.addStyleName("imgPublic");
			publicPanel.add(imgPublic);

			Label lblPublic = new Label("Privado");
			lblPublic.addStyleName("lblPublic");
			publicPanel.add(lblPublic);
			fieldPanel.add(publicPanel);
		}

		profileFields.add(fieldPanel);

		Image imgSeparator = new Image(IMAGE_PATH + "separatorBar.png");
		imgSeparator.addStyleName("fillWidth");
		imgSeparator.setHeight("2px");
		profileFields.add(imgSeparator);
	}

	private FlowPanel getValueWidget(String value, String fieldType) {
		FlowPanel fieldPanel = new FlowPanel();
		fieldPanel.addStyleName("fieldPanel");
		if(isEditMode){
			if("username".equals(fieldType)){
				username = new TextBox();
				username.setText(value);
				username.addStyleName("field");
				username.addStyleName("textField");
				fieldPanel.add(username);
				fieldPanel.add(usernameError);
			} else if("email".equals(fieldType)){
				email = new TextBox();
				email.setText(value);
				email.addStyleName("field");
				email.addStyleName("textField");
				fieldPanel.add(email);
				fieldPanel.add(emailError);
			} else if("password".equals(fieldType)){
				password = new PasswordTextBox();
				password.setText(value);
				password.addStyleName("field");
				password.addStyleName("textField");
				fieldPanel.add(password);
				fieldPanel.add(passwordError);
			} else if("firstName".equals(fieldType)){
				firstName = new TextBox();
				firstName.setText(value);
				firstName.addStyleName("field");
				firstName.addStyleName("textField");
				fieldPanel.add(firstName);
				fieldPanel.add(firstNameError);
			} else if("lastName".equals(fieldType)){
				lastName = new TextBox();
				lastName.setText(value);
				lastName.addStyleName("field");
				lastName.addStyleName("textField");
				fieldPanel.add(lastName);
				fieldPanel.add(lastNameError);
			} else if("company".equals(fieldType)){
				company = new TextBox();
				company.setText(value);
				company.addStyleName("field");
				company.addStyleName("textField");
				fieldPanel.add(company);
				fieldPanel.add(companyError);
			} else if("title".equals(fieldType)){
				title = new TextBox();
				title.setText(value);
				title.addStyleName("field");
				title.addStyleName("textField");
				fieldPanel.add(title);
				fieldPanel.add(titleError);
			} else if("sex".equals(fieldType)){
		        sex = new ListBox(false);
		        sex.addItem("");
		        sex.addItem("Feminino");
		        sex.addItem("Masculino");
		        sex.ensureDebugId("dropBoxDay");
		        sex.setWidth("120px");
				sex.addStyleName("field");
				sex.addStyleName("textField");
				fieldPanel.add(sex);
				fieldPanel.add(sexError);
			} else if("birthDate".equals(fieldType)){
				//value
				birthDate = new SimpleDatePicker();
				fieldPanel.add(birthDate);
				fieldPanel.add(birthDateError);
			} else {
				fieldPanel.add(new Label(""));
			}
		} else {
			Label lbl = new Label(value != null ? value : "");
			lbl.addStyleName(value != null ? "lblValue" : "lblValueAdd");
			fieldPanel.add(lbl);
		}
		return fieldPanel;
	}

	private void displayImageField(String labelName, List<String> extraInfo) {

		FlowPanel labelPanel = new FlowPanel();
		labelPanel.addStyleName("labelPanel");
		labelPanel.addStyleName("labelPanelImg");

		Label lblLabel = new Label(labelName);
		lblLabel.addStyleName("lblLabelImg");
		labelPanel.add(lblLabel);

		if (isEditMode && extraInfo != null) {
			for (String extra : extraInfo) {
				Label lblExtraInfo = new Label(extra);
				lblExtraInfo.addStyleName("lblExtraInfo");
				labelPanel.add(lblExtraInfo);
			}
		}

		FlowPanel imagePanel = new FlowPanel();
		imagePanel.addStyleName("imagePanel");

		Image img = new Image(IMAGE_PATH + "profilePic.png");
		img.addStyleName("imgPublic");
		imagePanel.add(img);

		FlowPanel fieldPanel = new FlowPanel();
		fieldPanel.addStyleName("fieldPanel");
		fieldPanel.addStyleName("fieldPanelImage");

		fieldPanel.add(labelPanel);
		fieldPanel.add(imagePanel);

		profileFields.add(fieldPanel);

		Image imgSeparator = new Image(IMAGE_PATH + "separatorBar.png");
		imgSeparator.addStyleName("fillWidth");
		profileFields.add(imgSeparator);
	}

	private void mapFieldsToErrorLabels() {
		fieldsToErrorLabels = new HashMap<Widget, Label>();
		
		usernameError.addStyleName("errorMessage");
		fieldsToErrorLabels.put(username, usernameError);
		
		emailError.addStyleName("errorMessage");
		fieldsToErrorLabels.put(email, emailError);

		passwordError.addStyleName("errorMessage");
		fieldsToErrorLabels.put(password, passwordError);
		
		firstNameError.addStyleName("errorMessage");
		fieldsToErrorLabels.put(firstName, firstNameError);
		
		lastNameError.addStyleName("errorMessage");
		fieldsToErrorLabels.put(lastName, lastNameError);

		companyError.addStyleName("errorMessage");
		fieldsToErrorLabels.put(company, companyError);
		
		titleError.addStyleName("errorMessage");
		fieldsToErrorLabels.put(title, titleError);
		
		sexError.addStyleName("errorMessage");
		fieldsToErrorLabels.put(sex, sexError);
		
		birthDateError.addStyleName("errorMessage");
		fieldsToErrorLabels.put(birthDate, birthDateError);
		
	}

	@Override
	public void setPresenter(Presenter presenter) {
		// TODO Auto-generated method stub

	}

}
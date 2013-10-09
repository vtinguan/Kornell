package kornell.gui.client.presentation.profile.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.profile.ProfileView;
import kornell.gui.client.presentation.util.SimpleDatePicker;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
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
	private TextBox username, email, firstName, lastName, company, title, sex, birthDate;
	
	
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
				isCurrentUser = ((ProfilePlace) placeCtrl.getWhere())
						.getUsername() == userTO.getUsername();
				display();
			}
		});
		lblEdit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				isEditMode = true;
				editPanel.setVisible(!isEditMode);
				displayFields();
			}
		});
		btnCancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				isEditMode = false;
				editPanel.setVisible(!isEditMode);
				displayFields();
			}
		});
		this.setVisible(true);
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
		extraInfoUserName.add("Sem espaços.");
		extraInfoUserName.add("Exemplo: nome.sobrenome");
		

		displayImageField("Imagem do perfil", labels);
		displayField("Nome de usuário" + mandatory, extraInfoUserName,
				user.getUsername(), true, "username");
		displayField("Email" + mandatory, null, user.getPerson().getEmail(),
				user.getPerson().isEmailPrivate(), "email");
		displayField("Nome" + mandatory, null, user.getPerson().getFirstName(),
				user.getPerson().isFirstNamePrivate(), "firstName");
		displayField("Sobrenome" + mandatory, null, user.getPerson()
				.getLastName(), user.getPerson().isLastNamePrivate(), "lastName");
		displayField("Empresa", null, user.getPerson().getCompany(), user
				.getPerson().isCompanyPrivate(), "company");
		displayField("Título", extraInfoTitle, user.getPerson().getTitle(),
				user.getPerson().isTitlePrivate(), "title");
		displayField("Sexo" + mandatory, null, user.getPerson().getSex(), user
				.getPerson().isSexPrivate(), "sex");
		displayField("Nascimento" + mandatory, null, user.getPerson()
				.getBirthDate().toString(), user.getPerson()
				.isBirthDatePrivate(), "birthDate");
	}

	private void displayTitle() {
		imgTitle.setUrl(IMAGE_PATH + "course.png");
		lblTitle.setText("Perfil");
		editPanel.setVisible(!isEditMode);
		lblEdit.setText("Editar");
	}

	private void displayField(String labelName, List<String> extraInfo,
			String value, boolean isPublic, String fieldType) {
		
		if(!isCurrentUser && !isPublic)
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

		if(isCurrentUser){
			FlowPanel publicPanel = new FlowPanel();
			publicPanel.addStyleName("publicPanel");

			Image imgPublic = new Image(IMAGE_PATH
					+ (isPublic ? "public.png" : "notPublic.png"));
			imgPublic.addStyleName("imgPublic");
			publicPanel.add(imgPublic);

			Label lblPublic = new Label(isPublic ? "Público" : "Privado");
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
			} else if("email".equals(fieldType)){
				email = new TextBox();
				email.setText(value);
				email.addStyleName("field");
				email.addStyleName("textField");
				fieldPanel.add(email);
			} else if("firstName".equals(fieldType)){
				firstName = new TextBox();
				firstName.setText(value);
				firstName.addStyleName("field");
				firstName.addStyleName("textField");
				fieldPanel.add(firstName);
			} else if("lastName".equals(fieldType)){
				lastName = new TextBox();
				lastName.setText(value);
				lastName.addStyleName("field");
				lastName.addStyleName("textField");
				fieldPanel.add(lastName);
			} else if("company".equals(fieldType)){
				company = new TextBox();
				company.setText(value);
				company.addStyleName("field");
				company.addStyleName("textField");
				fieldPanel.add(company);
			} else if("title".equals(fieldType)){
				title = new TextBox();
				title.setText(value);
				title.addStyleName("field");
				title.addStyleName("textField");
				fieldPanel.add(title);
			} else if("sex".equals(fieldType)){
				sex = new TextBox();
				sex.setText(value);
				sex.addStyleName("field");
				sex.addStyleName("textField");
				fieldPanel.add(sex);
			} else if("birthDate".equals(fieldType)){
				birthDate = new TextBox();
				birthDate.setText(value);
				birthDate.addStyleName("field");
				birthDate.addStyleName("textField");
				fieldPanel.add(new SimpleDatePicker());
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

	@Override
	public void setPresenter(Presenter presenter) {
		// TODO Auto-generated method stub

	}

}
package kornell.gui.client.presentation.profile.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.profile.ProfileView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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
	// TODO fix this
	private String IMAGE_PATH = "skins/first/icons/profile/";
	@UiField
	FlowPanel profileFields;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;
	
	private UserInfoTO user;

	public GenericProfileView(EventBus bus, KornellClient client, PlaceController placeCtrl) {
		this.bus = bus;
		this.client = client;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		initData();
		//i18n
		btnOK.setText("OK".toUpperCase());
		btnCancel.setText("Cancelar".toUpperCase());
	}

	private void initData() {

		client.getCurrentUser(new Callback<UserInfoTO>() {
			@Override
			protected void ok(UserInfoTO userTO) {
				user = userTO;
				display(false);
			}
		});
		this.setVisible(true);
	}

	private void display(boolean isEditMode) {
		profileFields.clear();
		// TODO i18n
		displayTitle();
		displayFields(isEditMode);
	}

	private void displayFields(boolean isEditMode) {
		List<String> labels = new ArrayList<String>();
		labels.add("Inclua aqui uma foto sua");

		List<String> extraInfoTitle = new ArrayList<String>();
		extraInfoTitle.add("Designação atribuída a você.");
		extraInfoTitle.add("Exemplo: Representante de Vendas");

		List<String> extraInfoUserName = new ArrayList<String>();
		extraInfoUserName.add("Sem espaços.");
		extraInfoUserName.add("Exemplo: nome.sobrenome");
		
		displayImageField("Imagem do perfil", labels);
		displayField("Nome de usuário", extraInfoUserName, user.getPerson().getFullName().replace(' ', '.'), true, isEditMode);
		displayField("Email", null, "carla@jacinto.com", false, isEditMode);
		displayField("Nome", null, user.getPerson().getFullName(), true, isEditMode);
		displayField("Empresa", null, "ARCOM", true, isEditMode);
		displayField("Título", extraInfoTitle,"Representante de Vendas", true, isEditMode);
		displayField("Sexo", null, "Feminino", true, isEditMode);
		displayField("Nascimento", null, "12/34/5678", false, isEditMode);
	}

	private void displayTitle() {
		Image imgTitle = new Image(IMAGE_PATH + "course.png");
		imgTitle.addStyleName("imgTitle");

		Label lblTitle = new Label("Perfil");
		lblTitle.addStyleName("lblTitle");

		FlowPanel titlePanel = new FlowPanel();
		titlePanel.addStyleName("titlePanel");
		titlePanel.add(imgTitle);
		titlePanel.add(lblTitle);
		
		Label editLabel = new Label("Editar");
		editLabel.addStyleName("editLabel");
		titlePanel.add(editLabel);

		profileFields.add(titlePanel);
	}

	private void displayField(String labelName, List<String> extraInfo,
			String value, boolean isPublic, boolean isEditMode) {

		FlowPanel labelPanel = new FlowPanel();
		labelPanel.addStyleName("labelPanel");

		Label lblLabel = new Label(labelName);
		lblLabel.addStyleName("lblLabel");
		labelPanel.add(lblLabel);

		if (!isEditMode && extraInfo != null) {
			labelPanel.addStyleName("labelPanel" + extraInfo.size());
			for (String extra : extraInfo) {
				Label lblExtraInfo = new Label(extra);
				lblExtraInfo.addStyleName("lblExtraInfo");
				labelPanel.add(lblExtraInfo);
			}
		}
		Label lblValue = new Label(value != null ? value : "Adicionar");
		lblValue.addStyleName(value != null ? "lblValue" : "lblValueAdd");

		FlowPanel publicPanel = new FlowPanel();
		publicPanel.addStyleName("publicPanel");

		Image imgPublic = new Image(IMAGE_PATH
				+ (isPublic ? "public.png" : "notPublic.png"));
		imgPublic.addStyleName("imgPublic");
		publicPanel.add(imgPublic);

		Label lblPublic = new Label(isPublic ? "Público"
				: "Privado");
		lblPublic.addStyleName("lblPublic");
		publicPanel.add(lblPublic);

		FlowPanel fieldPanel = new FlowPanel();
		fieldPanel.addStyleName("fieldPanel");

		fieldPanel.add(labelPanel);
		fieldPanel.add(lblValue);
		fieldPanel.add(publicPanel);

		profileFields.add(fieldPanel);

		Image imgSeparator = new Image(IMAGE_PATH + "separatorBar.png");
		imgSeparator.addStyleName("fillWidth");
		profileFields.add(imgSeparator);
	}

	private void displayImageField(String labelName, List<String> extraInfo) {

		FlowPanel labelPanel = new FlowPanel();
		labelPanel.addStyleName("labelPanel");
		labelPanel.addStyleName("labelPanelImg");

		Label lblLabel = new Label(labelName);
		lblLabel.addStyleName("lblLabelImg");
		labelPanel.add(lblLabel);

		for (String extra : extraInfo) {
			Label lblExtraInfo = new Label(extra);
			lblExtraInfo.addStyleName("lblExtraInfo");
			labelPanel.add(lblExtraInfo);
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
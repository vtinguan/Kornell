package kornell.gui.client.presentation.welcome.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.KornellClient;
import kornell.gui.client.KornellConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;


public class GenericWelcomeProfileView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericWelcomeProfileView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	
	private KornellClient client;
	private PlaceController placeCtrl;
	private final EventBus eventBus = new SimpleEventBus();
	private KornellConstants constants = GWT.create(KornellConstants.class);
	// TODO fix this
	private String IMAGE_PATH = "skins/first/icons/profile/";
	@UiField
	FlowPanel profileFields;
	
	
	public GenericWelcomeProfileView(KornellClient client, PlaceController placeCtrl) {
		this.client = client;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		initData();		
	}
	
	private void initData() {
		display();
		this.setVisible(true);
	}


	private void display() {
		//TODO i18n
		displayTitle();		
		
		List<String> labels = new ArrayList<String>();
		labels.add("Inclua aqui uma foto sua");
		displayImageField("Imagem do Perfil", labels);
		
		List<String> extraInfo = new ArrayList<String>();
		displayField("Nome", extraInfo, "Carla Jacinto Lux", true);
		
		List<String> extraInfoTitle = new ArrayList<String>();
		extraInfoTitle.add("Designação atribuída a você.");
		extraInfoTitle.add("Exemplo: Representante de Vendas");
		displayField("Título", extraInfoTitle, "Representante de Vendas do ARCOM", true);
		
		displayField("Sexo", extraInfo, "Feminino", true);
		
		displayField("Nascimento", extraInfo, null, false);
		
		displayField("Idioma", extraInfo, "Português Brasil", true);
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
		
		profileFields.add(titlePanel);
	}

	private void displayField(String labelName, List<String> extraInfo, String value, boolean isPublic) {
		
		FlowPanel labelPanel = new FlowPanel();
		labelPanel.addStyleName("labelPanel");
		labelPanel.addStyleName("labelPanel"+extraInfo.size());
		
		Label lblLabel = new Label(labelName);
		lblLabel.addStyleName("lblLabel");
		labelPanel.add(lblLabel);
		
		for (String extra : extraInfo) {
			Label lblExtraInfo = new Label(extra);
			lblExtraInfo.addStyleName("lblExtraInfo");
			labelPanel.add(lblExtraInfo);
		}
		Label lblValue = new Label(value != null ? value : "Adicionar");
		lblValue.addStyleName(value != null ? "lblValue" : "lblValueAdd");

		FlowPanel publicPanel = new FlowPanel();
		publicPanel.addStyleName("publicPanel");
		
		Image imgPublic = new Image(IMAGE_PATH + (isPublic ? "public.png" : "notPublic.png" ));
		imgPublic.addStyleName("imgPublic");
		publicPanel.add(imgPublic);
		
		Label lblPublic = new Label(isPublic ? "Informação pública" : "Informação não divulgada");
		lblPublic.addStyleName("lblPublic");
		publicPanel.add(lblPublic);
		
		FlowPanel fieldPanel = new FlowPanel();
		fieldPanel.addStyleName("fieldPanel");
		
		fieldPanel.add(labelPanel);
		fieldPanel.add(lblValue);
		fieldPanel.add(publicPanel);
		
		Image imgSeparator = new Image(IMAGE_PATH + "separatorBar.png");
		profileFields.add(imgSeparator);
		
		profileFields.add(fieldPanel);
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
		
		fieldPanel.add(labelPanel);
		fieldPanel.add(imagePanel);
		
		profileFields.add(fieldPanel);
	}


}
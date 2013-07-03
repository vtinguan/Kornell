package kornell.gui.client.presentation.course.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.data.CoursesTO;
import kornell.gui.client.KornellConstants;

import com.github.gwtbootstrap.client.ui.Button;
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


public class GenericCourseDetailsView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseDetailsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	
	private KornellClient client;
	private PlaceController placeCtrl;
	private final EventBus eventBus = new SimpleEventBus();
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private String IMAGES_PATH = "skins/first/icons/courseDetails/";
	
	@UiField
	FlowPanel detailsPanel;
	
	public GenericCourseDetailsView(KornellClient client, PlaceController placeCtrl) {
		this.client = client;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		initData();		
	}
	
	private void initData() {
		client.getCourses(new Callback<CoursesTO>() {
			@Override
			protected void ok(CoursesTO to) {
				display();
			}
		});
	}


	private void display() {
		//TODO i18n
		displayTitle();
		displayButtons();
		displayInfos();
		displayHints();
	}

	private void displayTitle() {
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.addStyleName("titlePanel");
		
		Image titleImage = new Image(IMAGES_PATH + "details.png");
		titleImage.addStyleName("titleImage");
		titlePanel.add(titleImage);
		
		Label titleLabel = new Label("Detalhes do curso: ");
		titleLabel.addStyleName("titleLabel");
		titlePanel.add(titleLabel);
		
		//TODO getcoursename
		Label courseNameLabel = new Label("Suplementação Alimentar");
		courseNameLabel.addStyleName("courseNameLabel");
		titlePanel.add(courseNameLabel);
		
		detailsPanel.add(titlePanel);
	}

	private void displayInfos() {
		// TODO get info
		FlowPanel infoPanel = new FlowPanel();
		infoPanel.addStyleName("infoPanel");

		infoPanel.add(displayInfo("Apresentação", "A alimentação é de fundamental importância para o ser humano, principalmente para um bom desempenho em qualquer modalidade esportiva. Por isso deve ser balanceada e completa, fornecendo todos os nutrientes necessários ao organismo para que ele realize suas funções de crescimento, reparo e manutenção dos tecidos e, além disso, produza energia."));
		infoPanel.add(displayInfo("Objetivos", "Você verá essencialmente os processos pelos quais os organismos vivos recebem e utilizam os materiais (alimentos) necessários para a manutenção de suas funções e para o crescimento e renovação de seus componentes."));
		infoPanel.add(displayInfo("Público-alvo", "Representantes de Vendas"));

		detailsPanel.add(infoPanel);
	}

	private FlowPanel displayInfo(String title, String text) {
		FlowPanel info = new FlowPanel();
		info.addStyleName("infoDetails");
		
		Label infoTitle = new Label(title);
		infoTitle.addStyleName("infoTitle");
		info.add(infoTitle);
		
		Label infoText = new Label(text);
		infoText.addStyleName("infoText");
		info.add(infoText);
		
		return info;
	}

	private void displayButtons() {
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.addStyleName("buttonsPanel");

		buttonsPanel.add(displayButton("Sobre o curso", "Visão geral", true));
		buttonsPanel.add(displayButton("Tópicos", "Principais pontos abordados neste curso"));
		buttonsPanel.add(displayButton("Certificação", "Avaliações e testes"));

		detailsPanel.add(buttonsPanel);
	}

	private Button displayButton(String title, String label) {
		return displayButton(title, label, false);
	}

	private Button displayButton(String title, String label, boolean selected) {
		Button btn = new Button();
		btn.addStyleName("btnDetails");
		btn.addStyleName(selected ? "btnSelected" : "btnNotSelected");
		btn.removeStyleName("btn");
		
		Label btnTitle = new Label(title);
		btnTitle.addStyleName("btnTitle");
		btn.add(btnTitle);
		
		Label btnLabel = new Label(label);
		btnLabel.addStyleName("btnLabel");
		btn.add(btnLabel);
		
		return btn;
	}

	private void displayHints() {
		// TODO get info
		FlowPanel hintsPanel = new FlowPanel();
		hintsPanel.addStyleName("hintsPanel");

		hintsPanel.add(displayHint("time.png", "Carga de estudo: 30 minutos por dia. Tempo total: 3 horas."));
		hintsPanel.add(displayHint("forum.png", "Este curso contém fórum de atividades. Participe!"));
		hintsPanel.add(displayHint("chat.png", "O chat é livre para todos os participantes que estiverem on-line."));
		hintsPanel.add(displayHint("library.png", "Material complementar disponível na biblioteca."));

		detailsPanel.add(hintsPanel);
	}

	private FlowPanel displayHint(String img, String hintText) {
		FlowPanel hint = new FlowPanel();
		hint.addStyleName("hintDetails");
		
		Image hintImg = new Image(IMAGES_PATH + img);
		hintImg.addStyleName("hintImg");
		hint.add(hintImg);
		
		Label lblHintText = new Label(hintText);
		lblHintText.addStyleName("hintText");
		hint.add(lblHintText);
		
		return hint;
	}


}
package kornell.gui.client.presentation.course.generic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.data.CoursesTO;
import kornell.gui.client.KornellConstants;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	@UiField
	FlowPanel titlePanel;
	@UiField
	FlowPanel buttonsPanel;
	@UiField
	FlowPanel contentPanel;

	@UiField
	Button btnAbout;
	@UiField
	Button btnTopics;
	@UiField
	Button btnCertification;
	Button btnCurrent;
	

	TopicsTO topicsTO;
	
	public GenericCourseDetailsView(KornellClient client, PlaceController placeCtrl) {
		this.client = client;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		initData();		
	}
	
	private void initData() {
		/*client.getCourses(new Callback<CoursesTO>() {
			@Override
			protected void ok(CoursesTO to) {
			}
		});*/
		// TODO get info	
		topicsTO = getTopicsTO();
		display();
	}


	private void display() {
		btnCurrent = btnAbout;
		//TODO i18n
		displayTitle();
		displayButtons();
		displayContent(btnCurrent);
	}

	private void displayContent(Button btn) {
		contentPanel.clear();
		if(btn.equals(btnAbout)){
			contentPanel.add(getInfosPanel());
			contentPanel.add(getHintsPanel());
		} else if(btn.equals(btnTopics)){
			contentPanel.add(getTopicsPanel());
		} else if(btn.equals(btnCertification)) {
			contentPanel.add(getCertificationPanel());
		}
	}

	private FlowPanel getTopicsPanel() {
		FlowPanel topicsPanel = new FlowPanel();
		topicsPanel.addStyleName("topicsPanel");

		topicsPanel.add(getTopicsTableHeader());
		topicsPanel.add(getTopicsTableContent());
		
		return topicsPanel;
	}

	private FlowPanel getTopicsTableContent() {
		FlowPanel topicsContentPanel = new FlowPanel();
		topicsContentPanel.addStyleName("topicsContentPanel");
		
		for (TopicTO topicTO : topicsTO.getTopics()) {
			topicsContentPanel.add(getTopicWrapper(topicTO));
		}

		return topicsContentPanel;
	}

	private FlowPanel getTopicWrapper(TopicTO topicTO) {
		FlowPanel topicWrapper = new FlowPanel();
		topicWrapper.addStyleName("topicWrapper");
		
		FlowPanel topicPanel = new FlowPanel();
		topicPanel.addStyleName("topicPanel");
		
		Image topicIcon = new Image(IMAGES_PATH + "status_" + topicTO.getType() + ".png");
		topicIcon.addStyleName("topicIcon");
		topicPanel.add(topicIcon);
		
		Label lblTopic = new Label(topicTO.getTopic());
		lblTopic.addStyleName("lblTopic");
		topicPanel.add(lblTopic);
		
		topicWrapper.add(topicPanel);
		
		Label lblStatus = new Label(topicTO.getStatus());
		lblStatus.addStyleName("finishedTest".equals(topicTO.getType()) ? "lblStatusFinishedTest" : "lblStatus");
		topicWrapper.add(lblStatus);
		
		Label lblTime = new Label("".equals(topicTO.getTime()) ? "-" : topicTO.getTime());
		lblTime.addStyleName("lblTime");
		topicWrapper.add(lblTime);
		
		FlowPanel pnlForumComments = new FlowPanel();
		pnlForumComments.addStyleName("pnlForumComments");
		
		Label lblForumComments = new Label(topicTO.getForumComments() == 0 ? "-" : ""+topicTO.getForumComments());
		lblForumComments.addStyleName("lblForumComments");
		pnlForumComments.add(lblForumComments);
		
		if(topicTO.getNewForumComments() > 0){
			Label lblDividingBar = new Label("|");
			lblDividingBar.addStyleName("lblDividingBar");
			pnlForumComments.add(lblDividingBar);

			Label lblNew = new Label("Novos:");
			lblNew.addStyleName("lblNew");
			pnlForumComments.add(lblNew);

			Label lblNewForumComments = new Label(""+topicTO.getNewForumComments());
			lblNewForumComments.addStyleName("lblNewForumComments");
			pnlForumComments.add(lblNewForumComments);
		}
		
		topicWrapper.add(pnlForumComments);
		
		Label lblNotes = new Label(topicTO.isNotes() ? "Ver" : "-");
		lblNotes.addStyleName("lblNotes");
		topicWrapper.add(lblNotes);
		
		return topicWrapper;
	}

	private FlowPanel getTopicsTableHeader() {
		FlowPanel topicsHeaderPanel = new FlowPanel(); 
		topicsHeaderPanel.addStyleName("topicsHeaderPanel");

		topicsHeaderPanel.add(getHeaderButton("Tópicos", "btnTopics"));
		topicsHeaderPanel.add(getHeaderButton("Status", "btnStatus"));
		topicsHeaderPanel.add(getHeaderButton("Tempo", "btnTime"));
		topicsHeaderPanel.add(getHeaderButton("Comentários no Fórum", "btnForumComments"));
		topicsHeaderPanel.add(getHeaderButton("Anotações", "btnNotes"));
		
		return topicsHeaderPanel;
	}

	private Button getHeaderButton(String label, String styleName) {
		Button btn = new Button(label);
		btn.removeStyleName("btn");
		btn.addStyleName("btnTopicsHeader"); 
		btn.addStyleName(styleName);
		btn.addStyleName("btnNotSelected");
		return btn;
	}

	private Widget getCertificationPanel() {
		return new FlowPanel();
	}

	private void displayTitle() {		
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
	}

	private FlowPanel getInfosPanel() {
		// TODO get info
		FlowPanel infoPanel = new FlowPanel();
		infoPanel.addStyleName("infoPanel");

		infoPanel.add(getInfoPanel("Apresentação", "A alimentação é de fundamental importância para o ser humano, principalmente para um bom desempenho em qualquer modalidade esportiva. Por isso deve ser balanceada e completa, fornecendo todos os nutrientes necessários ao organismo para que ele realize suas funções de crescimento, reparo e manutenção dos tecidos e, além disso, produza energia."));
		infoPanel.add(getInfoPanel("Objetivos", "Você verá essencialmente os processos pelos quais os organismos vivos recebem e utilizam os materiais (alimentos) necessários para a manutenção de suas funções e para o crescimento e renovação de seus componentes."));
		infoPanel.add(getInfoPanel("Público-alvo", "Representantes de Vendas"));

		return infoPanel;
	}

	private FlowPanel getInfoPanel(String title, String text) {
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
		//TODO i18n
		displayButton(btnAbout, "Sobre o curso", "Visão geral");
		displayButton(btnTopics, "Tópicos", "Principais pontos abordados neste curso");
		displayButton(btnCertification, "Certificação", "Avaliações e testes");
	}

	private void displayButton(Button btn, String title, String label) {
		btn.removeStyleName("btn");
		
		Label btnTitle = new Label(title);
		btnTitle.addStyleName("btnTitle");
		btn.add(btnTitle);
		
		Label btnLabel = new Label(label);
		btnLabel.addStyleName("btnLabel");
		btn.add(btnLabel);

		btn.addClickHandler(new DetailsButtonClickHandler());
	}

	private FlowPanel getHintsPanel() {
		// TODO get info
		FlowPanel hintsPanel = new FlowPanel();
		hintsPanel.addStyleName("hintsPanel");

		hintsPanel.add(getHintPanel("time.png", "Carga de estudo: 30 minutos por dia. Tempo total: 3 horas."));
		hintsPanel.add(getHintPanel("forum.png", "Este curso contém fórum de atividades. Participe!"));
		hintsPanel.add(getHintPanel("chat.png", "O chat é livre para todos os participantes que estiverem on-line."));
		hintsPanel.add(getHintPanel("library.png", "Material complementar disponível na biblioteca."));

		return hintsPanel;
	}

	private FlowPanel getHintPanel(String img, String hintText) {
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

	private void handleEvent(Button btn) {		
		btnCurrent.removeStyleName("btnSelected");
		btnCurrent.addStyleName("btnNotSelected");
		btn.addStyleName("btnSelected");
		btn.removeStyleName("btnNotSelected");
		
		displayContent(btn);
		btnCurrent = btn;
	}

	private final class DetailsButtonClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			handleEvent((Button) event.getSource());
		}
	}

	private TopicsTO getTopicsTO() {
		List<TopicTO> topics = new ArrayList<TopicTO>();
		topics.add(new TopicTO(1,"Abertura","Concluído","06:53",300,2,true,"finished"));
		topics.add(new TopicTO(2,"Saúde","Concluído","05:07",0,0,false,"finished"));
		topics.add(new TopicTO(3,"Avaliação (Pré-Teste)","30 pontos","12:03",0,0,false,"finishedTest"));
		topics.add(new TopicTO(4,"Sobre a Craftware","Em andamento","00:40",50,1,false,"current"));
		topics.add(new TopicTO(5,"Nutrição","A iniciar","",0,0,false,"toStart"));
		topics.add(new TopicTO(6,"A Suplementação Alimentar no Brasil","A iniciar","",3,0,true,"toStart"));
		topics.add(new TopicTO(7,"Os Suplementos Alimentares","A iniciar","",3,0,false,"toStart"));
		topics.add(new TopicTO(8,"Água","A iniciar","",0,0,false,"toStart"));
		topics.add(new TopicTO(9,"Carboidratos","A iniciar","",7,3,false,"toStart"));
		topics.add(new TopicTO(10,"Lipídios","A iniciar","",30,1,true,"toStart"));
		topics.add(new TopicTO(11,"Proteínas","A iniciar","",0,0,false,"toStart"));
		topics.add(new TopicTO(12,"Energia","A iniciar","",0,0,false,"toStart"));
		topics.add(new TopicTO(13,"Whey","A iniciar","",3,0,false,"toStart"));
		topics.add(new TopicTO(14,"Avaliação (Pós-Teste)","A iniciar","",1,1,true,"toStartTest"));
		
		return new TopicsTO(topics);
	}
}

class TopicsTO{
	List<TopicTO> topics;
	public TopicsTO(List<TopicTO> topics) {
		super();
		this.topics = topics;
	}
	public List<TopicTO> getTopics() {
		return topics;
	}
	public void setTopics(List<TopicTO> topics) {
		this.topics = topics;
	}
}
class TopicTO {
	Integer order;
	String topic;
	String status;
	String time;
	Integer forumComments;
	Integer newForumComments;
	boolean notes;
	String type;
	
	public TopicTO(Integer order, String topic, String status, String time, Integer forumComments, Integer newForumComments, boolean notes, String type) {
		super();
		this.order = order;
		this.topic = topic;
		this.status = status;
		this.time = time;
		this.forumComments = forumComments;
		this.newForumComments = newForumComments;
		this.notes = notes;
		this.type = type;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Integer getForumComments() {
		return forumComments;
	}
	public void setForumComments(Integer forumComments) {
		this.forumComments = forumComments;
	}
	public Integer getNewForumComments() {
		return newForumComments;
	}
	public void setNewForumComments(Integer newForumComments) {
		this.newForumComments = newForumComments;
	}
	public boolean isNotes() {
		return notes;
	}
	public void setNotes(boolean notes) {
		this.notes = notes;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
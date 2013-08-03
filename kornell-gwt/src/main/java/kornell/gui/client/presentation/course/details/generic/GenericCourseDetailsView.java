package kornell.gui.client.presentation.course.details.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.KornellClient;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.course.details.CourseDetailsView;

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


public class GenericCourseDetailsView extends Composite  implements CourseDetailsView {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseDetailsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	
	private KornellClient client;
	private PlaceController placeCtrl;
	private EventBus bus;
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
	CertificationsTO certificationsTO;
	
	public GenericCourseDetailsView(EventBus eventBus, KornellClient client, PlaceController placeCtrl) {
		this.bus = eventBus;
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
		certificationsTO = getCertificationsTO();
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

	private FlowPanel getCertificationPanel() {
		FlowPanel certificationPanel = new FlowPanel();
		certificationPanel.addStyleName("certificationPanel");

		certificationPanel.add(getCertificationInfo());
		certificationPanel.add(getCertificationTableHeader());
		certificationPanel.add(getCertificationTableContent());
		
		return certificationPanel;
	}

	private FlowPanel getCertificationInfo() {
		FlowPanel certificationInfo = new FlowPanel();
		certificationInfo.addStyleName("certificationInfo");
		
		Label infoTitle = new Label("Certificação");
		infoTitle.addStyleName("certificationInfoTitle");
		certificationInfo.add(infoTitle);
		
		Label infoText = new Label("Confira abaixo o status dos testes e avaliações presentes neste curso. Seu certificado pode ser impresso por aqui caso você tenha concluído 100% do conteúdo do curso e ter sido aprovado na avaliação final.");
		infoText.addStyleName("certificationInfoText");
		certificationInfo.add(infoText);
		
		return certificationInfo;
	}

	private FlowPanel getCertificationTableContent() {
		FlowPanel certificationContentPanel = new FlowPanel();
		certificationContentPanel.addStyleName("certificationContentPanel");
		
		for (CertificationTO certificationTO : certificationsTO.getCertifications()) {
			certificationContentPanel.add(getCertificationWrapper(certificationTO));
		}

		return certificationContentPanel;
	}

	private FlowPanel getCertificationWrapper(CertificationTO certificationTO) {
		FlowPanel certificationWrapper = new FlowPanel();
		certificationWrapper.addStyleName("certificationWrapper");

		FlowPanel itemPanel = new FlowPanel();
		itemPanel.addStyleName("itemPanel");
		
		Image certificationIcon = new Image(IMAGES_PATH + "status_" + certificationTO.getType() + ".png");
		certificationIcon.addStyleName("certificationIcon");
		itemPanel.add(certificationIcon);
		
		Label lblName = new Label(certificationTO.getName());
		lblName.addStyleName("lblName");
		itemPanel.add(lblName);
		
		Label lblDescription = new Label(certificationTO.getDescription());
		lblDescription.addStyleName("lblDescription");
		itemPanel.add(lblDescription);
		
		certificationWrapper.add(itemPanel);
		
		Label lblStatus = new Label(certificationTO.getStatus());
		lblStatus.addStyleName("lblStatus");
		certificationWrapper.add(lblStatus);
		
		Label lblGrade = new Label(certificationTO.getGrade() == null ? " " : (!"".equals(certificationTO.getGrade()) ? certificationTO.getGrade() : "-"));
		lblGrade.addStyleName("lblGrade");
		certificationWrapper.add(lblGrade);
		
		Label lblActions = new Label(!"".equals(certificationTO.getActions()) ? certificationTO.getActions() : "-");
		lblActions.addStyleName("lblActions");
		certificationWrapper.add(lblActions);
		
		return certificationWrapper;
	}

	private FlowPanel getCertificationTableHeader() {
		FlowPanel certificationHeaderPanel = new FlowPanel(); 
		certificationHeaderPanel.addStyleName("certificationHeaderPanel");

		certificationHeaderPanel.add(getHeaderButton("Item", "btnItem", "btnCertificationHeader"));
		certificationHeaderPanel.add(getHeaderButton("Status", "btnStatus", "btnCertificationHeader"));
		certificationHeaderPanel.add(getHeaderButton("Nota", "btnGrade", "btnCertificationHeader"));
		certificationHeaderPanel.add(getHeaderButton("Ações", "btnActions", "btnCertificationHeader"));
		
		return certificationHeaderPanel;
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

		topicsHeaderPanel.add(getHeaderButton("Tópicos", "btnTopics", "btnTopicsHeader"));
		topicsHeaderPanel.add(getHeaderButton("Status", "btnStatus", "btnTopicsHeader"));
		topicsHeaderPanel.add(getHeaderButton("Tempo", "btnTime", "btnTopicsHeader"));
		topicsHeaderPanel.add(getHeaderButton("Comentários no Fórum", "btnForumComments", "btnTopicsHeader"));
		topicsHeaderPanel.add(getHeaderButton("Anotações", "btnNotes", "btnTopicsHeader"));
		
		return topicsHeaderPanel;
	}

	private Button getHeaderButton(String label, String styleName, String styleNameGlobal) {
		Button btn = new Button(label);
		btn.removeStyleName("btn");
		btn.addStyleName(styleNameGlobal); 
		btn.addStyleName(styleName);
		btn.addStyleName("btnNotSelected");
		return btn;
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

	private CertificationsTO getCertificationsTO() {
		List<CertificationTO> certifications = new ArrayList<CertificationTO>();
		certifications.add(new CertificationTO("finishedTest", "Pré-teste", "Esta avaliação tem a intenção de identificar o seu conhecimento referente ao tema do curso."
				+" A diferença da nota do pré-teste com o pós-teste (avaliação final) serve para te mostrar o ganho de conhecimento que você terá obtido ao final do curso",
				"Concluído", "30", "Visualizar"));
		certifications.add(new CertificationTO("finishedTest", "Pós-teste", "Esta avaliação final tem a intenção de identificar o seu conhecimento após a conclusão do curso.",
				"A fazer", "", ""));
		certifications.add(new CertificationTO("certification", "Certificado", "Esta avaliação tem a intenção de identificar o seu conhecimento referente ao tema do curso."
				+" A diferença da nota do pré-teste com o pós-teste (avaliação final) serve para te mostrar o ganho de conhecimento que você terá obtido ao final do curso",
				"Indisponível", null, ""));
		
		return new CertificationsTO(certifications);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		// TODO Auto-generated method stub
		
	}
}

class CertificationsTO{
	List<CertificationTO> certifications;
	public CertificationsTO(List<CertificationTO> certifications) {
		super();
		this.certifications = certifications;
	}
	public List<CertificationTO> getCertifications() {
		return certifications;
	}
	public void setTopics(List<CertificationTO> certifications) {
		this.certifications = certifications;
	}
}
class CertificationTO {
	String type;
	String name;
	String description;
	String status;
	String grade;
	String actions;
	public CertificationTO(String type, String name, String description, String status, String grade, String actions) {
		super();
		this.type = type;
		this.name = name;
		this.description = description;
		this.status = status;
		this.grade = grade;
		this.actions = actions;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getActions() {
		return actions;
	}
	public void setActions(String actions) {
		this.actions = actions;
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
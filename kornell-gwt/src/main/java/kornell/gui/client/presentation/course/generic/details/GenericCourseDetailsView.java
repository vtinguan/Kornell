package kornell.gui.client.presentation.course.generic.details;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.KornellSession;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.lom.Actom;
import kornell.core.lom.Content;
import kornell.core.lom.ContentFormat;
import kornell.core.lom.Contents;
import kornell.core.lom.ContentsCategory;
import kornell.core.lom.ExternalPage;
import kornell.core.to.CourseClassTO;
import kornell.core.to.UserInfoTO;
import kornell.core.to.coursedetails.CourseDetailsTO;
import kornell.core.to.coursedetails.HintTO;
import kornell.core.to.coursedetails.InfoTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.ProgressEvent;
import kornell.gui.client.event.ShowDetailsEvent;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.HistoryMapper;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.course.ClassroomView.Presenter;
import kornell.gui.client.presentation.util.LoadingPopup;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericCourseDetailsView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseDetailsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final HistoryMapper historyMapper = GWT.create(HistoryMapper.class);

	private KornellSession session;
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
	FlowPanel detailsContentPanel;

	private Button btnAbout;
	private Button btnTopics;
	private Button btnCertification;
	private Button btnGoToCourse;

	private Button btnCurrent;
	private CourseClassTO courseClassTO;
	private CourseDetailsTO courseDetails;
	private UserInfoTO user;
	private FlowPanel aboutPanel;
	private FlowPanel topicsPanel;
	private FlowPanel certificationPanel;
	private ClientFactory clientFactory;

	private Presenter presenter;

	private Contents contents;
	private List<Actom> actoms;

	private boolean isEnrolled;
	
	public GenericCourseDetailsView(EventBus bus, KornellSession session, PlaceController placeCtrl) {
		this.bus = bus;
		this.session = session;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void initData() {
		setContents(presenter.getContents());
		certificationPanel = getCertificationPanel();
		courseClassTO = Dean.getInstance().getCourseClassTO();
		display();
	}

	private void setContents(Contents contents) {
		this.contents = contents;
		this.actoms = ContentsCategory.collectActoms(contents);
		//fireProgressChangeEvent();
	}

	private void fireProgressChangeEvent() {
		int pagesVisitedCount = 0;
		int totalPages = actoms.size();
		for (Actom actom : actoms) {
			if(actom.isVisited()){
				pagesVisitedCount++;
				continue;
			}
			break;
		}
		ProgressEvent progressChangeEvent = new ProgressEvent();
		progressChangeEvent.setCurrentPage(0);
		progressChangeEvent.setTotalPages(totalPages);		
		progressChangeEvent.setPagesVisitedCount(pagesVisitedCount);
		progressChangeEvent.setEnrollmentUUID(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID());
		bus.fireEvent(progressChangeEvent);
	}

	private void display() {
		isEnrolled = false;
		UserInfoTO user = session.getCurrentUser();
		for (Enrollment enrollment : user.getEnrollmentsTO().getEnrollments()) {
			if(enrollment.getUUID().equals(((ClassroomPlace)placeCtrl.getWhere()).getEnrollmentUUID())
					&& (EnrollmentState.enrolled.equals(enrollment.getState()) ||
							(EnrollmentState.preEnrolled.equals(enrollment.getState())))){
				isEnrolled = true;
				break;
			}
		}
		displayButtons();
		
		CourseDetailsTOBuilder builder = new CourseDetailsTOBuilder(courseClassTO.getCourseVersionTO()
				.getCourse().getInfoJson());
		builder.buildCourseDetails();
		courseDetails = builder.getCourseDetailsTO();

		topicsPanel = new FlowPanel();
		
		aboutPanel = getAboutPanel();
		detailsContentPanel.add(aboutPanel);
		btnCurrent = btnAbout;
		displayContent(btnCurrent);

		topicsPanel.addStyleName("topicsPanel");
		displayTopics();

		displayTitle();

		detailsContentPanel.add(topicsPanel);
		detailsContentPanel.add(certificationPanel);
	}

	private void displayContent(Button btn) {
		aboutPanel.setVisible(btn.equals(btnAbout));
		topicsPanel.setVisible(btn.equals(btnTopics));
		certificationPanel.setVisible(btn.equals(btnCertification));
		LoadingPopup.hide();
	}
	
	private FlowPanel getAboutPanel(){
		FlowPanel aboutPanel = new FlowPanel();
		aboutPanel.add(getInfosPanel());
		aboutPanel.add(getSidePanel());
		return aboutPanel;
	}

	private FlowPanel getCertificationPanel() {
		FlowPanel certificationPanel = new FlowPanel();
		certificationPanel.addStyleName("certificationPanel");
		// TODO: i18n
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

		Label infoText = new Label(
				/*"Confira abaixo o status dos testes e avaliações presentes neste curso. " + */"Seu certificado pode ser impresso por aqui caso você tenha concluído 100% do conteúdo do curso e tenha sido aprovado na avaliação final.");
		infoText.addStyleName("certificationInfoText");
		certificationInfo.add(infoText);

		return certificationInfo;
	}

	private FlowPanel getCertificationTableContent() {
		FlowPanel certificationContentPanel = new FlowPanel();
		certificationContentPanel.addStyleName("certificationContentPanel");

		//certificationContentPanel.add(new GenericCertificationItemView(bus, session, Dean.getInstance().getCourseClassTO(), GenericCertificationItemView.TEST));
		certificationContentPanel.add(new GenericCertificationItemView(bus, session, Dean.getInstance().getCourseClassTO(), GenericCertificationItemView.CERTIFICATION)); 

		return certificationContentPanel;
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

	private void displayTopics() {
		int i = 0;
		ExternalPage page;
		boolean enableAnchorOnNextTopicsFirstChild = true;
		for (Content content: contents.getChildren()) {
			topicsPanel.add(new GenericTopicView(bus, session, placeCtrl, session, Dean.getInstance().getCourseClassTO(), content, i++, enableAnchorOnNextTopicsFirstChild));
			enableAnchorOnNextTopicsFirstChild = true;
			List<Content> children = new ArrayList<Content>();
			if(ContentFormat.Topic.equals(content.getFormat()) ){
				children = content.getTopic().getChildren();
			}
			for (Content contentItem : children) {
				page = contentItem.getExternalPage();
				if(!page.isVisited()){
					enableAnchorOnNextTopicsFirstChild = false;
					break;
				}
			}
		}
	}

	private Button getHeaderButton(String label, String styleName,
			String styleNameGlobal) {
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

		Label titleLabel = new Label(constants.detailsHeader() + " ");
		titleLabel.addStyleName("titleLabel");
		titlePanel.add(titleLabel);

		Label courseNameLabel = new Label(courseClassTO.getCourseVersionTO().getCourse().getTitle());
		courseNameLabel.addStyleName("courseNameLabel");
		titlePanel.add(courseNameLabel);
	}

	private FlowPanel getInfosPanel() {
		FlowPanel infoPanel = new FlowPanel();
		infoPanel.addStyleName("infoPanel");
		for (InfoTO infoTO : courseDetails.getInfos()) {
			infoPanel.add(getInfoPanel(infoTO.getType(), infoTO.getText()));
		}
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
		btnAbout = new Button();
		btnTopics = new Button();
		btnCertification = new Button();
		btnGoToCourse = new Button();
		displayButton(btnAbout, constants.btnAbout(), constants.btnAboutInfo(), true);
		if(actoms.size() > 1){
			displayButton(btnTopics, constants.btnTopics(),
					constants.btnTopicsInfo(), false);
		}
		// TODO: i18n
		if(isEnrolled){
			//TODO comment
			displayButton(btnCertification, constants.btnCertification(), "Imprimir Certificado"/*constants.btnCertificationInfo()*/, false);
			displayButton(btnGoToCourse, "Ir para o curso", "", false);	
		}
	}

	private void displayButton(Button btn, String title, String label, boolean isSelected) {
		btn.addStyleName("btnDetails " + (isSelected ? "btnSelected" : "btnNotSelected"));
		btn.removeStyleName("btn");

		Label btnTitle = new Label(title);
		btnTitle.addStyleName("btnTitle");
		btn.add(btnTitle);

		Label btnLabel = new Label(label);
		btnLabel.addStyleName("btnLabel");
		btn.add(btnLabel);
		
		btn.addStyleName("gradient");
		
		btn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Button btn = (Button) event.getSource();
				if(!btnGoToCourse.equals(btn)){
					handleEvent(btn);
				} else {
					bus.fireEvent(new ShowDetailsEvent(false));
				}
			}
		});
		
		buttonsPanel.add(btn);
	}
	
	private FlowPanel getSidePanel(){
		FlowPanel sidePanel = new FlowPanel();
		sidePanel.addStyleName("sidePanel");


		if(!isEnrolled){
			FlowPanel notEnrolledPanel = new FlowPanel();
			notEnrolledPanel.addStyleName("notEnrolledPanel");
			HTMLPanel panel = new HTMLPanel("Sua matríula ainda não foi aprovada pela instituição.<br><br> Você receberá um e-mail no momento da aprovação.<br>");
			notEnrolledPanel.add(panel);
			
			sidePanel.add(notEnrolledPanel);
			//"Você receberá um email quando ela for aprovada."
		}
		
		sidePanel.add(getHintsPanel());
		
		return sidePanel;
	}

	private FlowPanel getHintsPanel() {
		FlowPanel hintsPanel = new FlowPanel();
		hintsPanel.addStyleName("hintsPanel");

		for (HintTO hintTO : courseDetails.getHints()) {
			hintsPanel.add(getHintPanel(hintTO.getType(), hintTO.getName()));
		}

		return hintsPanel;
	}

	private FlowPanel getHintPanel(String img, String hintText) {
		FlowPanel hint = new FlowPanel();
		hint.addStyleName("hintDetails");

		Image hintImg = new Image(IMAGES_PATH + img + ".png");
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

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}

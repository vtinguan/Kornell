package kornell.gui.client.presentation.course.details.generic;

import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.UserSession;
import kornell.core.lom.Actom;
import kornell.core.lom.Content;
import kornell.core.lom.Contents;
import kornell.core.lom.ContentsCategory;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.UserInfoTO;
import kornell.core.to.coursedetails.CourseDetailsTO;
import kornell.core.to.coursedetails.HintTO;
import kornell.core.to.coursedetails.InfoTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.ProgressChangeEvent;
import kornell.gui.client.event.ProgressChangeEventHandler;
import kornell.gui.client.presentation.HistoryMapper;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.course.details.CourseDetailsPlace;
import kornell.gui.client.presentation.course.details.CourseDetailsView;
import kornell.gui.client.presentation.course.details.data.CourseDetailsTOBuilder;
import kornell.gui.client.presentation.util.LoadingPopup;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericCourseDetailsView extends Composite implements
		CourseDetailsView {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseDetailsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final HistoryMapper historyMapper = GWT.create(HistoryMapper.class);

	private UserSession session;
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

	@UiField
	Button btnAbout;
	@UiField
	Button btnTopics;
	@UiField
	Button btnCertification;

	private Button btnCurrent;
	private CourseClassTO courseClassTO;
	private CourseDetailsTO courseDetails;
	private UserInfoTO user;
	private FlowPanel aboutPanel;
	private FlowPanel topicsPanel;
	private FlowPanel certificationPanel;
	private ClientFactory clientFactory;

	private Contents contents;
	private List<Actom> actoms;
	private CourseClassTO currentCourseClass;
	
	public GenericCourseDetailsView(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.bus = clientFactory.getEventBus();
		this.session = clientFactory.getUserSession();
		this.placeCtrl = clientFactory.getPlaceController();
		this.currentCourseClass = clientFactory.getCurrentCourseClass();
		initWidget(uiBinder.createAndBindUi(this));
		initData();
	}

	private void initData() {
		LoadingPopup.show();
		certificationPanel = getCertificationPanel();
		
		session.getCourseClassesTO(new Callback<CourseClassesTO>() {
			@Override
			public void ok(CourseClassesTO courseClasses) {
				for (CourseClassTO courseClassTmp : courseClasses.getCourseClasses()) {
					if(courseClassTmp.getCourseClass().getInstitutionUUID().equals(clientFactory.getInstitution().getUUID())){
						courseClassTO = courseClassTmp;
					}
				}
				user = session.getUserInfo();
				session.courseClass(clientFactory.getCurrentCourseClass().getCourseClass().getUUID()).contents(new Callback<Contents>() {
					@Override
					public void ok(Contents contents) {
						setContents(contents);
						display();
						LoadingPopup.hide();
					}
				});
			}
		});
	}

	private void setContents(Contents contents) {
		this.contents = contents;
		this.actoms = ContentsCategory.collectActoms(contents);
		fireProgressChangeEvent();
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
		ProgressChangeEvent progressChangeEvent = new ProgressChangeEvent();
		progressChangeEvent.setCurrentPage(pagesVisitedCount);
		progressChangeEvent.setTotalPages(totalPages);		
		progressChangeEvent.setPagesVisitedCount(pagesVisitedCount);
		progressChangeEvent.setEnrollmentUUID(currentCourseClass.getCourseClass().getUUID());
		bus.fireEvent(progressChangeEvent);
	}

	private void display() {
		CourseDetailsTOBuilder builder = new CourseDetailsTOBuilder(courseClassTO.getCourseVersionTO()
				.getCourse().getInfoJson());
		builder.buildCourseDetails();
		courseDetails = builder.getCourseDetailsTO();
		
		aboutPanel = getAboutPanel();
		
		topicsPanel = new FlowPanel();
		topicsPanel.addStyleName("topicsPanel");
		topicsPanel.add(getTopicsTableHeader());
		topicsPanel.add(getTopicsTableContent());

		btnCurrent = btnAbout;
		displayTitle();
		displayButtons();

		detailsContentPanel.add(aboutPanel);
		detailsContentPanel.add(topicsPanel);
		detailsContentPanel.add(certificationPanel);
		displayContent(btnCurrent);
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
		aboutPanel.add(getHintsPanel());
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
				"Confira abaixo o status dos testes e avaliações presentes neste curso. Seu certificado pode ser impresso por aqui caso você tenha concluído 100% do conteúdo do curso e tenha sido aprovado na avaliação final.");
		infoText.addStyleName("certificationInfoText");
		certificationInfo.add(infoText);

		return certificationInfo;
	}

	private FlowPanel getCertificationTableContent() {
		FlowPanel certificationContentPanel = new FlowPanel();
		certificationContentPanel.addStyleName("certificationContentPanel");

		certificationContentPanel.add(new GenericCertificationItemView(bus, session, currentCourseClass, GenericCertificationItemView.TEST));
		certificationContentPanel.add(new GenericCertificationItemView(bus, session, currentCourseClass, GenericCertificationItemView.CERTIFICATION)); 

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
	
	private FlowPanel getTopicsTableContent() {
		FlowPanel topicsContentPanel = new FlowPanel();
		topicsContentPanel.addStyleName("topicsContentPanel");
		boolean startOpened = (contents.getChildren().size() == 1);
		int i = 0;
		for (Content content: contents.getChildren()) {
			topicsContentPanel.add(new GenericTopicView(bus, session, placeCtrl, session, currentCourseClass, content, i++, startOpened));
		}	
		return topicsContentPanel;
	}

	private FlowPanel getTopicsTableHeader() {
		FlowPanel topicsHeaderPanel = new FlowPanel();
		topicsHeaderPanel.addStyleName("topicsHeaderPanel");

		topicsHeaderPanel.add(getHeaderButton(constants.topic(), "btnTopics",
				"btnTopicsHeader"));
		return topicsHeaderPanel;
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
		displayButton(btnAbout, constants.btnAbout(), constants.btnAboutInfo());
		displayButton(btnTopics, constants.btnTopics(),
				constants.btnTopicsInfo());
		displayButton(btnCertification, constants.btnCertification(),
				constants.btnCertificationInfo());
	}

	private void displayButton(Button btn, String title, String label) {
		btn.removeStyleName("btn");

		Label btnTitle = new Label(title);
		btnTitle.addStyleName("btnTitle");
		btn.add(btnTitle);

		Label btnLabel = new Label(label);
		btnLabel.addStyleName("btnLabel");
		btn.add(btnLabel);
		
		btn.addStyleName("gradient");

		btn.addClickHandler(new DetailsButtonClickHandler());
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

	private final class DetailsButtonClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			handleEvent((Button) event.getSource());
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}
}

package kornell.gui.client.presentation.course.details.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.api.client.UserSession;
import kornell.core.lom.Actom;
import kornell.core.lom.Content;
import kornell.core.lom.ContentFormat;
import kornell.core.lom.Contents;
import kornell.core.lom.ContentsCategory;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.CourseTO;
import kornell.core.to.UserInfoTO;
import kornell.core.to.coursedetails.CertificationTO;
import kornell.core.to.coursedetails.CourseDetailsTO;
import kornell.core.to.coursedetails.HintTO;
import kornell.core.to.coursedetails.InfoTO;
import kornell.core.to.coursedetails.TopicTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.HistoryMapper;
import kornell.gui.client.presentation.course.CourseClassPlace;
import kornell.gui.client.presentation.course.details.CourseDetailsPlace;
import kornell.gui.client.presentation.course.details.CourseDetailsView;
import kornell.gui.client.presentation.course.details.data.CourseDetailsTOBuilder;
import kornell.gui.client.presentation.util.loading.LoadingPopup;

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

	Button btnCurrent;

	CourseClassTO courseClassTO;
	
	CourseDetailsTO courseDetails;

	UserInfoTO user;
	
	FlowPanel topicsPanel;

	private Contents contents;
	private List<Actom> actoms;
	private CourseClassTO currentCourseClass;
	
	public GenericCourseDetailsView(EventBus eventBus, UserSession session,
			PlaceController placeCtrl, CourseClassTO currentCourseClass) {
		this.bus = eventBus;
		this.session = session;
		this.placeCtrl = placeCtrl;
		this.currentCourseClass = currentCourseClass;
		LoadingPopup.show();
		initWidget(uiBinder.createAndBindUi(this));
		initData();
	}

	private void initData() {
		final String uuid = placeCtrl.getWhere() instanceof CourseDetailsPlace ? ((CourseDetailsPlace) placeCtrl
				.getWhere()).getCourseClassUUID() : ((CourseClassPlace) placeCtrl
				.getWhere()).getCourseClassUUID();

		session.getCourseClassesTO(new Callback<CourseClassesTO>() {
			@Override
			public void ok(CourseClassesTO courseClasses) {
				courseClassTO = courseClasses.getCourseClasses().get(0);
				user = session.getUserInfo();
				session.courseClass(uuid).contents(new Callback<Contents>() {
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
	}

	private void display() {
		CourseDetailsTOBuilder builder = new CourseDetailsTOBuilder(courseClassTO.getCourseVersionTO()
				.getCourse().getInfoJson());
		builder.buildCourseDetails();
		courseDetails = builder.getCourseDetailsTO();
		
		topicsPanel = new FlowPanel();
		topicsPanel.addStyleName("topicsPanel");
		topicsPanel.add(getTopicsTableHeader());
		topicsPanel.add(getTopicsTableContent());

		btnCurrent = btnAbout;
		displayTitle();
		displayButtons();
		displayContent(btnCurrent);
	}

	private void displayContent(Button btn) {
		LoadingPopup.show();
		detailsContentPanel.clear();
		if (btn.equals(btnAbout)) {
			detailsContentPanel.add(getInfosPanel());
			detailsContentPanel.add(getHintsPanel());
		} else if (btn.equals(btnTopics)) {
			detailsContentPanel.add(topicsPanel);
		} else if (btn.equals(btnCertification)) {
			detailsContentPanel.add(getCertificationPanel());
		}
		LoadingPopup.hide();
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
				"Confira abaixo o status dos testes e avaliações presentes neste curso. Seu certificado pode ser impresso por aqui caso você tenha concluído 100% do conteúdo do curso e ter sido aprovado na avaliação final.");
		infoText.addStyleName("certificationInfoText");
		certificationInfo.add(infoText);

		return certificationInfo;
	}

	private FlowPanel getCertificationTableContent() {
		FlowPanel certificationContentPanel = new FlowPanel();
		certificationContentPanel.addStyleName("certificationContentPanel");

		CertificationTO certificationTO1 = new CertificationTO(
				"test",
				"Pré-teste",
				"Esta avaliação tem a intenção de identificar o seu conhecimento referente ao tema do curso. A diferença da nota do pré-teste com o pós-teste (avaliação final) serve para te mostrar o ganho de conhecimento que você terá obtido ao final do curso.");
		CertificationTO certificationTO2 = new CertificationTO(
				"test",
				"Pós-teste",
				"Esta avaliação final tem a intenção de identificar o seu conhecimento após a conclusão do curso.");
		CertificationTO certificationTO3 = new CertificationTO(
				"certification",
				"Certificado",
				"Impressão do certificado. Uma vez que o curso for terminado, você poderá gerar o certificado aqui.");


		certificationContentPanel.add(getCertificationWrapper(certificationTO1));
		certificationContentPanel.add(getCertificationWrapper(certificationTO2));
		certificationContentPanel.add(getCertificationWrapper(certificationTO3));

		return certificationContentPanel;
	}

	private FlowPanel getCertificationWrapper(CertificationTO certificationTO) {
		FlowPanel certificationWrapper = new FlowPanel();
		certificationWrapper.addStyleName("certificationWrapper");

		FlowPanel itemPanel = new FlowPanel();
		itemPanel.addStyleName("itemPanel");

		Image certificationIcon = new Image(IMAGES_PATH
				+ certificationTO.getType() + ".png");
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

		Label lblGrade = new Label(
				"certification".equals(certificationTO.getType()) ? " " : (!""
						.equals(certificationTO.getGrade()) ? certificationTO
						.getGrade() : "-"));
		lblGrade.addStyleName("lblGrade");
		certificationWrapper.add(lblGrade);

		Anchor lblActions;
		if ("test".equals(certificationTO.getType())) {
			lblActions = new Anchor("Visualizar");
		} else if ("certification".equals(certificationTO.getType())) {
			lblActions = new Anchor("Gerar");
			lblActions.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Window.Location.assign(session.getApiUrl() + "/report/certificate/"
							+ user.getPerson().getUUID() + "/"
							+ courseClassTO.getCourseClass().getUUID());
				}
			});
		} else {
			lblActions = new Anchor("-");
		}
		lblActions.addStyleName("lblActions");
		certificationWrapper.add(lblActions);

		return certificationWrapper;
	}

	private FlowPanel getCertificationTableHeader() {
		FlowPanel certificationHeaderPanel = new FlowPanel();
		certificationHeaderPanel.addStyleName("certificationHeaderPanel");

		certificationHeaderPanel.add(getHeaderButton("Item", "btnItem",
				"btnCertificationHeader"));
		certificationHeaderPanel.add(getHeaderButton("Status", "btnStatus",
				"btnCertificationHeader"));
		certificationHeaderPanel.add(getHeaderButton("Nota", "btnGrade",
				"btnCertificationHeader"));
		certificationHeaderPanel.add(getHeaderButton("Ações", "btnActions",
				"btnCertificationHeader"));

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

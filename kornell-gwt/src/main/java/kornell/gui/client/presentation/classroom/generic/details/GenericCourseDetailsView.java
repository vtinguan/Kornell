package kornell.gui.client.presentation.classroom.generic.details;

import static kornell.core.util.StringUtils.mkurl;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceChangeEvent;
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

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.InstitutionType;
import kornell.core.lom.Actom;
import kornell.core.lom.Content;
import kornell.core.lom.ContentFormat;
import kornell.core.lom.Contents;
import kornell.core.lom.ContentsOps;
import kornell.core.lom.ExternalPage;
import kornell.core.to.CourseClassTO;
import kornell.core.to.LibraryFilesTO;
import kornell.core.to.coursedetails.CourseDetailsTO;
import kornell.core.to.coursedetails.HintTO;
import kornell.core.to.coursedetails.InfoTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ShowDetailsEvent;
import kornell.gui.client.event.ShowDetailsEventHandler;
import kornell.gui.client.presentation.admin.courseclass.courseclass.generic.GenericCourseClassMessagesView;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.presentation.classroom.ClassroomView.Presenter;
import kornell.gui.client.presentation.message.MessagePanelType;
import kornell.gui.client.presentation.message.MessagePlace;
import kornell.gui.client.presentation.message.MessagePresenter;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.util.ClientConstants;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.LoadingPopup;

public class GenericCourseDetailsView extends Composite implements ShowDetailsEventHandler {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseDetailsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private KornellSession session;
	private PlaceController placeCtrl;
	private EventBus bus;
	private ViewFactory viewFactory;
	private MessagePresenter messagePresenterClassroomGlobalChat, messagePresenterClassroomTutorChat;
	private GenericCourseClassMessagesView messagesGlobalChatView, messagesTutorChatView;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private String IMAGES_PATH = mkurl(ClientConstants.IMAGES_PATH, "courseDetails");

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
	private Button btnChat;
	private Button btnTutor;
	private Button btnLibrary;
	private Button btnGoToCourse;

	private Button btnCurrent;
	private CourseClassTO courseClassTO;
	private CourseDetailsTO courseDetails;
	private FlowPanel aboutPanel;
	private FlowPanel topicsPanel;
	private FlowPanel certificationPanel;
	private FlowPanel chatPanel;
	private FlowPanel tutorPanel;
	private FlowPanel libraryPanel;

	private Presenter presenter;

	private Contents contents;
	private List<Actom> actoms;

	private boolean isEnrolled, isCancelled, isInactiveCourseClass;

	public GenericCourseDetailsView(EventBus bus, KornellSession session, PlaceController placeCtrl,
			ViewFactory viewFactory) {
		this.bus = bus;
		this.bus.addHandler(ShowDetailsEvent.TYPE, this);
		this.session = session;
		this.placeCtrl = placeCtrl;
		this.viewFactory = viewFactory;
		this.messagePresenterClassroomGlobalChat = viewFactory.getMessagePresenterClassroomGlobalChat();
		this.messagePresenterClassroomGlobalChat.enableMessagesUpdate(false);
		this.messagePresenterClassroomTutorChat = viewFactory.getMessagePresenterClassroomTutorChat();
		this.messagePresenterClassroomTutorChat.enableMessagesUpdate(false);
		initWidget(uiBinder.createAndBindUi(this));
		
		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				btnCurrent = btnAbout;
			}
		});
	}

	public void initData() {
		setContents(presenter.getContents());
		certificationPanel = getCertificationPanel();
		courseClassTO = session.getCurrentCourseClass();
		if (courseClassTO != null)
			display();
	}

	private void setContents(Contents contents) {
		this.contents = contents;
		if (contents != null) {
			this.actoms = ContentsOps.collectActoms(contents);
		}
	}

	private void display() {
		isEnrolled = false;
		isCancelled = false;

		CourseClassTO courseClassTO = session.getCurrentCourseClass();
		if (courseClassTO != null && courseClassTO.getEnrollment() != null) {
			if (EnrollmentState.enrolled.equals(courseClassTO.getEnrollment().getState())) {
				isEnrolled = true;
			} else if (EnrollmentState.cancelled.equals(courseClassTO.getEnrollment().getState())) {
				isCancelled = true;
			}
		}
		isInactiveCourseClass = CourseClassState.inactive.equals(courseClassTO.getCourseClass().getState());
		displayButtons();

		CourseDetailsTOBuilder builder = new CourseDetailsTOBuilder(courseClassTO.getCourseVersionTO().getCourse()
				.getInfoJson());
		builder.buildCourseDetails();
		courseDetails = builder.getCourseDetailsTO();

		topicsPanel = new FlowPanel();

		aboutPanel = getAboutPanel();
		detailsContentPanel.add(aboutPanel);
		btnCurrent = btnAbout;
		displayContent(btnCurrent);

		topicsPanel.addStyleName("topicsPanel");
		if (contents != null)
			displayTopics();

		displayTitle();

		detailsContentPanel.add(topicsPanel);
		detailsContentPanel.add(certificationPanel);

		btnLibrary.setVisible(false);
		if(!session.getCurrentCourseClass().isEnrolledOnCourseVersion()){
			session.courseClass(session.getCurrentCourseClass().getCourseClass().getUUID()).libraryFiles(
					new Callback<LibraryFilesTO>() {
						@Override
						public void ok(LibraryFilesTO to) {
							if (to.getLibraryFiles() != null && to.getLibraryFiles().size() > 0) {
								libraryPanel = getLibraryPanel(to);
								libraryPanel.setVisible(false);
								detailsContentPanel.add(libraryPanel);
								btnLibrary.setVisible(true);
							}
						}
					});
		}
	}

	private void displayContent(Button btn) {
		aboutPanel.setVisible(btn.equals(btnAbout));
		topicsPanel.setVisible(btn.equals(btnTopics));
		if (btn.equals(btnTopics)) {
			// When there's only one topic it should appear expanded by default
			if (topicsPanel.getWidgetCount() == 1) {
				((GenericTopicView) topicsPanel.getWidget(0)).show(true);
			}
		}
		certificationPanel.setVisible(btn.equals(btnCertification));

		if (btn.equals(btnChat)) {
			buildChatPanel();
		} else if (chatPanel != null) {
			chatPanel.setVisible(false);
			messagePresenterClassroomGlobalChat.enableMessagesUpdate(false);
		}

		if (btn.equals(btnTutor)) {
			buildTutorPanel();
		} else if (tutorPanel != null) {
			tutorPanel.setVisible(false);
			messagePresenterClassroomTutorChat.enableMessagesUpdate(false);
		}

		if (libraryPanel != null)
			libraryPanel.setVisible(btn.equals(btnLibrary));
		LoadingPopup.hide();
	}

	private FlowPanel getAboutPanel() {
		FlowPanel aboutPanel = new FlowPanel();
		aboutPanel.add(getInfosPanel());
		aboutPanel.add(getSidePanel());
		return aboutPanel;
	}

	private FlowPanel getCertificationPanel() {
		FlowPanel certificationPanel = new FlowPanel();
		certificationPanel.addStyleName("certificationPanel");
		certificationPanel.add(getCertificationInfo());
		certificationPanel.add(getCertificationTableHeader());
		certificationPanel.add(getCertificationTableContent());

		return certificationPanel;
	}

	private void buildChatPanel() {
		LoadingPopup.show();
		messagePresenterClassroomGlobalChat.enableMessagesUpdate(true);
		if (messagesGlobalChatView == null) {
			messagesGlobalChatView = new GenericCourseClassMessagesView(session, bus, placeCtrl, viewFactory,
					messagePresenterClassroomGlobalChat, session.getCurrentCourseClass());

			chatPanel = new FlowPanel();
			detailsContentPanel.add(chatPanel);
			
			messagesGlobalChatView.initData();
			messagePresenterClassroomGlobalChat.threadClicked(null);
		} else {
			messagePresenterClassroomGlobalChat.getChatThreadMessagesSinceLast();
			messagePresenterClassroomGlobalChat.scrollToBottom();
		}
		chatPanel.clear();
		chatPanel.add(messagePresenterClassroomGlobalChat.asWidget());
		chatPanel.setVisible(true);
	}

	private void buildTutorPanel() {
		LoadingPopup.show();
		messagePresenterClassroomTutorChat.enableMessagesUpdate(true);
		if (messagesTutorChatView == null) {
			messagesTutorChatView = new GenericCourseClassMessagesView(session, bus, placeCtrl, viewFactory,
					messagePresenterClassroomTutorChat, session.getCurrentCourseClass());
			
			tutorPanel = new FlowPanel();
			detailsContentPanel.add(tutorPanel);
			
			messagesTutorChatView.initData();
			messagePresenterClassroomTutorChat.threadClicked(null);
		} else {
			messagePresenterClassroomTutorChat.getChatThreadMessagesSinceLast();
			messagePresenterClassroomTutorChat.scrollToBottom();
		}
		tutorPanel.clear();
		tutorPanel.add(messagesTutorChatView);
		tutorPanel.setVisible(true);
	}

	private FlowPanel getLibraryPanel(LibraryFilesTO libraryFilesTO) {
		FlowPanel libraryPanel = new FlowPanel();
		libraryPanel.add(new GenericCourseLibraryView(bus, session, placeCtrl, libraryFilesTO));

		return libraryPanel;
	}

	private FlowPanel getCertificationInfo() {
		FlowPanel certificationInfo = new FlowPanel();
		certificationInfo.addStyleName("detailsInfo");

		Label infoTitle = new Label(constants.certification());
		infoTitle.addStyleName("detailsInfoTitle");
		certificationInfo.add(infoTitle);

		Label infoText = new Label(constants.certificationInfoText());
		infoText.addStyleName("detailsInfoText");
		certificationInfo.add(infoText);

		return certificationInfo;
	}

	private FlowPanel getCertificationTableContent() {
		FlowPanel certificationContentPanel = new FlowPanel();
		certificationContentPanel.addStyleName("certificationContentPanel");

		// certificationContentPanel.add(new GenericCertificationItemView(bus,
		// session, session.getCourseClassTO(),
		// GenericCertificationItemView.TEST));
		certificationContentPanel.add(new GenericCertificationItemView(bus, session, session.getCurrentCourseClass(),
				GenericCertificationItemView.CERTIFICATION));

		return certificationContentPanel;
	}

	private FlowPanel getCertificationTableHeader() {
		FlowPanel certificationHeaderPanel = new FlowPanel();
		certificationHeaderPanel.addStyleName("certificationHeaderPanel");

		certificationHeaderPanel.add(getHeaderButton(constants.certificationTableInfo(), "btnItem",
				"btnCertificationHeader"));
		certificationHeaderPanel.add(getHeaderButton(constants.certificationTableStatus(), "btnStatus centerText",
				"btnCertificationHeader"));
		certificationHeaderPanel.add(getHeaderButton(constants.certificationTableGrade(), "btnGrade centerText",
				"btnCertificationHeader"));
		certificationHeaderPanel.add(getHeaderButton(constants.certificationTableActions(), "btnActions centerText",
				"btnCertificationHeader"));

		return certificationHeaderPanel;
	}

	private Button getHeaderButton(String label, String styleName, String styleNameGlobal) {
		Button btn = new Button(label);
		btn.removeStyleName("btn");
		btn.addStyleName(styleNameGlobal);
		btn.addStyleName(styleName);
		return btn;
	}

	private void displayTopics() {
		int i = 0;
		ExternalPage page;
		boolean enableAnchorOnNextTopicsFirstChild = true;
		for (Content content : contents.getChildren()) {
			topicsPanel.add(new GenericTopicView(bus, session, placeCtrl, session, session.getCurrentCourseClass(), content,
					i++, enableAnchorOnNextTopicsFirstChild));
			enableAnchorOnNextTopicsFirstChild = true;
			List<Content> children = new ArrayList<Content>();
			if (ContentFormat.Topic.equals(content.getFormat())) {
				children = content.getTopic().getChildren();
			}
			for (Content contentItem : children) {
				page = contentItem.getExternalPage();
				if (!page.isVisited()) {
					enableAnchorOnNextTopicsFirstChild = false;
					break;
				}
			}
		}
	}

	private void displayTitle() {
		Image titleImage = new Image(StringUtils.mkurl(IMAGES_PATH, "details.png"));
		titleImage.addStyleName("titleImage");
		titlePanel.add(titleImage);

		Label titleLabel = new Label(constants.detailsHeader() + " ");
		titleLabel.addStyleName("titleLabel");
		titlePanel.add(titleLabel);

		Label courseNameLabel = new Label(courseClassTO.getCourseVersionTO().getCourse().getTitle());
		courseNameLabel.addStyleName("courseNameLabel");
		titlePanel.add(courseNameLabel);

		Label subTitleLabel = new Label(constants.detailsSubHeader() + " ");
		subTitleLabel.addStyleName("titleLabel subTitleLabel");
		titlePanel.add(subTitleLabel);

		Label courseClassNameLabel = new Label(courseClassTO.getCourseClass().getName());
		courseClassNameLabel.addStyleName("courseClassNameLabel");
		titlePanel.add(courseClassNameLabel);
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
		btnChat = new Button();
		btnTutor = new Button();
		btnLibrary = new Button();
		btnGoToCourse = new Button();
		displayButton(btnAbout, constants.btnAbout(), constants.btnAboutInfo(), true);
		if (actoms != null && actoms.size() > 1) {
			displayButton(btnTopics, constants.btnTopics(), constants.btnTopicsInfo(), false);
		}
		if (isInactiveCourseClass) {
			displayButton(btnCertification, constants.btnCertification(), constants.printCertificateButton(), false);
		} else if (isEnrolled && !isCancelled) {
			displayButton(btnCertification, constants.btnCertification(), constants.printCertificateButton(), false);
			if (courseClassTO.getCourseClass().isCourseClassChatEnabled()) {
				displayButton(btnChat, constants.btnChat(), constants.classChatButton(), false);
			}
			if (courseClassTO.getCourseClass().isTutorChatEnabled()) {
				displayButton(btnTutor, constants.btnTutor(), constants.tutorChatButton(), false);
			}
			displayButton(btnLibrary, constants.btnLibrary(), constants.libraryButton(), false);
			displayButton(btnGoToCourse, constants.goToClassButton(), "", false);
		}
	}

	private void displayButton(Button btn, String title, String label, boolean isSelected) {
		btn.addStyleName("btnDetails " + (isSelected ? "btnAction" : "btnNotSelected"));

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
				if (!btnGoToCourse.equals(btn)) {
					handleEvent(btn);
				} else {
					bus.fireEvent(new ShowDetailsEvent(false));
				}
			}
		});

		buttonsPanel.add(btn);
	}

	private FlowPanel getSidePanel() {
		detailsPanel.addStyleName("shy");
		FlowPanel sidePanel = new FlowPanel();
		sidePanel.addStyleName("sidePanel");

		String text = "";
		if (isInactiveCourseClass || isCancelled || !isEnrolled) {
			FlowPanel warningPanel = new FlowPanel();
			warningPanel.addStyleName("notEnrolledPanel");
			if (isInactiveCourseClass) {
				text = constants.inactiveCourseClass();
			} else if (isCancelled) {
				text = constants.cancelledEnrollment();
			} else if (!isEnrolled) {
				text = constants.enrollmentNotApproved()
						+ (StringUtils.isSome(session.getCurrentUser().getPerson().getEmail()) ? "" : constants
								.enrollmentConfirmationEmail());
			}
			HTMLPanel panel = new HTMLPanel(text);
			warningPanel.add(panel);
			sidePanel.add(warningPanel);
		}

		if (!"".equals(text) && InstitutionType.DASHBOARD.equals(session.getInstitution().getInstitutionType())) {
			KornellNotification.show(text.replaceAll("<br>", ""), AlertType.WARNING, 5000);
			placeCtrl.goTo(new ProfilePlace(session.getCurrentUser().getPerson().getUUID(), false));
		} else {
			detailsPanel.removeStyleName("shy");
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

	private FlowPanel getHintPanel(String fontAwesomeClass, String hintText) {
		FlowPanel hint = new FlowPanel();
		hint.addStyleName("hintDetails ");
		
		Icon icon = new Icon();
		icon.addStyleName(fontAwesomeClass);
		hint.add(icon);
		
		Label lblHintText = new Label(hintText);
		lblHintText.addStyleName("hintText");
		hint.add(lblHintText);

		return hint;
	}

	private void handleEvent(Button btn) {
		btnCurrent.removeStyleName("btnAction");
		btnCurrent.addStyleName("btnNotSelected");
		btn.addStyleName("btnAction");
		btn.removeStyleName("btnNotSelected");

		displayContent(btn);
		btnCurrent = btn;
	}

	@Override
	public void onShowDetails(ShowDetailsEvent event) {
		if(event.isShowDetails()){
			if(btnChat != null && btnChat.equals(btnCurrent)) {
				buildChatPanel();
			} else if(btnTutor != null && btnTutor.equals(btnCurrent)){
				buildTutorPanel();
			}
		} else {
			if(chatPanel != null){
				chatPanel.clear();	
			}
			if(tutorPanel != null){
				tutorPanel.clear();
			}
		}
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}

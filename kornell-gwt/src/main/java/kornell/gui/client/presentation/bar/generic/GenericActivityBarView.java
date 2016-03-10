package kornell.gui.client.presentation.bar.generic;

import static kornell.core.util.StringUtils.mkurl;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.to.UserInfoTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.GenericClientFactoryImpl;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.HideSouthBarEvent;
import kornell.gui.client.event.ProgressEvent;
import kornell.gui.client.event.ProgressEventHandler;
import kornell.gui.client.event.ShowChatDockEvent;
import kornell.gui.client.event.ShowChatDockEventHandler;
import kornell.gui.client.event.ShowDetailsEvent;
import kornell.gui.client.event.ShowDetailsEventHandler;
import kornell.gui.client.mvp.HistoryMapper;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.bar.ActivityBarView;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.presentation.classroom.generic.notes.NotesPopup;
import kornell.gui.client.sequence.NavigationRequest;
import kornell.gui.client.util.ClientConstants;

import com.github.gwtbootstrap.client.ui.ProgressBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class GenericActivityBarView extends Composite implements ActivityBarView, ProgressEventHandler,
		ShowDetailsEventHandler, ShowChatDockEventHandler {

	interface MyUiBinder extends UiBinder<Widget, GenericActivityBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private String page;

	private NotesPopup notesPopup;
	private FlowPanel progressBarPanel;
	private final HistoryMapper historyMapper = GWT.create(HistoryMapper.class);

	private static KornellConstants constants = GWT.create(KornellConstants.class);

	private static final String SOUTH_BAR_IMAGES_PATH = mkurl(ClientConstants.IMAGES_PATH, "southBar");

	private static String BUTTON_PREVIOUS = constants.previous();
	private static String BUTTON_NEXT = constants.next();
	private static String BUTTON_DETAILS = constants.details();
	private static String BUTTON_NOTES = constants.notes();
	private static String BUTTON_CHAT = constants.chat();

	private Image iconPrevious;
	private Image iconNext;
	private Image iconChat;

	private boolean showDetails = true;
	private boolean chatDockEnabled = false;
	private boolean enableNext = false;
	private boolean enablePrev = false;

	@UiField
	FocusPanel btnPrevious;
	@UiField
	FocusPanel btnNext;
	@UiField
	FocusPanel btnDetails;
	@UiField
	FocusPanel btnNotes;
	@UiField
	FocusPanel btnChat;
	@UiField
	FocusPanel btnProgress;

	@UiField
	FlowPanel activityBar;

	private UserInfoTO user;
	private ClientFactory clientFactory;
	private Dean dean;

	private Integer currentPage = 0, totalPages = 0, progressPercent = 0;

	private boolean shouldShowActivityBar, isEnrolled;

	public GenericActivityBarView(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.dean = GenericClientFactoryImpl.DEAN;
		initWidget(uiBinder.createAndBindUi(this));
		clientFactory.getEventBus().addHandler(ProgressEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(ShowDetailsEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(ShowChatDockEvent.TYPE, this);

		KornellSession session = clientFactory.getKornellSession();
		if (session.isAuthenticated()) {
			user = clientFactory.getKornellSession().getCurrentUser();
		}
		display();

		setupArrowsNavigation();

	}

	private void display() {

		this.setVisible(false);
		isEnrolled = false;

		UserInfoTO user = clientFactory.getKornellSession().getCurrentUser();
		if (user != null) {
			for (Enrollment enrollment : user.getEnrollments().getEnrollments()) {
				if (enrollment.getUUID().equals(
						((ClassroomPlace) clientFactory.getPlaceController().getWhere()).getEnrollmentUUID())
						&& EnrollmentState.enrolled.equals(enrollment.getState())) {
					isEnrolled = true;
					break;
				}
			}
		}
		shouldShowActivityBar = isEnrolled && dean.getCourseClassTO() != null
				&& !CourseClassState.inactive.equals(dean.getCourseClassTO().getCourseClass().getState());

		showDetails = !isEnrolled;

		iconPrevious = new Image(StringUtils.mkurl(SOUTH_BAR_IMAGES_PATH, getItemName(BUTTON_PREVIOUS) + ".png"));
		displayButton(btnPrevious, BUTTON_PREVIOUS, iconPrevious);

		iconNext = new Image(StringUtils.mkurl(SOUTH_BAR_IMAGES_PATH, getItemName(BUTTON_NEXT) + ".png"));
		displayButton(btnNext, BUTTON_NEXT, iconNext, true);

		displayButton(btnDetails, showDetails ? constants.course() : BUTTON_DETAILS,
				new Image(StringUtils.mkurl(SOUTH_BAR_IMAGES_PATH, getItemName(BUTTON_DETAILS) + ".png")));
		
		iconChat = new Image(StringUtils.mkurl(SOUTH_BAR_IMAGES_PATH, getItemName(BUTTON_CHAT) + ".png"));
		displayButton(btnChat, BUTTON_CHAT, iconChat);
		btnChat.addStyleName("activityBarButtonChat");

		displayButton(btnNotes, BUTTON_NOTES,
				new Image(StringUtils.mkurl(SOUTH_BAR_IMAGES_PATH, getItemName(BUTTON_NOTES) + ".png")));

		if (dean.getCourseClassTO().getCourseClass().isCourseClassChatEnabled() &&
				!dean.getCourseClassTO().getCourseClass().isChatDockEnabled()) {
			btnChat.addStyleName("activityBarButtonSmall");
			btnNotes.addStyleName("activityBarButtonSmall");
			btnChat.removeStyleName("shy");
		} else {
			btnChat.addStyleName("shy");
		}

		displayProgressButton();

		if (showDetails) {
			btnDetails.addStyleName("btnSelected");
			enableButton(BUTTON_PREVIOUS, false);
			enableButton(BUTTON_NEXT, false);
		}
	}

	private void displayProgressButton() {
		progressBarPanel = new FlowPanel();
		progressBarPanel.addStyleName("progressBarPanel");

		btnProgress.add(progressBarPanel);
		btnProgress.removeStyleName("btn");
		btnProgress.removeStyleName("btn-link");
	}

	private void updateProgressBarPanel(Integer currentPage, Integer totalPages, Integer progressPercent) {
		this.currentPage = currentPage;
		this.totalPages = totalPages;
		this.progressPercent = progressPercent;
		updateProgressBarPanel();
	}

	private void updateProgressBarPanel() {
		progressBarPanel.clear();

		ProgressBar progressBar = new ProgressBar();
		progressBar.setPercent(progressPercent);

		FlowPanel pagePanel = new FlowPanel();
		pagePanel.addStyleName("pagePanel");
		pagePanel.addStyleName("label");

		if (showDetails) {
			pagePanel.add(createSpan(progressPercent + "%", true, false));
			pagePanel.add(createSpan(constants.completed(), false, true));
		} else {
			pagePanel.add(createSpan(constants.pageForPagination(), false, true));
			pagePanel.add(createSpan(currentPage == 0 ? "-" : "" + currentPage, true, false));
			pagePanel.add(createSpan("/", false, false));
			pagePanel.add(createSpan("" + totalPages, false, false));
		}

		progressBarPanel.add(pagePanel);
		progressBarPanel.add(progressBar);
	}

	private HTMLPanel createSpan(String text, boolean isHighlighted, boolean hideWhenSmall) {
		HTMLPanel pageCaption = new HTMLPanel(text);
		pageCaption.addStyleName("marginLeft7");
		if (isHighlighted) {
			pageCaption.addStyleName("highlightText");
		}
		if (hideWhenSmall) {
			pageCaption.addStyleName("hideSmall");
		}
		return pageCaption;
	}

	private void displayButton(final FocusPanel btn, final String buttonType, Image icon) {
		displayButton(btn, buttonType, icon, false);
	}

	private void displayButton(final FocusPanel btn, final String buttonType, Image icon, boolean invertIcon) {
		btn.clear();
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("btnPanel");
		buttonPanel.addStyleName(getItemName(buttonType));

		icon.addStyleName("icon");

		Label label = new Label(buttonType.toUpperCase());
		label.addStyleName("label");

		if (invertIcon) {
			buttonPanel.add(label);
			buttonPanel.add(icon);
		} else {
			buttonPanel.add(icon);
			buttonPanel.add(label);
		}

		btn.add(buttonPanel);

	}

	private String getItemName(String constant) {
		if (constant.equals(BUTTON_NEXT)) {
			return "next";
		} else if (constant.equals(BUTTON_PREVIOUS)) {
			return "previous";
		} else if (constant.equals(BUTTON_DETAILS)) {
			return "details";
		} else if (constant.equals(BUTTON_CHAT)) {
			return "chat";
		} else {
			return "notes";
		}
	}

	private static native void setupArrowsNavigation() /*-{
		$doc.onkeydown = function() {
			if ($wnd.event && $wnd.event.target
					&& $wnd.event.target.nodeName != "TEXTAREA") {
				switch ($wnd.event.keyCode) {
				case 37: //LEFT ARROW
					$doc.getElementsByClassName("btnPanel previous")[0].click();
					break;
				case 39: //RIGHT ARROW
					$doc.getElementsByClassName("btnPanel next")[0].click();
					break;
				}
			}
		};
	}-*/;

	@UiHandler("btnNext")
	public void btnNextClicked(ClickEvent e) {
		if (!showDetails && btnNext.getStyleName().indexOf("disabled") < 0){
			clientFactory.getEventBus().fireEvent(NavigationRequest.next());
			clientFactory.getEventBus().fireEvent(new ShowChatDockEvent(chatDockEnabled));
		}
	}

	@UiHandler("btnPrevious")
	public void btnPrevClicked(ClickEvent e) {
		if (!showDetails && btnPrevious.getStyleName().indexOf("disabled") < 0){
			clientFactory.getEventBus().fireEvent(NavigationRequest.prev());
			clientFactory.getEventBus().fireEvent(new ShowChatDockEvent(chatDockEnabled));
		}
	}

	@UiHandler("btnNotes")
	void handleClickBtnNotes(ClickEvent e) {
		if (notesPopup == null) {
			notesPopup = new NotesPopup(clientFactory.getKornellSession(), dean.getCourseClassTO()
					.getCourseClass().getUUID(), dean.getCourseClassTO().getEnrollment().getNotes());
			notesPopup.show();
		} else {
			notesPopup.show();
		}
	}

	@UiHandler("btnChat")
	void handleClickBtnChat(ClickEvent e) {
		if(!(chatDockEnabled && dean.getCourseClassTO().getCourseClass().isChatDockEnabled()) && !showDetails && btnChat.getStyleName().indexOf("disabled") < 0){
			clientFactory.getEventBus().fireEvent(new ShowChatDockEvent(!chatDockEnabled));
		}
	}

	private void enableButton(String btn, boolean enable) {
		if (BUTTON_NEXT.equals(btn)) {
			if (enable && !showDetails) {
				iconNext.setUrl(StringUtils.mkurl(SOUTH_BAR_IMAGES_PATH, getItemName(BUTTON_NEXT) + ".png"));
				btnNext.removeStyleName("disabled");
			} else {
				iconNext.setUrl(StringUtils.mkurl(SOUTH_BAR_IMAGES_PATH, getItemName(BUTTON_NEXT) + "Disabled.png"));
				btnNext.addStyleName("disabled");
			}
		} else if (BUTTON_PREVIOUS.equals(btn)) {
			if (enable && !showDetails) {
				iconPrevious.setUrl(StringUtils.mkurl(SOUTH_BAR_IMAGES_PATH, getItemName(BUTTON_PREVIOUS) + ".png"));
				btnPrevious.removeStyleName("disabled");
			} else {
				iconPrevious.setUrl(StringUtils.mkurl(SOUTH_BAR_IMAGES_PATH, getItemName(BUTTON_PREVIOUS) + "Disabled.png"));
				btnPrevious.addStyleName("disabled");
			}
		} else if (BUTTON_CHAT.equals(btn)) {
			if (enable && !showDetails) {
				iconChat.setUrl(StringUtils.mkurl(SOUTH_BAR_IMAGES_PATH, getItemName(BUTTON_CHAT) + ".png"));
				btnChat.removeStyleName("disabled");
			} else {
				iconChat.setUrl(StringUtils.mkurl(SOUTH_BAR_IMAGES_PATH, getItemName(BUTTON_CHAT) + "Disabled.png"));
				btnChat.addStyleName("disabled");
			}
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

	@Override
	public void onProgress(ProgressEvent event) {
		boolean isMonoSCO = (event.getTotalPages() <= 1);
		if (event.getCurrentPage() != 0) {
			updateProgressBarPanel(event.getCurrentPage(), event.getTotalPages(), event.getProgressPercent());
			enablePrev = event.hasPrevious();
			enableNext = event.hasNext();
			enableButton(BUTTON_PREVIOUS, enablePrev);
			enableButton(BUTTON_NEXT, enableNext);
		}
		btnNext.setVisible(!isMonoSCO);
		btnPrevious.setVisible(!isMonoSCO);
		progressBarPanel.setVisible(!isMonoSCO);
		if (isMonoSCO) {
			btnDetails.addStyleName("firstMonoSCO");
			btnNotes.addStyleName("last");
		} else {
			btnDetails.removeStyleName("firstMonoSCO");
			btnNotes.removeStyleName("last");
		}
		shouldShowActivityBar = isEnrolled && dean.getCourseClassTO() != null
				&& !CourseClassState.inactive.equals(dean.getCourseClassTO().getCourseClass().getState());
		clientFactory.getEventBus().fireEvent(new HideSouthBarEvent(!shouldShowActivityBar));
		this.setVisible(shouldShowActivityBar);
	}

	@UiHandler("btnDetails")
	void handleClickBtnDetails(ClickEvent e) {
		if (shouldShowActivityBar)
			clientFactory.getEventBus().fireEvent(new ShowDetailsEvent(!showDetails));
	}

	@Override
	public void onShowDetails(ShowDetailsEvent event) {
		this.showDetails = event.isShowDetails();
		if (showDetails) {
			clientFactory.getEventBus().fireEvent(new ShowChatDockEvent(false));
			btnDetails.addStyleName("btnSelected");
			displayButton(btnDetails, constants.course(),
					new Image(StringUtils.mkurl(SOUTH_BAR_IMAGES_PATH, getItemName(BUTTON_DETAILS) + ".png")));
		} else {
			if(!chatDockEnabled && dean.getCourseClassTO() != null && dean.getCourseClassTO().getCourseClass().isChatDockEnabled()){
				clientFactory.getEventBus().fireEvent(new ShowChatDockEvent(true));
			}
			btnDetails.removeStyleName("btnSelected");
			displayButton(btnDetails, BUTTON_DETAILS,
					new Image(StringUtils.mkurl(SOUTH_BAR_IMAGES_PATH, getItemName(BUTTON_DETAILS) + ".png")));
		}
		enableButton(BUTTON_PREVIOUS, !showDetails && enablePrev);
		enableButton(BUTTON_NEXT, !showDetails && enableNext);
		enableButton(BUTTON_CHAT, !showDetails);
		updateProgressBarPanel();
	}

	@Override
	public void onShowChatDock(ShowChatDockEvent event) {
		this.chatDockEnabled = event.isShowChatDock();
		if (chatDockEnabled) {
			if(showDetails){
				clientFactory.getEventBus().fireEvent(new ShowDetailsEvent(false));
			}
			btnChat.addStyleName("btnSelected");
		} else {
			btnChat.removeStyleName("btnSelected");
		}
	}

}

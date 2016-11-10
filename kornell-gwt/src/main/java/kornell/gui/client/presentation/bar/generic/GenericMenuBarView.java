package kornell.gui.client.presentation.bar.generic;

import static kornell.core.util.StringUtils.mkurl;

import java.util.List;
import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.NavWidget;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.UnreadChatThreadTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.event.ComposeMessageEvent;
import kornell.gui.client.event.CourseClassesFetchedEvent;
import kornell.gui.client.event.CourseClassesFetchedEventHandler;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.event.UnreadMessagesCountChangedEvent;
import kornell.gui.client.event.UnreadMessagesCountChangedEventHandler;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEvent;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEventHandler;
import kornell.gui.client.mvp.PlaceUtils;
import kornell.gui.client.presentation.admin.AdminPlace;
import kornell.gui.client.presentation.admin.courseclass.courseclasses.AdminCourseClassesPlace;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.presentation.message.MessagePlace;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.util.ClientConstants;
import kornell.gui.client.util.easing.Ease;
import kornell.gui.client.util.easing.Transitions;
import kornell.gui.client.util.easing.Updater;
import kornell.gui.client.util.view.Positioning;

public class GenericMenuBarView extends Composite implements MenuBarView,
		UnreadMessagesPerThreadFetchedEventHandler,
		UnreadMessagesCountChangedEventHandler,
		CourseClassesFetchedEventHandler {

	Logger logger = Logger.getLogger(GenericMenuBarView.class.getName());

	interface MyUiBinder extends UiBinder<Widget, GenericMenuBarView> {
	}

	// TODO: Dependency Injection
	ClientFactory clientFactory;
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private boolean visible = false;

	
	@UiField
	Label testEnvWarning;
	@UiField
	FlowPanel menuBar;
	@UiField
	NavWidget navWidgetFullScreen;
	@UiField
	Button btnFullScreen;
	@UiField
	NavWidget navWidgetProfile;
	@UiField
	Button btnProfile;
	@UiField
	NavWidget navWidgetHome;
	@UiField
	Button btnHome;
	@UiField
	NavWidget navWidgetAdmin;
	@UiField
	Button btnAdmin;
	@UiField
	NavWidget navWidgetMessages;
	@UiField
	Button btnMessages;
	@UiField
	Label messagesCount;
	@UiField
	NavWidget navWidgetHelp;
	@UiField
	Button btnHelp;
	@UiField
	NavWidget navWidgetExit;
	@UiField
	Button btnExit;
	@UiField
	Image imgMenuBar;
	@UiField
	GenericPlaceBarView placeBar;

	private KornellSession session;
	private EventBus bus;
	private boolean hasEmail;
	private int totalCount;
	private String imgMenuBarUrl;
	private boolean isLoaded;
	private boolean showingPlacePanel;
	private CourseClassesTO courseClassesTO;

	public GenericMenuBarView(final ClientFactory clientFactory,
			final ScrollPanel scrollPanel, CourseClassesTO courseClassesTO) {
		this.clientFactory = clientFactory;
		this.session = clientFactory.getKornellSession();
		this.bus = clientFactory.getEventBus();
		this.courseClassesTO = courseClassesTO;
		bus.addHandler(UnreadMessagesPerThreadFetchedEvent.TYPE, this);
		bus.addHandler(UnreadMessagesCountChangedEvent.TYPE, this);
		bus.addHandler(CourseClassesFetchedEvent.TYPE, this);
		initWidget(uiBinder.createAndBindUi(this));
		display();
		// TODO: Consider anonynous
		if (session != null) {
			String assetsURL = session.getAssetsURL();
			String skin = session.getInstitution().getSkin();
			String barLogoFileName = "logo300x45"
					+ (!"_light".equals(skin) ? "_light" : "") + ".png";
			imgMenuBarUrl = StringUtils.mkurl(assetsURL, barLogoFileName);
		}
		addOffsets(scrollPanel, clientFactory.getPlaceController().getWhere());
		clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						Place newPlace = event.getNewPlace();
						addOffsets(scrollPanel, newPlace);
					}
				});
	}

	private void addOffsets(final ScrollPanel scrollPanel, Place place) {
		showingPlacePanel = !(place instanceof VitrinePlace || place instanceof ClassroomPlace || place instanceof AdminPlace);
		if (place instanceof VitrinePlace) {
			setVisible(false);
			addStyleName("shy");
			scrollPanel.removeStyleName("offsetNorthBar");
			scrollPanel.removeStyleName("offsetNorthBarPlus");
			placeBar.setVisible(false);
			placeBar.clear();
		} else {
			loadAssets();
			if (showingPlacePanel) {
				scrollPanel.addStyleName("offsetNorthBarPlus");
				scrollPanel.removeStyleName("offsetNorthBar");
				placeBar.setVisible(true);
			} else {
				scrollPanel.addStyleName("offsetNorthBar");
				scrollPanel.removeStyleName("offsetNorthBarPlus");
				placeBar.setVisible(false);
				placeBar.clear();
			}
			showButtons(place);
            if(isVisible())
                return;
			final Widget widget = this.asWidget();
			final int point = (showingPlacePanel ? Positioning.NORTH_BAR_PLUS : Positioning.NORTH_BAR);
			widget.getElement().getStyle().setProperty("top", (point * -1) + "px");
			setVisible(true);
			removeStyleName("shy");
		
			Ease.out(Transitions.QUAD, new Updater() {
				@Override
				public void update(double progress) {
					int position = ((int) (point * progress)) - point;
					widget.getElement().getStyle().setProperty("top", position + "px");
				}
			}).run(Positioning.BAR_ANIMATION_LENGTH);
			
		}
	}

	private void loadAssets() {
		if (isLoaded)
			return;

		if (StringUtils.isNone(imgMenuBar.getUrl())) {
			imgMenuBar.setUrl(imgMenuBarUrl);
		}

		Timer screenfulJsTimer = new Timer() {
			public void run() {
				ScriptInjector
						.fromUrl(mkurl(ClientConstants.JS_PATH, "screenfull.min.js"))
						.setCallback(
								new com.google.gwt.core.client.Callback<Void, Exception>() {
									public void onFailure(Exception reason) {
										logger.severe("Screeenful script load failed.");
									}

									public void onSuccess(Void result) {
										isLoaded = true;
									}
								}).setWindow(ScriptInjector.TOP_WINDOW)
						.inject();
			}
		};

		// wait 2 secs before loading the javascript file
		screenfulJsTimer.schedule((int) (2 * 1000));
	}

	private void showButtons(Place newPlace) {
		boolean isRegistrationCompleted = !(newPlace instanceof TermsPlace || ((newPlace instanceof ProfilePlace || newPlace instanceof MessagePlace) && isProfileIncomplete()));

		boolean showHelp = hasHelpCourseClasses() && !(newPlace instanceof TermsPlace);

		showButton(navWidgetHelp, showHelp);
		showButton(navWidgetMessages, showHelp);
		showButton(navWidgetProfile, !(newPlace instanceof TermsPlace));

		showButton(navWidgetHome, isRegistrationCompleted);
		showButton(navWidgetFullScreen, isRegistrationCompleted);
		showButton(navWidgetAdmin, isRegistrationCompleted && clientFactory.getKornellSession().hasAnyAdminRole());
		showButton(navWidgetExit, true);

		if (showingPlacePanel) {
			menuBar.addStyleName("menuBarPlus");
		} else {
			menuBar.removeStyleName("menuBarPlus");
		}
	}

	private boolean hasHelpCourseClasses() {
		if(courseClassesTO != null){
			for (CourseClassTO courseClassTO : courseClassesTO.getCourseClasses()) {
				if (courseClassTO.getEnrollment() != null && !courseClassTO.getCourseClass().isInvisible()) {
					return true;
				}
			}
		}
		return false;
	}

	private void showButton(NavWidget btn, boolean show) {
		if (show) {
			btn.removeStyleName("shy");
		} else {
			btn.addStyleName("shy");
		}
	}

	public void display() {
		if (Window.Location.getHostName().indexOf("-test.ed") >= 0
				|| Window.Location.getHostName().indexOf("-homolog.ed") >= 0) {
			testEnvWarning.removeStyleName("shy");
			testEnvWarning.setText("HOMOLOG");
		} else if (Window.Location.getHostName().indexOf("-develop.ed") >= 0) {
			testEnvWarning.removeStyleName("shy");
			testEnvWarning.setText("DEVELOP");
		}
		btnFullScreen.removeStyleName("btn");
		btnProfile.removeStyleName("btn");
		btnHome.removeStyleName("btn");
		btnAdmin.removeStyleName("btn");
		btnHelp.removeStyleName("btn");
		btnExit.removeStyleName("btn");
		btnMessages.removeStyleName("btn");
	}

	private boolean isProfileIncomplete() {
		return (session.getCurrentUser().getInstitutionRegistrationPrefix() == null || session
				.getCurrentUser().getInstitutionRegistrationPrefix()
				.isShowContactInformationOnProfile())
				&& session.getInstitution()
						.isDemandsPersonContactDetails()
				&& session.getInstitution()
						.isValidatePersonContactDetails()
				&& StringUtils.isNone(clientFactory.getKornellSession()
						.getCurrentUser().getPerson().getCity());
	}

	static native void requestFullscreen() /*-{
		if ($wnd.screenfull.enabled)
			if (!$wnd.screenfull.isFullscreen)
				$wnd.screenfull.request();
			else
				$wnd.screenfull.exit();
	}-*/;

	@UiHandler("navWidgetFullScreen")
	void handleFullScreen(ClickEvent e) {
		requestFullscreen();
	}

	@UiHandler("navWidgetProfile")
	void handleProfile(ClickEvent e) {
		clientFactory.getPlaceController().goTo(
				new ProfilePlace(clientFactory.getKornellSession()
						.getCurrentUser().getPerson().getUUID(),
						isProfileIncomplete()));
	}

	@UiHandler("navWidgetHome")
	void handleHome(ClickEvent e) {
		if(clientFactory.getPlaceController().getWhere().toString().equals(clientFactory.getKornellSession().getHomePlace().toString())){
			PlaceUtils.reloadCurrentPlace(clientFactory.getEventBus(), clientFactory.getPlaceController());	
		} else {
			clientFactory.getPlaceController().goTo(clientFactory.getKornellSession().getHomePlace());
		}
	}

	@UiHandler("navWidgetAdmin")
	void handleAdmin(ClickEvent e) {
		clientFactory.getPlaceController().goTo(new AdminCourseClassesPlace());
	}

	@UiHandler("navWidgetExit")
	void handleExit(ClickEvent e) {
		clientFactory.getEventBus().fireEvent(new LogoutEvent());
	}

	@UiHandler("navWidgetHelp")
	void handleHelp(ClickEvent e) {
		bus.fireEvent(new ComposeMessageEvent(showingPlacePanel));
	}

	@UiHandler("navWidgetMessages")
	void handleMessages(ClickEvent e) {
		clientFactory.getPlaceController().goTo(new MessagePlace());
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	private void updateUnreadCount() {
		if(totalCount > 0){
			messagesCount.removeStyleName("shy");
			messagesCount.setText("" + totalCount);
		} else {
			messagesCount.addStyleName("shy");
		}
		showButtons(clientFactory.getPlaceController().getWhere());
	}

	@Override
	public void onUnreadMessagesPerThreadFetched(
			UnreadMessagesPerThreadFetchedEvent event) {
		if (event.getUnreadChatThreadTOs().size() > 0
				&& !btnMessages.isVisible())
			btnMessages.setVisible(true);
		int count = 0;
		for (UnreadChatThreadTO unreadChatThreadTO : event
				.getUnreadChatThreadTOs()) {
			count = count
					+ Integer.parseInt(unreadChatThreadTO.getUnreadMessages());
		}
		totalCount = count;
		updateUnreadCount();
	}

	@Override
	public void onUnreadMessagesCountChanged(
			UnreadMessagesCountChangedEvent event) {
		totalCount = event.isIncrement() ? totalCount + event.getCountChange()
				: totalCount - event.getCountChange();
		updateUnreadCount();
	}

	@Override
	public void onCourseClassesFetched(CourseClassesFetchedEvent event) {
		this.courseClassesTO = event.getCourseClassesTO();		
		showButtons(clientFactory.getPlaceController().getWhere());
	}
	
	@Override
	public void clearPlaceBar() {
		placeBar.clear();
	}

	@Override
	public void initPlaceBar(IconType iconType, String titleStr, String subtitleStr) {
		placeBar.init(iconType, titleStr, subtitleStr);
	}

	@Override
	public void setPlaceBarWidgets(List<IsWidget> widgets) {
		placeBar.setWidgets(widgets);
	}

}

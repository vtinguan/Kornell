package kornell.gui.client.presentation.bar.generic;

import java.util.logging.Logger;

import kornell.api.client.KornellSession;
import kornell.core.entity.Institution;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
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
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.home.AdminHomePlace;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.message.MessagePlace;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.WelcomePlace;

import com.github.gwtbootstrap.client.ui.Button;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericMenuBarView extends Composite implements MenuBarView, UnreadMessagesPerThreadFetchedEventHandler, UnreadMessagesCountChangedEventHandler, CourseClassesFetchedEventHandler {

	Logger logger = Logger.getLogger(GenericMenuBarView.class.getName());

	interface MyUiBinder extends UiBinder<Widget, GenericMenuBarView> {
	}

	// TODO: Dependency Injection
	ClientFactory clientFactory;
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static final String IMAGES_PATH = "skins/first/icons/menuBar/";

	private boolean visible = false;

	@UiField
	Label testEnvWarning;
	@UiField
	FlowPanel menuBar;
	@UiField
	Button btnFullScreen;
	@UiField
	Button btnProfile;
	@UiField
	Button btnHome;
	@UiField
	Button btnAdmin;
	@UiField
	Button btnNotifications;
	@UiField
	Button btnMessages;
	@UiField
	Button btnHelp;
	@UiField
	Button btnMenu;
	@UiField
	Button btnExit;
	@UiField
	Image imgMenuBar;
	private KornellSession session;
	private EventBus bus;
	private boolean hasEmail;
	private Label messagesCount;
	private int totalCount;
	private String imgMenuBarUrl;
	private boolean isLoaded;

	public GenericMenuBarView(final ClientFactory clientFactory, final ScrollPanel scrollPanel) {
		this.clientFactory = clientFactory;
		this.session = clientFactory.getKornellSession();
		this.bus = clientFactory.getEventBus();
		bus.addHandler(UnreadMessagesPerThreadFetchedEvent.TYPE, this);
		bus.addHandler(UnreadMessagesCountChangedEvent.TYPE, this);
		bus.addHandler(CourseClassesFetchedEvent.TYPE, this);
		initWidget(uiBinder.createAndBindUi(this));
		display();
		Dean localDean = Dean.getInstance();
		// TODO: Consider anonynous
		if (localDean != null) {
			Institution localInstitution = localDean.getInstitution();
			String assetsURL = localInstitution.getAssetsURL();
			String skin = Dean.getInstance().getInstitution().getSkin();
			String barLogoFileName = "logo300x45" + (!"_light".equals(skin) ? "_light" : "") + ".png";
			imgMenuBarUrl = StringUtils.composeURL(assetsURL, barLogoFileName);
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
    if (place instanceof VitrinePlace) {
			setVisible(false);
			addStyleName("shy");
			scrollPanel.removeStyleName("offsetNorthBar");
		} else {
			loadAssets();
			setVisible(true);
			removeStyleName("shy");
			scrollPanel.addStyleName("offsetNorthBar");
			showButtons(place);
		}
  }

	private void loadAssets() {
		if(isLoaded) return;
		
    if(StringUtils.isNone(imgMenuBar.getUrl())){
    	imgMenuBar.setUrl(imgMenuBarUrl);
    }

		Timer screenfulJsTimer = new Timer() {
			public void run() {
  	    ScriptInjector.fromUrl("/js/screenfull.min.js").setCallback(
  	 		     new com.google.gwt.core.client.Callback<Void, Exception>() {
  	 		        public void onFailure(Exception reason) {
  	 		          GWT.log("Script load failed.");
  	 		        }
  	 		        public void onSuccess(Void result) {
  	 		        	isLoaded = true;
  	 		        }
  	 		     }).setWindow(ScriptInjector.TOP_WINDOW).inject();
			}
		};

		//wait 2 secs before loading the javascript file
		screenfulJsTimer.schedule((int) (2 * 1000));
  }

	private void showButtons(Place newPlace) {
		boolean isRegistrationCompleted = !( newPlace instanceof TermsPlace
		|| ( (newPlace instanceof ProfilePlace || newPlace instanceof MessagePlace)
				&& isProfileIncomplete()));
		
		boolean showHelp = (Dean.getInstance().getHelpCourseClasses().size() > 0) && !(newPlace instanceof TermsPlace);
		
		showButton(btnHelp, showHelp);
		showButton(btnMessages, showHelp);
		showButton(btnProfile, !(newPlace instanceof TermsPlace));
		
		showButton(btnHome, isRegistrationCompleted);
		showButton(btnFullScreen, isRegistrationCompleted);
		showButton(btnAdmin, isRegistrationCompleted &&  
				(RoleCategory.hasRole(clientFactory.getKornellSession().getCurrentUser().getRoles(), RoleType.courseClassAdmin) 
						|| clientFactory.getKornellSession().isInstitutionAdmin()));
		showButton(btnNotifications, false);
		showButton(btnMenu, false);
		showButton(btnExit, true);
	}

	private void showButton(Button btn, boolean show) {
		if (show) {
			btn.removeStyleName("shy");
		} else {
			btn.addStyleName("shy");
		}
	}

	public void display() {
		if(Window.Location.getHostName().indexOf("-test.ed") >= 0 || Window.Location.getHostName().indexOf("-homolog.ed") >= 0){
			testEnvWarning.removeStyleName("shy");
			testEnvWarning.setText("HOMOLOG");
		} else if(Window.Location.getHostName().indexOf("-develop.ed") >= 0){
			testEnvWarning.removeStyleName("shy");
			testEnvWarning.setText("DEVELOP");
		}
		btnFullScreen.removeStyleName("btn");
		btnProfile.removeStyleName("btn");
		btnHome.removeStyleName("btn");
		btnAdmin.removeStyleName("btn");
		btnHelp.removeStyleName("btn");
		btnExit.removeStyleName("btn");

		btnNotifications.removeStyleName("btn");
		btnMessages.removeStyleName("btn");
		btnMenu.removeStyleName("btn");
	}
	
	private boolean isProfileIncomplete(){
		return Dean.getInstance().getInstitution().isDemandsPersonContactDetails()
				&& Dean.getInstance().getInstitution().isValidatePersonContactDetails() 
				&& StringUtils.isNone(clientFactory.getKornellSession().getCurrentUser().getPerson().getCity());
	}

	static native void requestFullscreen() /*-{
		if ($wnd.screenfull.enabled)
			if(!$wnd.screenfull.isFullscreen)
        $wnd.screenfull.request();
			else
        $wnd.screenfull.exit();
	}-*/;
	
	@UiHandler("btnFullScreen")
	void handleFullScreen(ClickEvent e) {
		requestFullscreen();
	}
	
	@UiHandler("btnProfile")
	void handleProfile(ClickEvent e) {
		clientFactory.getPlaceController().goTo(
				new ProfilePlace(clientFactory.getKornellSession()
						.getCurrentUser().getPerson().getUUID(), isProfileIncomplete()));
	}

	@UiHandler("btnHome")
	void handleHome(ClickEvent e) {
		clientFactory.getPlaceController().goTo(new WelcomePlace());
	}

	@UiHandler("btnAdmin")
	void handleAdmin(ClickEvent e) {
		clientFactory.getPlaceController().goTo(new AdminHomePlace());
	}

	@UiHandler("btnExit")
	void handleExit(ClickEvent e) {
		clientFactory.getEventBus().fireEvent(new LogoutEvent());
	}

	@UiHandler("btnHelp")
	void handleHelp(ClickEvent e) {
			bus.fireEvent(new ComposeMessageEvent());
	}
	
	@UiHandler("btnMessages")
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
		String labelText = totalCount > 0 ? ""+totalCount : "";
	  if(btnMessages.getWidgetCount() == 3){
			btnMessages.remove(2);
		}
		this.messagesCount = new Label(labelText);
		messagesCount.addStyleName("count");
		messagesCount.addStyleName("countMessages");
		btnMessages.add(messagesCount);
  }

	@Override
  public void onUnreadMessagesPerThreadFetched(UnreadMessagesPerThreadFetchedEvent event) {
		if(event.getUnreadChatThreadTOs().size() > 0 && !btnMessages.isVisible())
			btnMessages.setVisible(true);
		int count = 0;
		for (UnreadChatThreadTO unreadChatThreadTO : event.getUnreadChatThreadTOs()) {
			count = count + Integer.parseInt(unreadChatThreadTO.getUnreadMessages());
    }
		totalCount = count;
		updateUnreadCount();
  }

	@Override
  public void onUnreadMessagesCountChanged(UnreadMessagesCountChangedEvent event) {
	  totalCount = event.isIncrement() ? totalCount + event.getCountChange() : totalCount - event.getCountChange();
		updateUnreadCount();
  }

	@Override
  public void onCourseClassesFetched(CourseClassesFetchedEvent event) {
	  showButtons(clientFactory.getPlaceController().getWhere());
  }

}

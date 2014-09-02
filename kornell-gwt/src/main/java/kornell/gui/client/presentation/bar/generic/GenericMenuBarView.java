package kornell.gui.client.presentation.bar.generic;

import static kornell.core.util.StringUtils.isSome;

import java.util.logging.Logger;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Institution;
import kornell.core.entity.Person;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.to.UserInfoTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.event.ComposeMessageEvent;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.event.LoginEventHandler;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.event.UnreadMessagesFetchedEvent;
import kornell.gui.client.event.UnreadMessagesFetchedEventHandler;
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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericMenuBarView extends Composite implements MenuBarView,
		LoginEventHandler, UnreadMessagesFetchedEventHandler {

	Logger logger = Logger.getLogger(GenericMenuBarView.class.getName());

	interface MyUiBinder extends UiBinder<Widget, GenericMenuBarView> {
	}

	// TODO: Dependency Injection
	ClientFactory clientFactory;
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static final String IMAGES_PATH = "skins/first/icons/menuBar/";

	private boolean visible = false;

	@UiField
	FlowPanel testEnvWarning;
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

	public GenericMenuBarView(final ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.session = clientFactory.getKornellSession();
		this.bus = clientFactory.getEventBus();
		bus.addHandler(LoginEvent.TYPE, this);
		bus.addHandler(UnreadMessagesFetchedEvent.TYPE, this);
		initWidget(uiBinder.createAndBindUi(this));
		display();
		Dean localDean = Dean.getInstance();
		// TODO: Consider anonynous
		if (localDean != null) {
			Institution localInstitution = localDean.getInstitution();
			String assetsURL = localInstitution.getAssetsURL();
			String skin = Dean.getInstance().getInstitution().getSkin();
			String barLogoFileName = "logo250x45" + (!"_light".equals(skin) ? "_light" : "") + ".png?1";
			imgMenuBar.setUrl(StringUtils
					.composeURL(assetsURL, barLogoFileName));
		}
		clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						Place newPlace = event.getNewPlace();
						if (newPlace instanceof VitrinePlace) {
							GenericMenuBarView.this.setVisible(false);
							setVisible(false);
						} else {
							setVisible(true);
							if (newPlace instanceof TermsPlace
									|| (newPlace instanceof ProfilePlace
											&& Dean.getInstance()
													.getInstitution()
													.isDemandsPersonContactDetails() && clientFactory
											.getKornellSession()
											.getCurrentUser().getPerson()
											.getCity() == null)) {
								showButtons(false);
							} else {
								showButtons(true);
							}
							GenericMenuBarView.this.setVisible(true);
						}
					}
				});
		//initHelp();
	}

	private void initHelp() {
		btnHelp.setVisible(false);
		
		session.getCurrentUser(new Callback<UserInfoTO>() {
			@Override
			public void ok(final UserInfoTO user) {
				identifyUserVoice(user);
			}
		});

	}

	private void scheduleInitUserVoice() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				GenericMenuBarView.initUserVoice();
			}
		});
	}

	void identifyUserVoice(UserInfoTO user) {
		Person person = user.getPerson();
		hasEmail = false;
		if (person != null) {
			final String email = person.getEmail();
			final String personUUID = person.getUUID();
			final String name = person.getFullName();
			if (isSome(email)) {
				hasEmail = true;
				
				Element elHelp = btnHelp.getElement();
				elHelp.setId("btnHelp");
				elHelp.setAttribute("data-uv-trigger", "contact");
				scheduleInitUserVoice();
				
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						identifyUserVoiceNative(personUUID, name, email);						
					}
				});
			}			

		}
		btnHelp.setVisible(true);
	}

	static native void identifyUserVoiceNative(String personUUID, String name,
			String email) /*-{
		$wnd.UserVoice.push([ 'identify', {
			email : email,
			name : name,
			id : personUUID
		} ]);
	}-*/;

	static native void initUserVoice() /*-{
		$wnd.UserVoice.push([ 'set', {
			locale : 'pt-BR',
			screenshot_enabled : false,
			accent_color : '#9b020a'
		} ]);
		$wnd.UserVoice.push([ 'addTrigger', '#btnHelp', {} ]);
	}-*/;

	private void showButtons(boolean show) {
		showButton(btnFullScreen, show);
		showButton(btnProfile, show);
		showButton(btnHome, show);
		showButton(btnAdmin, show &&  
				(RoleCategory.hasRole(clientFactory.getKornellSession().getCurrentUser().getRoles(), RoleType.courseClassAdmin) 
						|| clientFactory.getKornellSession().isInstitutionAdmin()));
		showButton(btnNotifications, false);
		showButton(btnMessages, true);
		showButton(btnHelp, true);
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
		if(Window.Location.getHostName().indexOf("-test.eduvem") >= 0 || Window.Location.getHostName().indexOf("-develop.eduvem") >= 0){
			testEnvWarning.removeStyleName("shy");
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
						.getCurrentUser().getPerson().getUUID(), false));
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
	public void onLogin(UserInfoTO user) {
		identifyUserVoice(user);
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
  public void onUnreadMessagesFetched(UnreadMessagesFetchedEvent event) {
		if(btnMessages.getWidgetCount() == 3){
			btnMessages.remove(2);
		}
		String labelText = "0".equals(event.getUnreadMessagesCount()) ? "" : event.getUnreadMessagesCount();
		this.messagesCount = new Label(labelText);
		messagesCount.addStyleName("count");
		messagesCount.addStyleName("countMessages");
		btnMessages.add(messagesCount);
  }

}

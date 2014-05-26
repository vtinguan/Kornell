package kornell.gui.client.presentation.bar.generic;

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
import kornell.gui.client.Kornell;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.event.LoginEventHandler;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.AdminPlace;
import kornell.gui.client.presentation.admin.home.AdminHomePlace;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.WelcomePlace;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import static kornell.core.util.StringUtils.*;

public class GenericMenuBarView extends Composite implements MenuBarView,
		LoginEventHandler {
	Logger logger = Logger.getLogger(GenericMenuBarView.class.getName());

	interface MyUiBinder extends UiBinder<Widget, GenericMenuBarView> {
	}

	// TODO: Dependency Injection
	ClientFactory clientFactory;
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static final String IMAGES_PATH = "skins/first/icons/menuBar/";
	private String barLogoFileName = "logo250x45.png";

	private boolean visible = false;

	@UiField
	FlowPanel menuBar;
	@UiField
	Button btnFake;
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

	public GenericMenuBarView(final ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.session = clientFactory.getKornellSession();
		this.bus = clientFactory.getEventBus();
		bus.addHandler(LoginEvent.TYPE, this);
		initWidget(uiBinder.createAndBindUi(this));
		display();
		Dean localDean = Dean.getInstance();
		// TODO: Consider anonynous
		if (localDean != null) {
			Institution localInstitution = localDean.getInstitution();
			String assetsURL = localInstitution.getAssetsURL();
			// TODO: Add composeUrl to best practices
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
		initHelp();
	}

	private void initHelp() {
		btnHelp.setVisible(false);
		Element elHelp = btnHelp.getElement();
		elHelp.setId("btnHelp");
		elHelp.setAttribute("data-uv-trigger", "contact");
		scheduleInitUserVoice();
		
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
		boolean hasEmail = false;
		if (person != null) {
			final String email = person.getEmail();
			final String personUUID = person.getUUID();
			final String name = person.getFullName();
			if (isSome(email)) {
				hasEmail = true;
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						identifyUserVoiceNative(personUUID, name, email);						
					}
				});
			}			

		}
		btnHelp.setVisible(hasEmail);
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
		showButton(btnProfile, show);
		showButton(btnHome, show);
		showButton(btnAdmin, show &&  
				(RoleCategory.hasRole(clientFactory.getKornellSession().getCurrentUser().getRoles(), RoleType.courseClassAdmin) 
						|| clientFactory.getKornellSession().isInstitutionAdmin()));
		showButton(btnNotifications, false);
		showButton(btnMessages, false);
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
		displayButton(btnFake, "btnFake", "", false, "");
		displayButton(btnProfile, "btnProfile", "profile", true, "Perfil");
		displayButton(btnHome, "btnHome", "home", true, "Página Inicial");
		displayButton(btnAdmin, "btnAdmin", "admin", true, "Administração");
		displayButtonWithCount(btnNotifications, "btnNotifications",
				"notifications", "countNotifications", 19);
		displayButtonWithCount(btnMessages, "btnMessages", "messages",
				"countMessages", 99);
		displayButton(btnHelp, "btnHelp", "help", true, "Ajuda");
		displayButton(btnMenu, "btnMenu", "MENU", false, "");
		displayButton(btnExit, "btnExit", "SAIR", false, "Encerrar sessão");
	}

	private void displayButtonWithCount(Button btn, final String buttonType,
			String content, String countStyleName, Integer value) {
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("btnPanel");
		buttonPanel.addStyleName(buttonType);

		Image icon = new Image(IMAGES_PATH + content + ".png");
		icon.addStyleName("icon");
		buttonPanel.add(icon);

		Label count = new Label("" + value);
		count.addStyleName("count");
		count.addStyleName(countStyleName);
		buttonPanel.add(count);

		btn.add(buttonPanel);
		btn.removeStyleName("btn");
	}

	private void displayButton(Button btn, final String buttonType,
			String content, boolean isImage, String title) {
		btn.clear();

		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("btnPanel");
		buttonPanel.addStyleName(buttonType);

		if (isImage) {
			Image icon = new Image(IMAGES_PATH + content + ".png");
			icon.addStyleName("icon");
			icon.setTitle(title);
			buttonPanel.add(icon);
		} else {
			Label label = new Label(content);
			label.addStyleName("label");
			buttonPanel.add(label);
		}

		btn.add(buttonPanel);
		btn.removeStyleName("btn");
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

}

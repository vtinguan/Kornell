package kornell.gui.client.presentation.bar.generic;

import kornell.api.client.Callback;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.WelcomePlace;
import kornell.gui.client.util.ClientProperties;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class GenericMenuBarView extends Composite implements MenuBarView {
	interface MyUiBinder extends UiBinder<Widget, GenericMenuBarView> {
	}
	ClientFactory clientFactory;
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	private static final String IMAGES_PATH = "skins/first/icons/menuBar/";
	private String barLogoFileName = "logo250x45.png";
	
	@UiField
	FlowPanel menuBar;
	@UiField
	Button btnFake;
	@UiField
	Button btnProfile;
	@UiField
	Button btnHome;
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

	public GenericMenuBarView(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		initWidget(uiBinder.createAndBindUi(this));

		// TODO i18n
		try {
			String institutionAssetsURL = ClientProperties
					.getDecoded(ClientProperties.INSTITUTION_ASSETS_URL);
			imgMenuBar.setUrl(institutionAssetsURL + barLogoFileName);
		} catch (Exception e) {
			GWT.log("Couldn't find bar logo image.");
		}


		display();

		imgMenuBar.setUrl(clientFactory.getInstitution().getAssetsURL() + barLogoFileName);
		
		clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				Place newPlace = event.getNewPlace();
				boolean isAtVitrine = newPlace instanceof VitrinePlace;
				GenericMenuBarView.this.setVisible(!isAtVitrine);
			}
		});
	}

	public void display() {
		displayButton(btnFake, "btnFake", "", false, "");
		displayButton(btnProfile, "btnProfile", "profile", true, "Perfil");
		displayButton(btnHome, "btnHome", "home", true, "");
		displayButtonWithCount(btnNotifications, "btnNotifications",
				"notifications", "countNotifications", 19);
		displayButtonWithCount(btnMessages, "btnMessages", "messages",
				"countMessages", 99);
		displayButton(btnHelp, "btnHelp", "help", true, "");
		displayButton(btnMenu, "btnMenu", "MENU", false, "");
		displayButton(btnExit, "btnExit", "SAIR", false, "");

		Timer timer = new Timer() {
			@Override
			public void run() {
				if ("".equals(imgMenuBar.getUrl())) {
					imgMenuBar.setUrl("skins/first/icons/logo.png");
				}
			}
		};
		// If it was unable to fetch the logo, use the default logo
		timer.schedule(2000);
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
		clientFactory.getKornellClient().getCurrentUser(new Callback<UserInfoTO>() {
			@Override
			protected void ok(UserInfoTO userTO) {
				clientFactory.getPlaceController().goTo(new ProfilePlace(userTO.getUsername()));
			}
		});
	}

	@UiHandler("btnHome")
	void handleHome(ClickEvent e) {
		clientFactory.getPlaceController().goTo(new WelcomePlace());
	}

	@UiHandler("btnExit")
	void handleExit(ClickEvent e) {
		clientFactory.getEventBus().fireEvent(new LogoutEvent());
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

}

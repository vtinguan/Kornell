package kornell.gui.client.presentation.bar.generic;

import kornell.api.client.Callback;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.home.AdminHomePlace;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.terms.TermsPlace;
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

	public GenericMenuBarView(final ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		initWidget(uiBinder.createAndBindUi(this));
		display();
		imgMenuBar.setUrl(Dean.getInstance().getInstitution().getAssetsURL() + barLogoFileName);
		
		clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				Place newPlace = event.getNewPlace();
				if(newPlace instanceof VitrinePlace){
					GenericMenuBarView.this.setVisible(false);
				} else {
					if(newPlace instanceof TermsPlace || 
							(newPlace instanceof ProfilePlace && Dean.getInstance().getInstitution().isDemandsPersonContactDetails() &&
									clientFactory.getUserSession().getUserInfo().getPerson().getCity() == null)){
						showButtons(false);
					} else {
						showButtons(true);
					}
					GenericMenuBarView.this.setVisible(true);
				}
			}
		});
	}

	private void showButtons(boolean show) {
		showButton(btnProfile, show);
		showButton(btnHome, show);
		showButton(btnAdmin, show && clientFactory.getUserSession().isCourseClassAdmin());
		showButton(btnNotifications, false);
		showButton(btnMessages, false);
		showButton(btnHelp, false);
		showButton(btnMenu, false);
		showButton(btnExit, true);
	}

	private void showButton(Button btn, boolean show) {
		if(show){
			btn.removeStyleName("shy");
		} else {
			btn.addStyleName("shy");
		}
	}

	public void display() {
		displayButton(btnFake, "btnFake", "", false, "");
		displayButton(btnProfile, "btnProfile", "profile", true, "Perfil");
		displayButton(btnHome, "btnHome", "home", true, "");
		displayButton(btnAdmin, "btnAdmin", "admin", true, "Administração");
		displayButtonWithCount(btnNotifications, "btnNotifications", "notifications", "countNotifications", 19);
		displayButtonWithCount(btnMessages, "btnMessages", "messages", "countMessages", 99);
		displayButton(btnHelp, "btnHelp", "help", true, "");
		displayButton(btnMenu, "btnMenu", "MENU", false, "");
		displayButton(btnExit, "btnExit", "SAIR", false, "");
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

	private void displayButton(Button btn, final String buttonType, String content, boolean isImage, String title) {
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
		clientFactory.getPlaceController().goTo(new ProfilePlace(clientFactory.getUserSession().getUserInfo().getPerson().getUUID(), false));
	}

	@UiHandler("btnHome")
	void handleHome(ClickEvent e) {
		clientFactory.getPlaceController().goTo(clientFactory.getDefaultPlace());
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

}

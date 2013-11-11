package kornell.gui.client.presentation.vitrine.generic;

import static kornell.core.util.StringUtils.composeURL;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.util.ClientProperties;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class GenericVitrineView extends Composite implements VitrineView {
	interface MyUiBinder extends UiBinder<Widget, GenericVitrineView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private VitrineView.Presenter presenter;

	@UiField
	TextBox txtUsername;
	@UiField
	PasswordTextBox pwdPassword;
	@UiField
	Form frmLogin;
	@UiField
	Button btnLogin;
	@UiField
	Button btnRegister;
	@UiField
	Alert altUnauthorized;
	@UiField
	Image imgLogo;
	@UiField
	Alert userCreatedAlert;

	// TODO i18n xml
	public GenericVitrineView() {
		String imgLogoURL = ClientProperties.getDecoded("institutionAssetsURL");

		initWidget(uiBinder.createAndBindUi(this));
		
		if (imgLogoURL != null) {
			imgLogo.setUrl(composeURL(imgLogoURL, "logo300x80.png"));
		} else {
			imgLogo.setUrl("/skins/first/icons/logo.png");
		}
		
		pwdPassword.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (KeyCodes.KEY_ENTER == event.getCharCode())
					doLogin(null);
			}
		});
		
		txtUsername.getElement().setAttribute("autocorrect", "off");
		txtUsername.getElement().setAttribute("autocapitalize", "off");
		
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				txtUsername.setFocus(true);
			}
		});
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("btnLogin")
	void doLogin(ClickEvent e) {
		presenter.onLoginButtonClicked();
	}

	@UiHandler("btnRegister")
	void register(ClickEvent e) {
		presenter.onRegisterButtonClicked();
	}

	@Override
	public String getUsername() {
		return txtUsername.getValue();
	}

	@Override
	public String getPassword() {
		return pwdPassword.getValue();
	}

	@Override
	public void hideMessage() {
		altUnauthorized.setVisible(false);
	}

	@Override
	public void showMessage() {
		altUnauthorized.setVisible(true);
	}

	@Override
	public void setMessage(String msg) {
		altUnauthorized.setText(msg);
	}
	
	@Override
	public void showUserCreatedAlert(){
		userCreatedAlert.removeStyleName("shy");		
	}

}
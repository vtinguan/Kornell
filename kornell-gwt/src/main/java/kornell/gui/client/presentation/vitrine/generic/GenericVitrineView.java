package kornell.gui.client.presentation.vitrine.generic;

import static kornell.core.util.StringUtils.composeURL;

import java.util.List;

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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class GenericVitrineView extends Composite implements VitrineView {
	interface MyUiBinder extends UiBinder<Widget, GenericVitrineView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private VitrineView.Presenter presenter;

	@UiField
	Image imgLogo;
	
	@UiField
	FlowPanel loginPanel;
	@UiField
	Form frmLogin;
	@UiField
	TextBox txtUsername;
	@UiField
	PasswordTextBox pwdPassword;
	@UiField
	Button btnLogin;
	@UiField
	Button btnRegister;
	@UiField
	Alert alertError;
	@UiField
	Alert userCreatedAlert;
	
	@UiField
	FlowPanel signUpPanel;
	@UiField
	Form frmSignUp;

	@UiField
	TextBox suName;
	@UiField
	TextBox suEmail;
	@UiField
	PasswordTextBox suPasswordConfirm;
	@UiField
	PasswordTextBox suPassword;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;

	// TODO i18n xml
	public GenericVitrineView() {
		initWidget(uiBinder.createAndBindUi(this));
		displayLoginPanel(true);
		
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

	@UiHandler("btnOK")
	void signUp(ClickEvent e) {
		presenter.onSignUpButtonClicked();
	}

	@UiHandler("btnCancel")
	void cancelSignUp(ClickEvent e) {
		presenter.onCancelSignUpButtonClicked();
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
		alertError.setVisible(false);
	}

	@Override
	public void showMessage() {
		alertError.setVisible(true);
	}

	@Override
	public void setMessage(String msg) {
		alertError.setHTML(msg);
	}
	
	@Override
	public void setMessage(List<String> msgs){
		String errorsStr = "";
		for (String error : msgs) {
			errorsStr += error+"<br>";
		}
		alertError.setHTML(errorsStr);
	}
	
	@Override
	public void showUserCreatedAlert(){
		userCreatedAlert.removeStyleName("shy");		
	}
	
	@Override
	public void hideUserCreatedAlert(){
		userCreatedAlert.addStyleName("shy");		
	}

	@Override
	public void displayLoginPanel(boolean show) {
		loginPanel.setVisible(show);
		signUpPanel.setVisible(!show);
	}

	@Override
	public String getSuEmail() {
		return suEmail.getValue();
	}

	@Override
	public String getSuName() {
		return suName.getValue();
	}

	@Override
	public String getSuPassword() {
		return suPassword.getValue();
	}

	@Override
	public String getSuPasswordConfirm() {
		return suPasswordConfirm.getValue();
	}

	@Override
	public void setLogoURL(String imgLogoURL) {
		if (imgLogoURL != null) {
			imgLogo.setUrl(composeURL(imgLogoURL, "logo300x80.png"));
		} else {
			imgLogo.setUrl("/skins/first/icons/logo.png");
		}
	}

}
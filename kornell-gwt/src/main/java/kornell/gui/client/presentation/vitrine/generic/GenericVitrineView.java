package kornell.gui.client.presentation.vitrine.generic;

import static kornell.core.util.StringUtils.composeURL;

import java.util.List;

import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.vitrine.VitrineViewType;

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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class GenericVitrineView extends Composite implements VitrineView {
	interface MyUiBinder extends UiBinder<Widget, GenericVitrineView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private VitrineView.Presenter presenter;
	private VitrineViewType currentViewType = VitrineViewType.login;

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
	Button btnForgotPassword;
	@UiField
	Alert alertError;
	
	@UiField
	FlowPanel signUpPanel;
	@UiField
	Form frmSignUp;
	@UiField
	TextBox suName;
	@UiField
	TextBox suEmail;
	@UiField
	PasswordTextBox suPassword;
	@UiField
	PasswordTextBox suPasswordConfirm;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;
	
	@UiField
	FlowPanel forgotPasswordPanel;
	@UiField
	Form frmforgotPassword;
	@UiField
	TextBox fpEmail;
	@UiField
	Button btnOKFp;
	@UiField
	Button btnCancelFp;
	
	@UiField
	FlowPanel newPasswordPanel;
	@UiField
	Form frmNewPassword;
	@UiField
	PasswordTextBox newPassword;
	@UiField
	PasswordTextBox newPasswordConfirm;
	@UiField
	Button btnOKNewPassword;
	@UiField
	Button btnCancelNewPassword;
	
	
	// TODO i18n xml
	public GenericVitrineView() {
		initWidget(uiBinder.createAndBindUi(this));
		displayView(VitrineViewType.login);
		
		pwdPassword.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (KeyCodes.KEY_ENTER == event.getCharCode()){
					switch (currentViewType) {
					case login: doLogin(null);
						break;
					case register: signUp(null);
						break;
					case forgotPassword: requestPasswordChange(null);
						break;
					case newPassword: changePassword(null);
						break;
					default:
						break;
					}
				}
			}
		});
		
		txtUsername.getElement().setAttribute("autocorrect", "off");
		txtUsername.getElement().setAttribute("autocapitalize", "off");
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

	@UiHandler("btnForgotPassword")
	void forgotPassword(ClickEvent e) {
		presenter.onForgotPasswordButtonClicked();
	}

	@UiHandler("btnOK")
	void signUp(ClickEvent e) {
		presenter.onSignUpButtonClicked();
	}

	@UiHandler("btnCancel")
	void cancelSignUp(ClickEvent e) {
		presenter.onCancelSignUpButtonClicked();
	}

	@UiHandler("btnOKFp")
	void requestPasswordChange(ClickEvent e) {
		presenter.onRequestPasswordChangeButtonClicked();
	}

	@UiHandler("btnCancelFp")
	void cancelPasswordChangeRequest(ClickEvent e) {
		presenter.onCancelPasswordChangeRequestButtonClicked();
	}

	@UiHandler("btnOKNewPassword")
	void changePassword(ClickEvent e) {
		presenter.onChangePasswordButtonClicked();
	}

	@UiHandler("btnCancelNewPassword")
	void cancelChangePassword(ClickEvent e) {
		presenter.onCancelChangePasswordButtonClicked();
	}

	@Override
	public String getEmail() {
		return txtUsername.getValue();
	}

	@Override
	public void setEmail(String email) {
		txtUsername.setValue(email);
	}

	@Override
	public String getPassword() {
		return pwdPassword.getValue();
	}

	@Override
	public void setPassword(String password) {
		pwdPassword.setValue(password);
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
	public void displayView(VitrineViewType type){
		loginPanel.setVisible(false);
		signUpPanel.setVisible(false);
		forgotPasswordPanel.setVisible(false);
		newPasswordPanel.setVisible(false);
		switch (type) {
		case login: 
			loginPanel.setVisible(true);
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				public void execute() {
					txtUsername.setFocus(true);
				}
			});
			break;
		case register: 
			signUpPanel.setVisible(true);
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				public void execute() {
					suName.setFocus(true);
				}
			});
			break;
		case forgotPassword: 
			forgotPasswordPanel.setVisible(true);
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				public void execute() {
					fpEmail.setFocus(true);
				}
			});
			break;
		case newPassword: 
			newPasswordPanel.setVisible(true);
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				public void execute() {
					newPassword.setFocus(true);
				}
			});
			break;
		default:
			break;
		}
		
		currentViewType = type;
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
	public String getFpEmail() {
		return fpEmail.getValue();
	}

	@Override
	public String getNewPassword() {
		return newPassword.getValue();
	}

	@Override
	public String getNewPasswordConfirm() {
		return newPasswordConfirm.getValue();
	}

	@Override
	public void setLogoURL(String assetsURL) {
		imgLogo.setUrl(composeURL(assetsURL, "logo300x80.png"));
	}

	@Override
	public void setBackgroundImage(String assetsURL) {
		String style = "background: url('"+composeURL(assetsURL, "bgVitrine.jpg")+"') no-repeat center center fixed; " + 
				"-webkit-background-size: cover; " + 
				"-moz-background-size: cover; " + 
				"-o-background-size: cover; " + 
				"background-size: cover;";
		DOM.setElementAttribute(RootLayoutPanel.get().getElement(), "style", style);
	}

}
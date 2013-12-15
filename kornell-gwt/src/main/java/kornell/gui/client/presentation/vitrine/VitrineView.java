package kornell.gui.client.presentation.vitrine;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

public interface VitrineView extends IsWidget {
	public interface Presenter extends IsWidget {
		void onLoginButtonClicked();
		void onRegisterButtonClicked();
		void onSignUpButtonClicked();
		void onCancelSignUpButtonClicked();
		void onForgotPasswordButtonClicked();
		void onRequestPasswordChangeButtonClicked();
		void onCancelPasswordChangeRequestButtonClicked();
		void onChangePasswordButtonClicked();
		void onCancelChangePasswordButtonClicked();
	}

	void setPresenter(Presenter presenter);
	String getUsername();
	String getPassword();
	String getSuEmail();
	String getSuName();
	String getSuPassword();
	String getSuPasswordConfirm();
	String getFpEmail();
	String getNewPassword();
	String getNewPasswordConfirm();
	void hideMessage();
	void showMessage();
	void setMessage(String msg);
	void setMessage(List<String> msgs);
	void setLogoURL(String imgLogoURL);
	void displayView(VitrineViewType type);
	
}
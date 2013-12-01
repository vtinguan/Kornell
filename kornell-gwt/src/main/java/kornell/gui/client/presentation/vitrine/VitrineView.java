package kornell.gui.client.presentation.vitrine;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

public interface VitrineView extends IsWidget {
	public interface Presenter extends IsWidget {
		void onLoginButtonClicked();
		void onRegisterButtonClicked();
		void checkIfUserWasCreated();
		void onSignUpButtonClicked();
		void onCancelSignUpButtonClicked();
	}

	void setPresenter(Presenter presenter);

	
	String getUsername();
	String getPassword();
	String getSuEmail();
	String getSuName();
	String getSuPassword();
	String getSuPasswordConfirm();
	void hideMessage();
	void showMessage();
	void setMessage(String msg);
	void setMessage(List<String> msgs);
	void showUserCreatedAlert();
	void hideUserCreatedAlert();
	void displayLoginPanel(boolean b);
	void setLogoURL(String imgLogoURL);

	
	
}
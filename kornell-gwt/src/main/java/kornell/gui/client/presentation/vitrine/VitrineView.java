package kornell.gui.client.presentation.vitrine;

import com.google.gwt.user.client.ui.IsWidget;

public interface VitrineView extends IsWidget {
	public interface Presenter extends IsWidget {
		void onLoginButtonClicked();
		void onRegisterButtonClicked();
		void checkIfUserWasCreated();
	}

	void setPresenter(Presenter presenter);

	String getUsername();
	String getPassword();
	void hideMessage();
	void showMessage();
	void setMessage(String msg);
	void showUserCreatedAlert();
	
}
package kornell.gui.client.presentation.message.compose;

import kornell.gui.client.util.view.formfield.KornellFormFieldWrapper;

import com.google.gwt.user.client.ui.IsWidget;

public interface MessageComposeView  extends IsWidget {
	public interface Presenter extends IsWidget {

		void okButtonClicked();
		void init();
		void cancelButtonClicked();
	}

	void setPresenter(Presenter presenter);

	KornellFormFieldWrapper getMessageText();

	KornellFormFieldWrapper getRecipient();

	void show(String courseClassUUID);

	boolean checkErrors();

	void clearErrors();

}

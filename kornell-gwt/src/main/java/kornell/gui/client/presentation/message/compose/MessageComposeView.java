package kornell.gui.client.presentation.message.compose;

import kornell.core.entity.Message;
import kornell.gui.client.util.view.formfield.KornellFormFieldWrapper;

import com.google.gwt.user.client.ui.IsWidget;

public interface MessageComposeView  extends IsWidget {
	public interface Presenter extends IsWidget {

		void okButtonClicked();
		void init(Message message);
		void cancelButtonClicked();
	}

	void setPresenter(Presenter presenter);

	KornellFormFieldWrapper getSubject();

	KornellFormFieldWrapper getBody();

	KornellFormFieldWrapper getRecipient();

	void show(Message message);

	boolean checkErrors();

	void clearErrors();

}

package kornell.gui.client.presentation.message.inbox;

import kornell.core.entity.Message;
import kornell.gui.client.util.view.formfield.KornellFormFieldWrapper;

import com.google.gwt.user.client.ui.IsWidget;

public interface MessageInboxView  extends IsWidget {
	public interface Presenter extends IsWidget {

		void okButtonClicked();
	}

	void setPresenter(Presenter presenter);

}

package kornell.gui.client.presentation.message.compose;

import kornell.core.entity.Message;

import com.google.gwt.user.client.ui.IsWidget;

public interface MessageComposeView  extends IsWidget {
	public interface Presenter extends IsWidget {

		void okButtonClicked();

	}

	void setPresenter(Presenter presenter);

	Message getMessage();

}

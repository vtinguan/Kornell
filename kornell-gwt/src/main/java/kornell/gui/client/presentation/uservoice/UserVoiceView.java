package kornell.gui.client.presentation.uservoice;

import com.google.gwt.user.client.ui.IsWidget;

public interface UserVoiceView extends IsWidget {
	public interface Presenter extends IsWidget {
	}

	void setPresenter(Presenter presenter);


}

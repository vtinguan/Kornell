package kornell.gui.client.presentation.prototype;

import com.google.gwt.user.client.ui.IsWidget;

public interface PrototypeView extends IsWidget {
	public interface Presenter extends IsWidget {
	}

	void setPresenter(Presenter presenter);


}

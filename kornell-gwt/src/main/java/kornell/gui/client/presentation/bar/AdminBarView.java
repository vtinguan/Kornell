package kornell.gui.client.presentation.bar;

import com.google.gwt.user.client.ui.IsWidget;

public interface AdminBarView extends IsWidget {
	public interface Presenter extends IsWidget {
	}

	void setPresenter(Presenter presenter);

}

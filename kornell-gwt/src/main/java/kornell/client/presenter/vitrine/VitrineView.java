package kornell.client.presenter.vitrine;

import com.google.gwt.user.client.ui.IsWidget;

public interface VitrineView extends IsWidget {
	public interface Presenter extends IsWidget {
	}

	void setPresenter(Presenter presenter);

	
}
package kornell.gui.client.presentation.course;

import com.google.gwt.user.client.ui.IsWidget;

public interface CourseView  extends IsWidget {
	public interface Presenter extends IsWidget {
	}

	void setPresenter(Presenter presenter);
}

package kornell.gui.client.presentation.course.library;

import com.google.gwt.user.client.ui.IsWidget;

public interface CourseLibraryView  extends IsWidget {
	public interface Presenter extends IsWidget {
	}

	void setPresenter(Presenter presenter);
}

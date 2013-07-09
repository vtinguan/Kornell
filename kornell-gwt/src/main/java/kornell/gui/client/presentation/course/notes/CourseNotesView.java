package kornell.gui.client.presentation.course.notes;

import com.google.gwt.user.client.ui.IsWidget;

public interface CourseNotesView  extends IsWidget {
	public interface Presenter extends IsWidget {
	}

	void setPresenter(Presenter presenter);
}

package kornell.gui.client.presentation.course.course;

import com.google.gwt.user.client.ui.IsWidget;

public interface CourseHomeView  extends IsWidget {
	public interface Presenter extends IsWidget {
	}

	void setPresenter(Presenter presenter);
}

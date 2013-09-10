package kornell.gui.client.presentation.course.forum;

import com.google.gwt.user.client.ui.IsWidget;

public interface CourseForumView  extends IsWidget {
	public interface Presenter extends IsWidget {
	}

	void setPresenter(Presenter presenter);
}

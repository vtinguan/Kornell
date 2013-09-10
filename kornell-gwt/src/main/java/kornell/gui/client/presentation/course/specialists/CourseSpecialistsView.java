package kornell.gui.client.presentation.course.specialists;

import com.google.gwt.user.client.ui.IsWidget;

public interface CourseSpecialistsView  extends IsWidget {
	public interface Presenter extends IsWidget {
	}

	void setPresenter(Presenter presenter);
}

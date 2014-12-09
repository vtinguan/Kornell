package kornell.gui.client.presentation.admin.course.courses;

import java.util.List;

import kornell.core.entity.Course;

import com.google.gwt.user.client.ui.IsWidget;

public interface AdminCoursesView extends IsWidget {
	public interface Presenter extends IsWidget {
	}
	void setCourses(List<Course> list);
	void setPresenter(Presenter presenter);
}
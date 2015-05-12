package kornell.gui.client.presentation.admin.course.courses;

import java.util.List;

import kornell.core.entity.Course;
import kornell.gui.client.presentation.admin.PaginationPresenter;

import com.google.gwt.user.client.ui.IsWidget;

public interface AdminCoursesView extends IsWidget {
	public interface Presenter extends PaginationPresenter {
	}
	void setPresenter(AdminCoursesView.Presenter presenter);
	void setCourses(List<Course> courses, Integer count, Integer searchCount);
}
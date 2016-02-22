package kornell.gui.client.presentation.admin.courseclass.courseclasses;

import java.util.List;

import kornell.core.to.CourseClassTO;
import kornell.gui.client.util.view.PaginationPresenter;

import com.google.gwt.user.client.ui.IsWidget;

public interface AdminCourseClassesView extends IsWidget {
	public interface Presenter extends PaginationPresenter {
		void updateCourseClass(String courseClassUUID);
	}
	void setPresenter(AdminCourseClassesView.Presenter presenter);
	void setCourseClasses(List<CourseClassTO> courseClasses, Integer count, Integer searchCount);
}
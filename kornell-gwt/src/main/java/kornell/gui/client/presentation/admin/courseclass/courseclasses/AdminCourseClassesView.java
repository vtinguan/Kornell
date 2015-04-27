package kornell.gui.client.presentation.admin.courseclass.courseclasses;

import java.util.List;

import kornell.core.entity.CourseClass;
import kornell.core.to.CourseClassTO;
import kornell.gui.client.presentation.admin.PaginationPresenter;
import kornell.gui.client.presentation.admin.courseclass.courseclasses.AdminCourseClassesView.Presenter;

import com.google.gwt.user.client.ui.IsWidget;

public interface AdminCourseClassesView extends IsWidget {
	public interface Presenter extends PaginationPresenter {
		void upsertCourseClass(CourseClass courseClass);
		void updateCourseClass(String courseClassUUID);
	}
	void setPresenter(AdminCourseClassesView.Presenter presenter);
	void setCourseClasses(List<CourseClassTO> courseClasses, Integer count, Integer searchCount);
}
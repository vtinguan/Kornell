package kornell.gui.client.presentation.admin.courseclass.courseclasses;

import java.util.List;

import kornell.core.entity.CourseClass;
import kornell.core.to.CourseClassTO;

import com.google.gwt.user.client.ui.IsWidget;

public interface AdminCourseClassesView extends IsWidget {
	public interface Presenter extends IsWidget {
		void upsertCourseClass(CourseClass courseClass);

		void updateCourseClass(String courseClassUUID);
	}

	void setPresenter(AdminCourseClassesPresenter adminCourseClassesPresenter);

	void setCourseClasses(List<CourseClassTO> courseClasses);

	void setPresenter(Presenter presenter);
}
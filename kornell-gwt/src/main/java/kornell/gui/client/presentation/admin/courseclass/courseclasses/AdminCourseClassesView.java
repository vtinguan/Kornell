package kornell.gui.client.presentation.admin.courseclass.courseclasses;

import java.util.List;

import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.RegistrationType;
import kornell.core.to.CourseClassTO;
import kornell.core.to.EnrollmentTO;
import kornell.gui.client.presentation.admin.courseclass.courseclasses.AdminCourseClassesView.Presenter;

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
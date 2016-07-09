package kornell.gui.client.presentation.welcome;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.entity.EnrollmentProgressDescription;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.welcome.summary.CourseSummaryPresenter;

public interface WelcomeView  extends IsWidget {
	public interface Presenter extends IsWidget {
		void initData();
		boolean isEnrolled(CourseClassTO courseClassTO);
		CourseSummaryPresenter getCourseSummaryPresenter(CourseClassTO courseClassTO);
		EnrollmentProgressDescription getEnrollmentProgressDescription(CourseClassTO courseClassTO);
	}

	void setPresenter(Presenter presenter);
	void display(CourseClassesTO courseClassesTO, MenuBarView menuBarView);
}

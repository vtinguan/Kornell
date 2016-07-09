package kornell.gui.client.presentation.welcome.summary;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.to.CourseClassTO;

public interface CourseSummaryView  extends IsWidget {
	public interface Presenter extends IsWidget {
		void courseSummaryClicked(CourseClassTO courseClassTO);
		void initData();
		void requestEnrollmentButtonClicked(CourseClassTO courseClassTO);
	}

	void setPresenter(Presenter presenter);
}

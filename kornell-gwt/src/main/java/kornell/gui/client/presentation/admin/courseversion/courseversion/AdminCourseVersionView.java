package kornell.gui.client.presentation.admin.courseversion.courseversion;

import kornell.core.entity.CourseVersion;

import com.google.gwt.user.client.ui.IsWidget;

public interface AdminCourseVersionView extends IsWidget {
	public interface Presenter extends IsWidget {
		void upsertCourseVersion(CourseVersion courseVersion);
		CourseVersion getNewCourseVersion();
	}
	void setPresenter(Presenter presenter);
	Presenter getPresenter();
	void init();
}
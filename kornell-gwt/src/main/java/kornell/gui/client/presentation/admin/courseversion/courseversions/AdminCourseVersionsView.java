package kornell.gui.client.presentation.admin.courseversion.courseversions;

import java.util.List;

import kornell.core.entity.CourseVersion;
import kornell.gui.client.presentation.admin.PaginationPresenter;

import com.google.gwt.user.client.ui.IsWidget;

public interface AdminCourseVersionsView extends IsWidget {
	public interface Presenter extends PaginationPresenter {
	}
	void setPresenter(AdminCourseVersionsView.Presenter presenter);
	void setCourseVersions(List<CourseVersion> courseVersions, Integer count, Integer searchCount);
}
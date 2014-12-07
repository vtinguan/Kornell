package kornell.gui.client.presentation.admin.courseversion.courseversions;

import java.util.List;

import kornell.core.entity.CourseVersion;
import kornell.core.to.CourseVersionTO;

import com.google.gwt.user.client.ui.IsWidget;

public interface AdminCourseVersionsView extends IsWidget {
	public interface Presenter extends IsWidget {
	}
	void setCourseVersions(List<CourseVersion> list);
	void setPresenter(Presenter presenter);
}
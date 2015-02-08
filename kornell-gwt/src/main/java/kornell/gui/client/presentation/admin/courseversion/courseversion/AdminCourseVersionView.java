package kornell.gui.client.presentation.admin.courseversion.courseversion;

import java.util.List;

import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.CourseVersion;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.Institution;
import kornell.core.entity.RegistrationType;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseVersionTO;
import kornell.core.to.EnrollmentTO;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionView.Presenter;

import com.google.gwt.user.client.ui.IsWidget;

public interface AdminCourseVersionView extends IsWidget {
	public interface Presenter extends IsWidget {
		void upsertCourseVersion(CourseVersion courseVersion);
		CourseVersion getNewCourseVersion();
	}
	void setPresenter(Presenter presenter);
	void init();
}
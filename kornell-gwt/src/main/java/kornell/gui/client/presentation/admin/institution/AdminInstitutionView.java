package kornell.gui.client.presentation.admin.institution;

import java.util.List;

import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.RegistrationEnrollmentType;
import kornell.core.to.CourseClassTO;
import kornell.core.to.EnrollmentTO;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionView.Presenter;

import com.google.gwt.user.client.ui.IsWidget;

public interface AdminInstitutionView extends IsWidget {
	public interface Presenter extends IsWidget {
		void upsertCourseClass(CourseClass courseClass);

		void updateCourseClass(String courseClassUUID);
	}


	void setCourseClasses(List<CourseClassTO> courseClasses);

	void setPresenter(Presenter presenter);
}
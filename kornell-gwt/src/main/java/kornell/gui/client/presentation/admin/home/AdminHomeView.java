package kornell.gui.client.presentation.admin.home;

import java.util.List;

import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;

import com.google.gwt.user.client.ui.IsWidget;

public interface AdminHomeView extends IsWidget {
	public interface Presenter extends IsWidget {
		void changeEnrollmentState(Enrollment enrollment, EnrollmentState state);
		boolean showActionButton(String actionName, EnrollmentState state);
		void onAddEnrollmentButtonClicked(String fullName, String email);
		void onAddEnrollmentBatchButtonClicked(String txtAddEnrollmentBatch);
		void onGoToCourseButtonClicked();
		void onModalOkButtonClicked();
		void onUserClicked(String uuid);
	}

	void setPresenter(Presenter presenter);
	void setEnrollmentList(List<Enrollment> enrollments);
	void showModal();
	void setModalErrors(String errors);
	void setCourseClassName(String courseClassName);
	void setCourseName(String courseName);
}
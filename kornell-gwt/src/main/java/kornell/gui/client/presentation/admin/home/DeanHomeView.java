package kornell.gui.client.presentation.admin.home;

import java.util.List;

import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;

import com.google.gwt.user.client.ui.IsWidget;

public interface DeanHomeView extends IsWidget {
	public interface Presenter extends IsWidget {
		void changeEnrollmentState(Enrollment enrollment, EnrollmentState state);
		boolean showActionButton(String actionName, EnrollmentState state);
		void onAddEnrollmentButtonClicked(String fullName, String email);
		void onAddEnrollmentBatchButtonClicked(String txtAddEnrollmentBatch);
	}

	void setPresenter(Presenter presenter);
	void setEnrollmentList(List<Enrollment> enrollments);

}
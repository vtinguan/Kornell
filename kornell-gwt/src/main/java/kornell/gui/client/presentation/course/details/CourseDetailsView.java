package kornell.gui.client.presentation.course.details;

import kornell.core.lom.Contents;

import com.google.gwt.user.client.ui.IsWidget;

public interface CourseDetailsView  extends IsWidget {
	public interface Presenter extends IsWidget {
	}

	void setPresenter(Presenter presenter);

	void display(Contents contents);
}

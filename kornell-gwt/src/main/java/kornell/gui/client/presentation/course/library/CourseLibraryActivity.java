package kornell.gui.client.presentation.course.library;

import kornell.gui.client.ClientFactory;
import kornell.gui.client.presentation.course.course.CourseHomePresenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

public class CourseLibraryActivity extends AbstractActivity {
	
	private CourseLibraryPresenter presenter;

	public CourseLibraryActivity(CourseLibraryPresenter courseLibraryPresenter) {
		this.presenter = courseLibraryPresenter;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(presenter);		
	}
}

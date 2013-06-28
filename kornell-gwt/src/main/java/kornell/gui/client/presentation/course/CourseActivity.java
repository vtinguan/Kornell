package kornell.gui.client.presentation.course;

import kornell.gui.client.ClientFactory;
import kornell.gui.client.presentation.course.CoursePresenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

public class CourseActivity extends AbstractActivity {
	
	private CoursePresenter presenter;

	public CourseActivity(CoursePresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(presenter);		
	}
}

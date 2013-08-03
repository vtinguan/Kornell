package kornell.gui.client.presentation.course.forum;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class CourseForumActivity extends AbstractActivity {
	
	private CourseForumPresenter presenter;

	public CourseForumActivity(CourseForumPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(presenter);		
	}
}

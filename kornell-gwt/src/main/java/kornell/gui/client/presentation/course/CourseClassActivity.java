package kornell.gui.client.presentation.course;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;


public class CourseClassActivity extends AbstractActivity {
	
	private CourseClassPresenter presenter;

	public CourseClassActivity(CourseClassPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(presenter);		
	}
}
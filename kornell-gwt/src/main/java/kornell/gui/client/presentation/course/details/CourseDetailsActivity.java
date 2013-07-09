package kornell.gui.client.presentation.course.details;

import kornell.gui.client.ClientFactory;
import kornell.gui.client.presentation.course.course.CourseHomePresenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

public class CourseDetailsActivity extends AbstractActivity {
	
	private CourseDetailsPresenter presenter;

	public CourseDetailsActivity(CourseDetailsPresenter courseDetailsPresenter) {
		this.presenter = courseDetailsPresenter;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(presenter);		
	}
}

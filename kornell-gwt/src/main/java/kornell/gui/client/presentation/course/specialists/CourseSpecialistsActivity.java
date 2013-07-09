package kornell.gui.client.presentation.course.specialists;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class CourseSpecialistsActivity extends AbstractActivity {
	
	private CourseSpecialistsPresenter presenter;

	public CourseSpecialistsActivity(CourseSpecialistsPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(presenter);		
	}
}

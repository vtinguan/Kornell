package kornell.gui.client.presentation.course.notes;

import kornell.gui.client.ClientFactory;
import kornell.gui.client.presentation.course.notes.CourseNotesPresenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

public class CourseNotesActivity extends AbstractActivity {
	
	private CourseNotesPresenter presenter;

	public CourseNotesActivity(CourseNotesPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(presenter);		
	}
}

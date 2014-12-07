package kornell.gui.client.presentation.admin.course.course;

import kornell.gui.client.ClientFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class AdminCourseActivity extends AbstractActivity {
	private ClientFactory clientFactory;

	public AdminCourseActivity(ClientFactory clientFactory) {
	    this.clientFactory = clientFactory;
	  }

	@Override
	public void start(AcceptsOneWidget panel, com.google.gwt.event.shared.EventBus eventBus) {
		AdminCoursePresenter presenter = clientFactory.getViewFactory().getAdminCoursePresenter();
		panel.setWidget(presenter);
		
	}

}

package kornell.gui.client.presentation.activity;

import com.google.gwt.activity.shared.AbstractActivity;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

//TODO: Well... this name is awkward
public class ActivityActivity extends AbstractActivity {
	
	private ActivityPresenter presenter;

	public ActivityActivity(ActivityPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(presenter);
		
	}
}
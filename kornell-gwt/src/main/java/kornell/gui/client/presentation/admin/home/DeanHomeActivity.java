package kornell.gui.client.presentation.admin.home;

import kornell.gui.client.ClientFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class DeanHomeActivity extends AbstractActivity {
	private ClientFactory clientFactory;

	public DeanHomeActivity(ClientFactory clientFactory) {
	    this.clientFactory = clientFactory;
	  }

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {		
		DeanHomePresenter presenter = new DeanHomePresenter(clientFactory);
		panel.setWidget(presenter);
		
	}

}

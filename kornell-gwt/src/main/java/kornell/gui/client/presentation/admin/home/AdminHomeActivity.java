package kornell.gui.client.presentation.admin.home;

import kornell.gui.client.ClientFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class AdminHomeActivity extends AbstractActivity {
	private ClientFactory clientFactory;

	public AdminHomeActivity(ClientFactory clientFactory) {
	    this.clientFactory = clientFactory;
	  }

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {		
		AdminHomePresenter presenter = new AdminHomePresenter(clientFactory);
		panel.setWidget(presenter);
		
	}

}

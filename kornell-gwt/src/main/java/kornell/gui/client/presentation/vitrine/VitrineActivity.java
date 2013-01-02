package kornell.gui.client.presentation.vitrine;

import kornell.gui.client.ClientFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class VitrineActivity extends AbstractActivity {
	private ClientFactory clientFactory;

	public VitrineActivity(ClientFactory clientFactory) {
	    this.clientFactory = clientFactory;
	  }

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {		
		VitrinePresenter presenter = new VitrinePresenter(clientFactory);
		panel.setWidget(presenter);
		
	}

}

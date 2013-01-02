package kornell.client.activity;

import kornell.client.ClientFactory;
import kornell.client.presenter.welcome.WelcomePresenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

public class WelcomeActivity extends AbstractActivity{
	private ClientFactory clientFactory;
	private static WelcomePresenter presenter;
	public WelcomeActivity(ClientFactory clientFactory) {
	    this.clientFactory = clientFactory;
	    if(presenter == null){
	    this.presenter = new WelcomePresenter(clientFactory);
	    }
	 }
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		Widget widget = presenter.asWidget();
		panel.setWidget(widget);		
	}

}

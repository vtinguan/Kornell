package kornell.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class Kornell implements EntryPoint {

	@Override
	public void onModuleLoad() {
		  ClientFactory clientFactory = GWT.create(ClientFactory.class);
		  clientFactory.getApp().run(RootLayoutPanel.get());		
	}

}

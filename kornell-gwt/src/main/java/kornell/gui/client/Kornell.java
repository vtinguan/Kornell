package kornell.gui.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public class Kornell implements EntryPoint {
	
	Logger logger = Logger.getLogger(Kornell.class.getName());
	ClientFactory clientFactory = GWT.create(ClientFactory.class);

	@Override
	public void onModuleLoad() {
		long t0 = System.currentTimeMillis();
		startLMS();
		long t1 = System.currentTimeMillis();
		logger.info("Kornell GWT started in [" + (t1 - t0) + " ms]");
	}


	private void startLMS() {
		clientFactory.startApp();
		clientFactory.logState();
	}
	
	public ClientFactory getClientFactory(){
		return clientFactory;
	}

}

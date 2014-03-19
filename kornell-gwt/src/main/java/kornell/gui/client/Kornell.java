package kornell.gui.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class Kornell implements EntryPoint {
	Logger logger = Logger.getLogger(Kornell.class.getName());
	
	@Override
	public void onModuleLoad() {
		long t0 = System.currentTimeMillis();
		startLMS();
		long t1 = System.currentTimeMillis();
		logger.info("Kornell GWT started in ["+(t1-t0)+" ms]" );
	}

	private void startLMS() {
		ClientFactory clientFactory = GWT.create(ClientFactory.class);
		clientFactory.startApp();
		clientFactory.logState();
	}

}

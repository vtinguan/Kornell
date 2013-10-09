package kornell.gui.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class Kornell implements EntryPoint {
	Logger logger = Logger.getLogger(Kornell.class.getName());
	
	@Override
	public void onModuleLoad() {
		ClientFactory clientFactory = GWT.create(ClientFactory.class);
		clientFactory.startApp();
		logger.log(Level.INFO, "Application started.");
	}

}

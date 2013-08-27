package kornell.gui.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class Kornell implements EntryPoint {

	@Override
	public void onModuleLoad() {
	    Logger logger = Logger.getLogger("NameOfYourLogger");
	    logger.log(Level.SEVERE, "this message should get logged");
		ClientFactory clientFactory = GWT.create(ClientFactory.class);
		clientFactory.startApp();
	}

}

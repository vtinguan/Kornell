package kornell.gui.client.util;

import java.util.logging.Logger;

public class Debugger {
	public static final Logger logger = Logger.getLogger(Debugger.class.getName());

	public void ping(){
		logger.info("pong");
	}	
}

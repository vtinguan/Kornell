package kornell.gui.client.uidget;

import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;

public class OpenURLView extends Uidget {
	public static final Logger log = Logger.getLogger(OpenURLView.class.getName());
	
	FlowPanel panel = new FlowPanel();
	

	public OpenURLView(String URL) {
		log.info("*** open-url ["+System.currentTimeMillis()+"]");
		Frame frame = new Frame();
		frame.setHeight("600px");
		frame.setWidth("800px");
		
		frame.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				log.info("*** on-load ["+System.currentTimeMillis()+"]");
			}
		});
		
		frame.setUrl(URL);
		panel.add(frame);
		initWidget(panel);
	}
	

}

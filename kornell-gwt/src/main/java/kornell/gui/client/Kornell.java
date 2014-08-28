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

	@Override
	public void onModuleLoad() {
		long t0 = System.currentTimeMillis();
		startLMS();
		long t1 = System.currentTimeMillis();
		logger.info("Kornell GWT started in [" + (t1 - t0) + " ms]");
		BodyElement bodyElement = getBodyElement();
		DivElement div = Document.get().createDivElement();
		HTMLPanel panel = HTMLPanel.wrap(div);
		panel.add(new Label("BWA HA HA!!!!"));		
		bodyElement.appendChild(div);
	}

	public static native BodyElement getBodyElement() /*-{
		var win = window.open("", "win",
				"width=940,height=400,status=1,resizeable=1,scrollbars=1"); // a window object
		win.document.open("text/html", "replace");
		win.document.write("<html><body></body></html>");
		win.document.close();
		win.focus();
		return win.document.body;
	}-*/;

	private void startLMS() {
		ClientFactory clientFactory = GWT.create(ClientFactory.class);
		clientFactory.startApp();
		clientFactory.logState();
	}

}

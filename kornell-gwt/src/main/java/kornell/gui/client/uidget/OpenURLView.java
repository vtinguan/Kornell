package kornell.gui.client.uidget;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.ui.FlowPanel;

public class OpenURLView extends Uidget {
	FlowPanel panel = new FlowPanel();
	

	public OpenURLView(String URL) {
		IFrameElement iframe = createIFrame();
		iframe.setSrc(URL);
		panel.getElement().appendChild(iframe);
		initWidget(panel);
	}


	private IFrameElement createIFrame() {
		IFrameElement iframe = Document.get().createIFrameElement();
		return iframe;
	}
	
	

}

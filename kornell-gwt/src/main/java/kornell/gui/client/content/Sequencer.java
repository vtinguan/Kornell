package kornell.gui.client.content;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.place.shared.*;

public interface Sequencer 
	extends NavigationRequest.Handler {
	void displayOn(FlowPanel contentPanel);
}

package kornell.gui.client.sequence;

import com.google.gwt.user.client.ui.FlowPanel;

public interface Sequencer 
	extends NavigationRequest.Handler {
	void displayOn(FlowPanel contentPanel);
}

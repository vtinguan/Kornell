package kornell.gui.client.uidget;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;

import kornell.gui.client.event.ViewReadyEvent;
import kornell.gui.client.event.ViewReadyEventHandler;

public abstract class Uidget extends Composite {
	private ViewReadyEventHandler viewReadyEventHandler;

	public void onViewReady(ViewReadyEventHandler viewReadyEventHandler) {
		this.viewReadyEventHandler = viewReadyEventHandler;
	}

	protected void fireViewReady() {
		//GWT.log("READY ["+this+ "]");
		if(viewReadyEventHandler != null)
			viewReadyEventHandler.onViewReady(new ViewReadyEvent());
	}
}

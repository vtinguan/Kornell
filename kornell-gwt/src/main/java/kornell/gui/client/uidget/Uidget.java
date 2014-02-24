package kornell.gui.client.uidget;

import kornell.gui.client.event.ViewReadyEvent;
import kornell.gui.client.event.ViewReadyEventHandler;

import com.google.gwt.user.client.ui.Composite;

public abstract class Uidget extends Composite {
	private ViewReadyEventHandler viewReadyEventHandler;

	public void onViewReady(ViewReadyEventHandler viewReadyEventHandler) {
		this.viewReadyEventHandler = viewReadyEventHandler;
	}

	protected void fireViewReady() {		
		if(viewReadyEventHandler != null)
			viewReadyEventHandler.onViewReady(new ViewReadyEvent());
	}
}

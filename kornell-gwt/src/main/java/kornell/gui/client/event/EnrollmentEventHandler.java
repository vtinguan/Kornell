package kornell.gui.client.event;

import com.google.gwt.event.shared.EventHandler;

import kornell.core.to.EnrollmentLaunchTO;

public interface EnrollmentEventHandler extends EventHandler {
	void onEnrollmentLaunched(EnrollmentLaunchTO enrollmentLaunchTO);
}

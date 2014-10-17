package kornell.gui.client.event;

import kornell.core.to.EnrollmentLaunchTO;

import com.google.gwt.event.shared.GwtEvent;

public class EnrollmentEvent extends GwtEvent<EnrollmentEventHandler> {
	
	public static final Type<EnrollmentEventHandler> TYPE = new Type<EnrollmentEventHandler>();
	private EnrollmentLaunchTO enrollmentLaunchTO;
	
	public EnrollmentEvent(EnrollmentLaunchTO enrollmentLaunchTO){
		this.enrollmentLaunchTO = enrollmentLaunchTO;
	}
	
	@Override
	public Type<EnrollmentEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EnrollmentEventHandler handler) {
		handler.onEnrollmentLaunched(enrollmentLaunchTO);
	}

}

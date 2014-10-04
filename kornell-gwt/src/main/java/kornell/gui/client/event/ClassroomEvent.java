package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ClassroomEvent extends GwtEvent<ClassroomEventHandler>{

	public static final Type<ClassroomEventHandler> TYPE = new Type<ClassroomEventHandler>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ClassroomEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ClassroomEventHandler handler) {
		handler.onClassroomStarted(this);		
	}

}

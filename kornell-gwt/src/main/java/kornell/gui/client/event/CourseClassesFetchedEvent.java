package kornell.gui.client.event;

import java.util.List;

import kornell.core.to.UnreadChatThreadTO;

import com.google.gwt.event.shared.GwtEvent;

public class CourseClassesFetchedEvent extends GwtEvent<CourseClassesFetchedEventHandler>{
	public static final Type<CourseClassesFetchedEventHandler> TYPE = new Type<CourseClassesFetchedEventHandler>();
		
	public CourseClassesFetchedEvent() {
	}

	@Override
	protected void dispatch(CourseClassesFetchedEventHandler handler) {
		handler.onCourseClassesFetched(this);		
	}

	@Override
	public Type<CourseClassesFetchedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
}
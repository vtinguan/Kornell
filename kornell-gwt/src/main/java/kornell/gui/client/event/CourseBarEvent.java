package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CourseBarEvent extends GwtEvent<CourseBarEventHandler>{

	public static Type<CourseBarEventHandler> TYPE = new Type<CourseBarEventHandler>();
	
	private String courseBarItemSelected;
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CourseBarEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CourseBarEventHandler handler) {
		handler.onItemSelected(this);		
	}

	public String getCourseBarItemSelected() {
		return courseBarItemSelected;
	}

	public void setCourseBarItemSelected(String courseBarItemSelected) {
		this.courseBarItemSelected = courseBarItemSelected;
	}

}
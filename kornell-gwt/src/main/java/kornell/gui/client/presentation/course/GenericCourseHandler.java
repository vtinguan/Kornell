package kornell.gui.client.presentation.course;

import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.CourseBarEvent;
import kornell.gui.client.event.CourseBarEventHandler;
import kornell.gui.client.presentation.course.chat.CourseChatPlace;
import kornell.gui.client.presentation.course.course.CourseHomePlace;
import kornell.gui.client.presentation.course.details.CourseDetailsPlace;
import kornell.gui.client.presentation.course.forum.CourseForumPlace;
import kornell.gui.client.presentation.course.library.CourseLibraryPlace;
import kornell.gui.client.presentation.course.notes.CourseNotesPlace;
import kornell.gui.client.presentation.course.specialists.CourseSpecialistsPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;


public class GenericCourseHandler {

	private EventBus bus;
	private PlaceController placeCtrl;
	private String courseUUID;

	private static KornellConstants constants = GWT.create(KornellConstants.class);
	
	private static String COURSE_VIEW = constants.course();
	private static String DETAILS_VIEW = constants.details();
	private static String LIBRARY_VIEW = constants.library();
	private static String FORUM_VIEW = constants.forum();
	private static String CHAT_VIEW = constants.chat();
	private static String SPECIALISTS_VIEW = constants.specialists();
	private static String NOTES_VIEW = constants.notes();
	
	public GenericCourseHandler(EventBus eventBus, PlaceController placeCtrl, String courseUUID) {
		this.bus = eventBus;
		this.placeCtrl = placeCtrl;
		this.courseUUID = courseUUID;
		bus.addHandler(CourseBarEvent.TYPE, new CourseBarEventHandler() {
			@Override
			public void onItemSelected(CourseBarEvent event) {
				goTO(event.getCourseBarItemSelected());
			}
		});
	}
	
	private void goTO(String viewName) {
		if(COURSE_VIEW.equals(viewName)){
			placeCtrl.goTo(new CourseHomePlace(courseUUID));
			
		} else if(DETAILS_VIEW.equals(viewName)){
			placeCtrl.goTo(new CourseDetailsPlace(courseUUID));
			
		} else if(LIBRARY_VIEW.equals(viewName)){
			placeCtrl.goTo(new CourseLibraryPlace(courseUUID));
			
		} else if(FORUM_VIEW.equals(viewName)){
			placeCtrl.goTo(new CourseForumPlace(courseUUID));
			
		} else if(CHAT_VIEW.equals(viewName)){
			placeCtrl.goTo(new CourseChatPlace(courseUUID));
			
		} else if(SPECIALISTS_VIEW.equals(viewName)){
			placeCtrl.goTo(new CourseSpecialistsPlace(courseUUID));
			
		} else {
			placeCtrl.goTo(new CourseNotesPlace(courseUUID));
		}
	}

	public String getCourseUUID() {
		return courseUUID;
	}

	public void setCourseUUID(String courseUUID) {
		this.courseUUID = courseUUID;
	}
}
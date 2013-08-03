package kornell.gui.client.presentation.course.notes.generic;

import kornell.api.client.KornellClient;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.course.notes.CourseNotesView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;


public class GenericCourseNotesView extends Composite implements CourseNotesView {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseNotesView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	
	private KornellClient client;
	private PlaceController placeCtrl;
	private EventBus bus;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	
	
	public GenericCourseNotesView(EventBus eventBus, KornellClient client, PlaceController placeCtrl) {
		this.bus = eventBus;
		this.client = client;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		initData();		
	}
	
	private void initData() {
		/*client.getCourses(new Callback<CoursesTO>() {
			@Override
			protected void ok(CoursesTO to) {
				display();
			}
		});*/
		display();
	}


	private void display() {
	}

	@Override
	public void setPresenter(Presenter presenter) {
		// TODO Auto-generated method stub
		
	}


}
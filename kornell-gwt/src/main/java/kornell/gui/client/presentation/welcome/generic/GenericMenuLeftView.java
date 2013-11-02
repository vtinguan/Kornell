package kornell.gui.client.presentation.welcome.generic;


import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.to.CoursesTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericMenuLeftView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericMenuLeftView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	private KornellClient client;

	private PlaceController placeCtrl;
	
	private CoursesTO coursesTO;

	private final EventBus bus; 

	GenericMenuLeftItemView genericMenuLeftItemCourses;
	
	GenericMenuLeftItemView genericMenuLeftItemNotifications;
	
	GenericMenuLeftItemView genericMenuLeftItemMyParticipation;
	
	GenericMenuLeftItemView genericMenuLeftItemProfile;
	
	@UiField
	FlowPanel menuLeft;

	public GenericMenuLeftView(EventBus bus, KornellClient client, PlaceController placeCtrl) {
		this.bus = bus;
		this.client = client;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		initData();		
	}

	private void initData() {
		
		
		client.getCourses(new Callback<CoursesTO>() {
			@Override
			protected void ok(CoursesTO to) {
				coursesTO = to;
				display();
			}
		});
	}
	
	public void clearSelection(){
		genericMenuLeftItemCourses.setUnselected();
		genericMenuLeftItemNotifications.setUnselected();
		genericMenuLeftItemMyParticipation.setUnselected();
		genericMenuLeftItemProfile.setUnselected();
	}

	private void display() {
		genericMenuLeftItemCourses = new GenericMenuLeftItemView(bus, GenericMenuLeftItemView.MENU_ITEM_COURSES, this);
		menuLeft.add(genericMenuLeftItemCourses);
		
		genericMenuLeftItemNotifications = new GenericMenuLeftItemView(bus, GenericMenuLeftItemView.MENU_ITEM_NOTIFICATIONS, this);
		menuLeft.add(genericMenuLeftItemNotifications);
		
		genericMenuLeftItemMyParticipation = new GenericMenuLeftItemView(bus, GenericMenuLeftItemView.MENU_ITEM_MY_PARTICIPATION, this);
		menuLeft.add(genericMenuLeftItemMyParticipation);
		
		genericMenuLeftItemProfile = new GenericMenuLeftItemView(bus, GenericMenuLeftItemView.MENU_ITEM_PROFILE, this);
		menuLeft.add(genericMenuLeftItemProfile);
	}

}

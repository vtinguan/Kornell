package kornell.gui.client.presentation.welcome.generic;


import java.math.BigDecimal;

import kornell.core.shared.data.Course;
import kornell.core.shared.data.CourseTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.atividade.AtividadePlace;

import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Constants.DefaultStringValue;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GenericMenuLeftItemView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericMenuLeftItemView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField
	VerticalPanel pnlFields;
	
	@UiField
	Image imgIcon;
	
	private KornellConstants constants = GWT.create(KornellConstants.class);
	
	private String menuItemType;

	public static String MENU_ITEM_COURSES = "courses";
	public static String MENU_ITEM_NOTIFICATIONS = "notifications";
	public static String MENU_ITEM_MY_PARTICIPATION = "myParticipation";
	public static String MENU_ITEM_PROFILE = "profile";

	public GenericMenuLeftItemView(final PlaceController placeCtrl, String menuItemType) {
		this.menuItemType = menuItemType;
		initWidget(uiBinder.createAndBindUi(this));
		display();
		
		sinkEvents(Event.ONCLICK);
		addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
			}
		}, ClickEvent.getType());
		
	}

	private void display() {
		if(MENU_ITEM_COURSES.equals(menuItemType)){
			displayCourses();
		} else if(MENU_ITEM_NOTIFICATIONS.equals(menuItemType)){
			displayNotifications(); 
		} else if(MENU_ITEM_MY_PARTICIPATION.equals(menuItemType)){
			displayMyParticipation(); 
		} else if(MENU_ITEM_PROFILE.equals(menuItemType)){
			displayProfile(); 
		}
	}
	//TODO i18n
	private void displayCourses() {
		imgIcon.setUrl("skins/first/icons/menuLeftCourses.png");
		createHeader(constants.courses(), "paddingTop30");
		createInfo(constants.toStart()+constants.colon(), "1");		
		createInfo(constants.inProgress()+constants.colon(), "0");		
		createInfo(constants.finished()+constants.colon(), "0");		
	}

	private void displayNotifications() {
		imgIcon.setUrl("skins/first/icons/menuLeftNotifications.png");
		createHeader(constants.notifications(), "paddingTop44");
		createInfo(constants.networkActivities()+constants.colon(), "19");
	}

	private void displayMyParticipation() {
		imgIcon.setUrl("skins/first/icons/menuLeftMyParticipation.png");
		createHeader(constants.myParticipation(), "paddingTop37");
		createInfo(constants.messages()+constants.colon(), "0");
		createInfo(constants.forums()+constants.colon(), "0");
	}

	private void displayProfile() {
		imgIcon.setUrl("skins/first/icons/menuLeftProfile.png");
		createHeader(constants.profile(), "paddingTop44");
		createInfo(constants.complete(), "30%", true);
		
	}

	private void createHeader(String lblName, String styleName) {
		Label notifications = new Label(lblName);
		notifications.addStyleName("menuLeftHeader");
		notifications.addStyleName(styleName);
		pnlFields.add(notifications);
	}

	private void createInfo(String lblName, String value) {
		createInfo(lblName, value, false);
	}

	private void createInfo(String lblName, String value, boolean reverseLabel) {
		HorizontalPanel pnlNetworkActivities = new HorizontalPanel();
		Label networkActivities = new Label(lblName);
		networkActivities.addStyleName("menuLeftItemLabel");
		Label networkActivitiesValue = new Label(value);
		networkActivitiesValue.addStyleName("menuLeftItemValue");
		if(!reverseLabel){
			pnlNetworkActivities.add(networkActivities);
			pnlNetworkActivities.add(networkActivitiesValue);
		} else {
			pnlNetworkActivities.add(networkActivitiesValue);
			pnlNetworkActivities.add(networkActivities);
		}
		pnlFields.add(pnlNetworkActivities);
	}



}

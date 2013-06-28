package kornell.gui.client.presentation.bar.generic;

import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.CourseBarEvent;
import kornell.gui.client.presentation.bar.CourseBarView;
import kornell.gui.client.presentation.welcome.WelcomePlace;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;


public class GenericCourseBarView extends Composite implements CourseBarView {
	
	interface MyUiBinder extends UiBinder<Widget, GenericCourseBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	private static KornellConstants constants = GWT.create(KornellConstants.class);

	@UiField
	Button btnCourse;

	@UiField
	Button btnDetails;

	@UiField
	Button btnLibrary;

	@UiField
	Button btnForum;

	@UiField
	Button btnChat;

	@UiField
	Button btnSpecialists;

	@UiField
	Button btnNotes;

	@UiField
	Button btnBack;
	
	Button currentBtn = new Button();

	@UiField
	FlowPanel courseBar;

	private static String BUTTON_COURSE = constants.course();
	private static String BUTTON_DETAILS = constants.details();
	private static String BUTTON_LIBRARY = constants.library();
	private static String BUTTON_FORUM = constants.forum();
	private static String BUTTON_CHAT = constants.chat();
	private static String BUTTON_SPECIALISTS = constants.specialists();
	private static String BUTTON_NOTES = constants.notes();
	private static String BUTTON_BACK = constants.back();

	private EventBus bus;
	
	private PlaceController placeCtrl;
	
	public GenericCourseBarView(EventBus bus, PlaceController placeCtrl) {
		initWidget(uiBinder.createAndBindUi(this));
		this.bus = bus;
		this.placeCtrl = placeCtrl;
		display();
	}
	
	private void display(){
		displayButton(btnCourse, BUTTON_COURSE, true);
		displayButton(btnDetails, BUTTON_DETAILS);
		displayButton(btnLibrary, BUTTON_LIBRARY);
		displayButton(btnForum, BUTTON_FORUM);
		displayButton(btnChat, BUTTON_CHAT);
		displayButton(btnSpecialists, BUTTON_SPECIALISTS);
		displayButton(btnNotes, BUTTON_NOTES);
		displayButton(btnBack, BUTTON_BACK);	
	}
	
	private void displayButton(final Button btnCourse, final String buttonType, boolean active) {
		//TODO i18n
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("btnPanel");
		buttonPanel.addStyleName(getItemName(buttonType));

		Image icon = new Image("skins/first/icons/courseBar/"+getItemName(buttonType)+".png");
		icon.addStyleName("icon");
		buttonPanel.add(icon);
		
		Label label = new Label(buttonType.toUpperCase());
		label.addStyleName("label");
		buttonPanel.add(label);
		
		btnCourse.add(buttonPanel);
		
		if(active)
			setSelected(btnCourse);
		else
			setUnselected(btnCourse);
	}

	private void displayButton(Button btnCourse, String buttonType) {
		displayButton(btnCourse, buttonType, false);
	}

	public void setSelected(Button btnCourse){
		setUnselected(currentBtn);
		currentBtn = btnCourse;
		
		btnCourse.removeStyleName("inactiveCourseButton");
		btnCourse.addStyleName("activeCourseButton");
	}

	public void setUnselected(Button btnCourse){
		btnCourse.removeStyleName("activeCourseButton");
		btnCourse.addStyleName("inactiveCourseButton");
	}
	
	public void clearSelection(){
		setUnselected(btnCourse);
		setUnselected(btnDetails);
		setUnselected(btnLibrary);
		setUnselected(btnForum);
		setUnselected(btnChat);
		setUnselected(btnSpecialists);
		setUnselected(btnNotes);
		setUnselected(btnBack);
	}
	
	@UiHandler("btnCourse")
	void handleClickBtnCourse(ClickEvent e) {
		fireEvent(BUTTON_COURSE);
		setSelected(btnCourse);
	}
	
	@UiHandler("btnDetails")
	void handleClickBtnDetails(ClickEvent e) {
		fireEvent(BUTTON_DETAILS);
		setSelected(btnDetails);
	}
	
	@UiHandler("btnLibrary")
	void handleClickBtnLibrary(ClickEvent e) {
		fireEvent(BUTTON_LIBRARY);
		setSelected(btnLibrary);
	}
	
	@UiHandler("btnForum")
	void handleClickBtnForum(ClickEvent e) {
		fireEvent(BUTTON_FORUM);
		setSelected(btnForum);
	}
	
	@UiHandler("btnChat")
	void handleClickBtnChat(ClickEvent e) {
		fireEvent(BUTTON_CHAT);
		setSelected(btnChat);
	}
	
	@UiHandler("btnSpecialists")
	void handleClickBtnSpecialists(ClickEvent e) {
		fireEvent(BUTTON_SPECIALISTS);
		setSelected(btnSpecialists);
	}
	
	@UiHandler("btnNotes")
	void handleClickBtnNotes(ClickEvent e) {
		fireEvent(BUTTON_NOTES);
		setSelected(btnNotes);
	}
	
	@UiHandler("btnBack")
	void handleClickBtnBack(ClickEvent e) {
		fireEvent(BUTTON_BACK);
		placeCtrl.goTo(new WelcomePlace());
	}

	private void fireEvent(String buttonType) {
		CourseBarEvent courseBarEvent = new CourseBarEvent();
		courseBarEvent.setCourseBarItemSelected(buttonType);
		bus.fireEvent(courseBarEvent);
	}
	
	private String getItemName(String constant){
		if(constant.equals(BUTTON_COURSE)){
			return "course";
		} else if(constant.equals(BUTTON_DETAILS)) {
			return "details";
		} else if(constant.equals(BUTTON_LIBRARY)) {
			return "library";
		} else if(constant.equals(BUTTON_FORUM)) {
			return "forum";
		} else if(constant.equals(BUTTON_CHAT)) {
			return "chat";
		} else if(constant.equals(BUTTON_SPECIALISTS)) {
			return "specialists";
		} else if(constant.equals(BUTTON_NOTES)) {
			return "notes";
		}   else {
			return "back";
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

}

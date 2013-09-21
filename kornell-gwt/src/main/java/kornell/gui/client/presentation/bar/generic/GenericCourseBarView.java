	package kornell.gui.client.presentation.bar.generic;

import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.CourseBarEvent;
import kornell.gui.client.presentation.bar.CourseBarView;
import kornell.gui.client.presentation.course.chat.CourseChatPlace;
import kornell.gui.client.presentation.course.course.CourseHomePlace;
import kornell.gui.client.presentation.course.details.CourseDetailsPlace;
import kornell.gui.client.presentation.course.forum.CourseForumPlace;
import kornell.gui.client.presentation.course.library.CourseLibraryPlace;
import kornell.gui.client.presentation.course.specialists.CourseSpecialistsPlace;
import kornell.gui.client.presentation.welcome.WelcomePlace;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
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
	
	private static final String IMAGES_PATH = constants.imagesPath() + "southBar/";

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
		displayButton(btnCourse, BUTTON_COURSE, placeCtrl.getWhere() instanceof CourseHomePlace);
		displayButton(btnDetails, BUTTON_DETAILS, placeCtrl.getWhere() instanceof CourseDetailsPlace);
		displayButton(btnLibrary, BUTTON_LIBRARY, placeCtrl.getWhere() instanceof CourseLibraryPlace);
		displayButton(btnForum, BUTTON_FORUM, placeCtrl.getWhere() instanceof CourseForumPlace);
		displayButton(btnChat, BUTTON_CHAT, placeCtrl.getWhere() instanceof CourseChatPlace);
		displayButton(btnSpecialists, BUTTON_SPECIALISTS, placeCtrl.getWhere() instanceof CourseSpecialistsPlace);
		displayButton(btnBack, BUTTON_BACK, false);	
	}
	
	private void displayButton(final Button btn, final String buttonType, boolean active) {
		//TODO i18n
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("btnPanel");
		buttonPanel.addStyleName(getItemName(buttonType));

		Image icon = new Image(IMAGES_PATH + getItemName(buttonType)+".png");
		icon.addStyleName("icon");
		buttonPanel.add(icon);
		
		Label label = new Label(buttonType.toUpperCase());
		label.addStyleName("label");
		buttonPanel.add(label);
		
		btn.add(buttonPanel);
		btn.removeStyleName("btn");
		
		if(active)
			setSelected(btn);
		else
			setUnselected(btn);
	}

	public void setSelected(Button btn){
		setUnselected(currentBtn);
		currentBtn = btn;
		btn.removeStyleName("btnNotSelected");
		btn.addStyleName("btnSelected");
	}

	public void setUnselected(Button btn){
		btn.removeStyleName("btnSelected");
		btn.addStyleName("btnNotSelected");
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

	@Override
	public void updateSelection(Place place){
		if(place instanceof CourseHomePlace){
			setSelected(btnCourse);
		} else if(place instanceof CourseDetailsPlace){
			setSelected(btnDetails);
		} else if(place instanceof CourseLibraryPlace){
			setSelected(btnLibrary);
		} else if(place instanceof CourseForumPlace){
			setSelected(btnForum);
		} else if(place instanceof CourseChatPlace){
			setSelected(btnChat);
		} else if(place instanceof CourseSpecialistsPlace){
			setSelected(btnSpecialists);
		}
	}
	
	@UiHandler("btnCourse")
	void handleClickBtnCourse(ClickEvent e) {
		placeCtrl.goTo(new CourseHomePlace(getCourseUUID()));
		setSelected(btnCourse);
	}
	
	@UiHandler("btnDetails")
	void handleClickBtnDetails(ClickEvent e) {
		placeCtrl.goTo(new CourseDetailsPlace(getCourseUUID()));
		setSelected(btnDetails);
	}
	
	@UiHandler("btnLibrary")
	void handleClickBtnLibrary(ClickEvent e) {
		placeCtrl.goTo(new CourseLibraryPlace(getCourseUUID()));
		setSelected(btnLibrary);
	}
	
	@UiHandler("btnForum")
	void handleClickBtnForum(ClickEvent e) {
		placeCtrl.goTo(new CourseForumPlace(getCourseUUID()));
		setSelected(btnForum);
	}
	
	@UiHandler("btnChat")
	void handleClickBtnChat(ClickEvent e) {
		placeCtrl.goTo(new CourseChatPlace(getCourseUUID()));
		setSelected(btnChat);
	}
	
	@UiHandler("btnSpecialists")
	void handleClickBtnSpecialists(ClickEvent e) {
		placeCtrl.goTo(new CourseSpecialistsPlace(getCourseUUID()));
		setSelected(btnSpecialists);
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
	
	private String getCourseUUID() {
		try{		
			return Window.Location.getHash().split(":")[1].split(";")[0];
		} catch (Exception ex){
			placeCtrl.goTo(new WelcomePlace());
		}
		return null;
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}


}

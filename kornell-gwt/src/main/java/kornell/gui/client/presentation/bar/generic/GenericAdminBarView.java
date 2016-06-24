package kornell.gui.client.presentation.bar.generic;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import kornell.gui.client.ClientFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.admin.course.CoursePlace;
import kornell.gui.client.presentation.admin.course.courses.AdminCoursesPlace;
import kornell.gui.client.presentation.admin.courseclass.courseclasses.AdminCourseClassesPlace;
import kornell.gui.client.presentation.admin.courseversion.CourseVersionPlace;
import kornell.gui.client.presentation.admin.courseversion.courseversions.AdminCourseVersionsPlace;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionPlace;
import kornell.gui.client.presentation.bar.AdminBarView;


public class GenericAdminBarView extends Composite implements AdminBarView {
	
	interface MyUiBinder extends UiBinder<Widget, GenericAdminBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	private static KornellConstants constants = GWT.create(KornellConstants.class);

	private static String BUTTON_INSTITUTION = constants.institution();
	private static String BUTTON_COURSE = constants.courses();
	private static String BUTTON_COURSE_VERSION = constants.versions();
	private static String BUTTON_COURSE_CLASS = constants.classes();

	
	@UiField
	Button btnInstitution;
	@UiField
	Button btnCourse;
	@UiField
	Button btnCourseVersion;
	@UiField
	Button btnCourseClass;
	@UiField
	FlowPanel activityBar;
	
	private ClientFactory clientFactory;
	
	public GenericAdminBarView(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		initWidget(uiBinder.createAndBindUi(this));
		
		display();
		updateButtonByPlace(clientFactory.getPlaceController().getWhere());

		clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						updateButtonByPlace(event.getNewPlace());
					}
				});
	}
	
	private void updateButtonByPlace(Place place){
		if(place instanceof AdminInstitutionPlace)
			updateSelection(BUTTON_INSTITUTION);
		else if(place instanceof CoursePlace)
			updateSelection(BUTTON_COURSE);
		else if(place instanceof CourseVersionPlace)
			updateSelection(BUTTON_COURSE_VERSION);
		else
			updateSelection(BUTTON_COURSE_CLASS);
	}
	 
	private void display(){
		displayButton(btnInstitution, BUTTON_INSTITUTION, new Icon(IconType.BUILDING));	
		displayButton(btnCourse, BUTTON_COURSE, new Icon(IconType.FOLDER_OPEN));	
		displayButton(btnCourseVersion, BUTTON_COURSE_VERSION, new Icon(IconType.TAGS));	
		displayButton(btnCourseClass, BUTTON_COURSE_CLASS, new Icon(IconType.BOOK));	
		updateSelection(BUTTON_COURSE_CLASS);
	}

	private void displayButton(final Button btn, final String buttonType, Icon icon) {
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("btnPanel");
		buttonPanel.addStyleName(getItemName(buttonType));

		icon.addStyleName("label");
		icon.addStyleName("font16");
		
		Label label = new Label(buttonType.toUpperCase());
		label.addStyleName("label");
		
		buttonPanel.add(icon);
		buttonPanel.add(label);
		
		btn.add(buttonPanel);
		btn.removeStyleName("btn");
		btn.removeStyleName("btn-link");
	}
	
	private String getItemName(String constant){
		if(constant.equals(BUTTON_COURSE)){
			return "course";
		} else if(constant.equals(BUTTON_COURSE_VERSION)){
			return "courseVersion";
		} else if(constant.equals(BUTTON_COURSE_CLASS)){
			return "courseClass";
		} else if(constant.equals(BUTTON_INSTITUTION)){
			return "institution";
		}
		return "";
	}
	
	private void updateSelection(String button){
		btnInstitution.removeStyleName("btnSelected");
		btnCourse.removeStyleName("btnSelected");
		btnCourseVersion.removeStyleName("btnSelected");
		btnCourseClass.removeStyleName("btnSelected");
		if(button.equals(BUTTON_COURSE)){
			btnCourse.addStyleName("btnSelected");	
		} else if(button.equals(BUTTON_COURSE_VERSION)){
			btnCourseVersion.addStyleName("btnSelected");	
		} else if(button.equals(BUTTON_COURSE_CLASS)){
			btnCourseClass.addStyleName("btnSelected");	
		} else if(button.equals(BUTTON_INSTITUTION)){
			btnInstitution.addStyleName("btnSelected");	
		}
	}
	
	@UiHandler("btnCourse")
	void handleClickBtnCourse(ClickEvent e) {
		updateSelection(BUTTON_COURSE);
		clientFactory.getPlaceController().goTo(new AdminCoursesPlace());
	}
	
	@UiHandler("btnCourseVersion")
	void handleClickBtnCourseVersion(ClickEvent e) {
		updateSelection(BUTTON_COURSE_VERSION);
		clientFactory.getPlaceController().goTo(new AdminCourseVersionsPlace());
	}
	
	@UiHandler("btnCourseClass")
	void handleClickBtnCourseClass(ClickEvent e) {
		updateSelection(BUTTON_COURSE_CLASS);
		clientFactory.getPlaceController().goTo(new AdminCourseClassesPlace());
	}
	
	@UiHandler("btnInstitution")
	void handleClickBtnInstitution(ClickEvent e) {
		updateSelection(BUTTON_INSTITUTION);
		clientFactory.getPlaceController().goTo(new AdminInstitutionPlace());
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
	}
	
}
package kornell.gui.client.presentation.bar.generic;

import kornell.core.to.UserInfoTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.HistoryMapper;
import kornell.gui.client.presentation.admin.home.AdminHomePlace;
import kornell.gui.client.presentation.bar.AdminBarView;
import kornell.gui.client.presentation.course.generic.notes.NotesPopup;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


public class GenericAdminBarView extends Composite implements AdminBarView {
	
	interface MyUiBinder extends UiBinder<Widget, GenericAdminBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
		
	private String page;
	
	private NotesPopup notesPopup;
	
	private final HistoryMapper historyMapper = GWT.create(HistoryMapper.class);
	
	private static KornellConstants constants = GWT.create(KornellConstants.class);

	private static final String IMAGES_PATH = constants.imagesPath() + "southBar/";

	private static String BUTTON_INSTITUTION = constants.institution();
	private static String BUTTON_COURSE_CLASS = constants.classes();

	
	@UiField
	Button btnCourseClass;
	@UiField
	Button btnInstitution;

	@UiField
	FlowPanel activityBar;
	
	private UserInfoTO user;
	private ClientFactory clientFactory;
	
	public GenericAdminBarView(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		initWidget(uiBinder.createAndBindUi(this));

		user = clientFactory.getKornellSession().getCurrentUser();
		display();
	}
	 
	private void display(){
		displayButton(btnInstitution, BUTTON_INSTITUTION, new Icon(IconType.BUILDING));	
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
		if(constant.equals(BUTTON_COURSE_CLASS)){
			return "courseClass";
		} else if(constant.equals(BUTTON_INSTITUTION)){
			return "institution";
		}
		return "";
	}
	
	private void updateSelection(String button){
		btnInstitution.removeStyleName("btnSelected");
		btnCourseClass.removeStyleName("btnSelected");
		if(button.equals(BUTTON_COURSE_CLASS)){
			btnCourseClass.addStyleName("btnSelected");	
		} else if(button.equals(BUTTON_INSTITUTION)){
			btnInstitution.addStyleName("btnSelected");	
		}
	}
	
	@UiHandler("btnCourseClass")
	void handleClickBtnCourseClass(ClickEvent e) {
		updateSelection(BUTTON_COURSE_CLASS);
		clientFactory.getPlaceController().goTo(new AdminHomePlace());
	}
	
	@UiHandler("btnInstitution")
	void handleClickBtnInstitution(ClickEvent e) {
		//clientFactory.getPlaceController().goTo(new AdminInstitutionPlace());
		updateSelection(BUTTON_INSTITUTION);
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
	}
	
}
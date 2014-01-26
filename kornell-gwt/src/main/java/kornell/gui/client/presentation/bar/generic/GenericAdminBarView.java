package kornell.gui.client.presentation.bar.generic;

import kornell.core.to.UserInfoTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.NavigationForecastEvent;
import kornell.gui.client.presentation.HistoryMapper;
import kornell.gui.client.presentation.admin.home.AdminHomeView.Presenter;
import kornell.gui.client.presentation.bar.AdminBarView;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.course.details.CourseDetailsPlace;
import kornell.gui.client.presentation.course.notes.NotesPopup;
import kornell.gui.client.sequence.NavigationRequest;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
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

	private static String BUTTON_PREVIOUS = constants.previous();
	private static String BUTTON_NEXT = constants.next();
	private static String BUTTON_DETAILS = constants.details();
	private static String BUTTON_NOTES = constants.notes();

	Image iconPrevious;
	Image iconNext;

	@UiField
	Button btnPrevious;
	@UiField
	Button btnNext;
	@UiField
	Button btnDetails;
	@UiField
	Button btnNotes;

	@UiField
	FlowPanel activityBar;
	
	private UserInfoTO user;
	private ClientFactory clientFactory;
	
	public GenericAdminBarView(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		initWidget(uiBinder.createAndBindUi(this));
		
		clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						Place newPlace = event.getNewPlace();
						
						if(newPlace instanceof ClassroomPlace){							
							btnDetails.removeStyleName("btnSelected");
						} else if(newPlace instanceof CourseDetailsPlace){
							//enableButton(BUTTON_PREVIOUS, false);
							//enableButton(BUTTON_NEXT, false);
							btnDetails.addStyleName("btnSelected");
						}
						
					}});

		user = clientFactory.getUserSession().getUserInfo();
		display();
		
		setUpArrowNavigation();
	}
	 
	private void display(){
		iconPrevious = new Image(IMAGES_PATH + getItemName(BUTTON_PREVIOUS)+".png");
		displayButton(btnPrevious, BUTTON_PREVIOUS, iconPrevious);
		
		iconNext = new Image(IMAGES_PATH + getItemName(BUTTON_NEXT)+".png");
		displayButton(btnNext, BUTTON_NEXT, iconNext, true);	
		
		displayButton(btnDetails, BUTTON_DETAILS, new Image(IMAGES_PATH + getItemName(BUTTON_DETAILS)+".png"));	
		
		displayButton(btnNotes, BUTTON_NOTES, new Image(IMAGES_PATH + getItemName(BUTTON_NOTES)+".png"));	
		
		
		if (clientFactory.getPlaceController().getWhere() instanceof CourseDetailsPlace) {
			btnDetails.addStyleName("btnSelected");
			//enableButton(BUTTON_PREVIOUS, false);
			//enableButton(BUTTON_NEXT, false);
		}
	}

	private void displayButton(final Button btn, final String buttonType, Image icon) {
		displayButton(btn, buttonType, icon, false);
	}
	
	private void displayButton(final Button btn, final String buttonType, Image icon, boolean invertIcon) {
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("btnPanel");
		buttonPanel.addStyleName(getItemName(buttonType));
		
		icon.addStyleName("icon");
		
		Label label = new Label(buttonType.toUpperCase());
		label.addStyleName("label");
		
		if(invertIcon){
			buttonPanel.add(label);
			buttonPanel.add(icon);
		} else {
			buttonPanel.add(icon);
			buttonPanel.add(label);
		}
		
		btn.add(buttonPanel);
		btn.removeStyleName("btn");
		btn.removeStyleName("btn-link");
	}
	
	private String getItemName(String constant){
		if(constant.equals(BUTTON_NEXT)){
			return "next";
		} else if(constant.equals(BUTTON_PREVIOUS)) {
			return "previous";
		} else if(constant.equals(BUTTON_DETAILS)){
			return "details";
		} else {
			return "notes";
		}
	}

	private static native void setUpArrowNavigation() /*-{
		$doc.onkeydown = function() {
			if($wnd.event.target.nodeName != "TEXTAREA"){
			    switch ($wnd.event.keyCode) {
			        case 37: //LEFT ARROW
			        	$doc.getElementsByClassName("btnPanel previous")[0].click();
			            break;
			        case 39: //RIGHT ARROW
			        	$doc.getElementsByClassName("btnPanel next")[0].click();
			            break;
			    }
			}
		};
	}-*/;
	
	@UiHandler("btnNext")
	public void btnNextClicked(ClickEvent e){
		clientFactory.getEventBus().fireEvent(NavigationRequest.next());
	}

	@UiHandler("btnPrevious")
	public void btnPrevClicked(ClickEvent e){
		clientFactory.getEventBus().fireEvent(NavigationRequest.prev());		
	}
	
	@UiHandler("btnDetails")
	void handleClickBtnDetails(ClickEvent e) {
		if(clientFactory.getPlaceController().getWhere() instanceof ClassroomPlace){
			clientFactory.getPlaceController().goTo(new CourseDetailsPlace(clientFactory.getCurrentCourseClass().getCourseClass().getUUID()));
			btnDetails.addStyleName("btnSelected");
			GWT.log("btnSelected");
		} else {
			//TODO remove this
			clientFactory.getPlaceController().goTo(new ClassroomPlace(clientFactory.getCurrentCourseClass().getCourseClass().getUUID()));
			btnDetails.removeStyleName("btnSelected");
		}
		
	}
	
	@UiHandler("btnNotes")
	void handleClickBtnNotes(ClickEvent e) {
		if(notesPopup == null){
			notesPopup = new NotesPopup(clientFactory.getUserSession(), 
					clientFactory.getCurrentCourseClass().getCourseClass().getUUID(), 
					clientFactory.getCurrentCourseClass().getEnrollment().getNotes());
			notesPopup.show();
		} else {
			notesPopup.show();
		}
	}
	
	private void enableButton(String btn, boolean enable){
		if(BUTTON_NEXT.equals(btn)){
			if(enable){
				iconNext.setUrl(IMAGES_PATH + getItemName(BUTTON_NEXT)+".png");
				btnNext.removeStyleName("disabled");
			} else {
				iconNext.setUrl(IMAGES_PATH + getItemName(BUTTON_NEXT)+"Disabled.png");
				btnNext.addStyleName("disabled");
			}
		}
		else if(BUTTON_PREVIOUS.equals(btn)){
			if(enable){
				iconPrevious.setUrl(IMAGES_PATH + getItemName(BUTTON_PREVIOUS)+".png");
				btnPrevious.removeStyleName("disabled");
			} else {
				iconPrevious.setUrl(IMAGES_PATH + getItemName(BUTTON_PREVIOUS)+"Disabled.png");
				btnPrevious.addStyleName("disabled");
			}
		}
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
	}
	
}

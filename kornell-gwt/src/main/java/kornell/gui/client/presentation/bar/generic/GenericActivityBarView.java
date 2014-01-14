package kornell.gui.client.presentation.bar.generic;

import kornell.core.to.UserInfoTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.NavigationForecastEvent;
import kornell.gui.client.event.NavigationForecastEventHandler;
import kornell.gui.client.event.ProgressChangeEvent;
import kornell.gui.client.event.ProgressChangeEventHandler;
import kornell.gui.client.presentation.HistoryMapper;
import kornell.gui.client.presentation.bar.ActivityBarView;
import kornell.gui.client.presentation.course.CourseClassPlace;
import kornell.gui.client.presentation.course.details.CourseDetailsPlace;
import kornell.gui.client.presentation.course.notes.NotesPopup;
import kornell.gui.client.sequence.NavigationRequest;

import com.github.gwtbootstrap.client.ui.ProgressBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


public class GenericActivityBarView extends Composite implements ActivityBarView, NavigationForecastEventHandler, ProgressChangeEventHandler {
	
	interface MyUiBinder extends UiBinder<Widget, GenericActivityBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
		
	private String page;
	
	private NotesPopup notesPopup;
	private FlowPanel progressBarPanel;
	private final HistoryMapper historyMapper = GWT.create(HistoryMapper.class);
	
	private static KornellConstants constants = GWT.create(KornellConstants.class);

	private static final String IMAGES_PATH = constants.imagesPath() + "southBar/";

	private static String BUTTON_PREVIOUS = constants.previous();
	private static String BUTTON_NEXT = constants.next();
	private static String BUTTON_DETAILS = constants.details();
	private static String BUTTON_NOTES = constants.notes();

	private Image iconPrevious;
	private Image iconNext;

	@UiField
	FocusPanel btnPrevious;
	@UiField
	FocusPanel btnNext;
	@UiField
	FocusPanel btnDetails;
	@UiField
	FocusPanel btnNotes;
	@UiField
	FocusPanel btnProgress;
	
	@UiField
	FlowPanel activityBar;
	
	private UserInfoTO user;
	private ClientFactory clientFactory;
	
	private Integer currentPage, totalPages, progressPercent;
	
	public GenericActivityBarView(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		initWidget(uiBinder.createAndBindUi(this));
		clientFactory.getEventBus().addHandler(NavigationForecastEvent.TYPE,this);
		clientFactory.getEventBus().addHandler(ProgressChangeEvent.TYPE,this);

		clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						Place newPlace = event.getNewPlace();
						
						if(newPlace instanceof CourseClassPlace){							
							btnDetails.removeStyleName("btnSelected");
							updateProgressBarPanel();
						} else if(newPlace instanceof CourseDetailsPlace){
							//enableButton(BUTTON_PREVIOUS, false);
							//enableButton(BUTTON_NEXT, false);
							btnDetails.addStyleName("btnSelected");
							updateProgressBarPanel();
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
		
		displayProgressButton();
		if (clientFactory.getPlaceController().getWhere() instanceof CourseDetailsPlace) {
			btnDetails.addStyleName("btnSelected");
			//enableButton(BUTTON_PREVIOUS, false);
			//enableButton(BUTTON_NEXT, false);
		}
	}

	private void displayProgressButton() {
		progressBarPanel = new FlowPanel ();
		progressBarPanel.addStyleName("progressBarPanel");
				
		btnProgress.add(progressBarPanel);
		btnProgress.removeStyleName("btn");
		btnProgress.removeStyleName("btn-link");
	}
	
	private void updateProgressBarPanel(Integer currentPage, Integer totalPages, Integer progressPercent){
		this.currentPage = currentPage;
		this.totalPages = totalPages;
		this.progressPercent = progressPercent;
		updateProgressBarPanel();
	}
	
	private void updateProgressBarPanel(){
		progressBarPanel.clear();
		
		ProgressBar  progressBar = new ProgressBar();
		progressBar.setPercent(progressPercent);
		
		FlowPanel pagePanel = new FlowPanel();
		pagePanel.addStyleName("pagePanel");
		pagePanel.addStyleName("label");
		
		if(clientFactory.getPlaceController().getWhere() instanceof CourseClassPlace){
			pagePanel.add(createSpan("Página", false));
			pagePanel.add(createSpan(""+currentPage, true));
			pagePanel.add(createSpan("/", false));
			pagePanel.add(createSpan(""+totalPages, false));
		} else {
			pagePanel.add(createSpan(progressPercent+"%", true));
			pagePanel.add(createSpan("concluído", false));
		}
		
		progressBarPanel.add(pagePanel);
		progressBarPanel.add(progressBar);
	}

	private HTMLPanel createSpan(String text, boolean isHighlighted) {
		HTMLPanel pageCaption = new HTMLPanel(text);
		pageCaption.addStyleName("marginLeft7");
		if(isHighlighted){
			pageCaption.addStyleName("highlightText");
		}
		return pageCaption;
	}

	private void displayButton(final FocusPanel btn, final String buttonType, Image icon) {
		displayButton(btn, buttonType, icon, false);
	}
	
	private void displayButton(final FocusPanel btn, final String buttonType, Image icon, boolean invertIcon) {
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
		if(clientFactory.getPlaceController().getWhere() instanceof CourseClassPlace){
			clientFactory.getPlaceController().goTo(new CourseDetailsPlace(clientFactory.getCurrentCourseClass().getCourseClass().getUUID()));
			btnDetails.addStyleName("btnSelected");
			GWT.log("btnSelected");
		} else {
			//TODO remove this
			clientFactory.getPlaceController().goTo(new CourseClassPlace(clientFactory.getCurrentCourseClass().getCourseClass().getUUID()));
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
	public void onNextActivityNotOK(NavigationForecastEvent evt) {
		//TODO: uncomment
		//enableButton(BUTTON_NEXT, true);
	}

	@Override
	public void onNextActivityOK(NavigationForecastEvent evt) {
		//enableButton(BUTTON_NEXT, true);
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
	}

	@Override
	public void onProgressChange(ProgressChangeEvent event) {
		updateProgressBarPanel(event.getCurrentPage(), event.getTotalPages(), event.getProgressPercent());
		enableButton(BUTTON_PREVIOUS, event.hasPrevious());
		enableButton(BUTTON_NEXT, event.hasNext());
	}
	
}

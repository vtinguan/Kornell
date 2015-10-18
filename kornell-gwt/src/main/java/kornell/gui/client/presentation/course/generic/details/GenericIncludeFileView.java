package kornell.gui.client.presentation.course.generic.details;

import kornell.api.client.KornellClient;
import kornell.gui.client.KornellConstants;

import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;


public class GenericIncludeFileView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericIncludeFileView> {
	}

	private KornellConstants constants = GWT.create(KornellConstants.class);
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	private String IMAGES_PATH = "skins/first/icons/courseLibrary/";

	@UiField
	FileUpload fileUpload;
	@UiField
	Button btnPublish;

	@UiField
	Label includeFileFormInfoTitle;
	@UiField
	Label includeFileFormInfoText;
	@UiField
	Label starsLabel;

	@UiField
	TextArea fileDescription;

	@UiField
	Image star1;
	@UiField
	Image star2;
	@UiField
	Image star3;
	@UiField
	Image star4;
	@UiField
	Image star5;
	
	public GenericIncludeFileView(EventBus eventBus, KornellClient client, PlaceController placeCtrl) {
		initWidget(uiBinder.createAndBindUi(this));
		initData();		
	}
	
	private void initData() {
		/*client.getCourses(new Callback<CoursesTO>() {
			@Override
			protected void ok(CoursesTO to) {
			}
		});*/
		// TODO get info	
		display();
	}


	private void display() {
		includeFileFormInfoTitle.setText(constants.fileFormInfoTitle());
		includeFileFormInfoText.setText(constants.fileFormInfoText());
		
		fileDescription.setPlaceholder(constants.fileDescription());
		
		starsLabel.setText(constants.starsLabelText());
		star1.setUrl(IMAGES_PATH + "starOff.png");
		star2.setUrl(IMAGES_PATH + "starOff.png");
		star3.setUrl(IMAGES_PATH + "starOff.png");
		star4.setUrl(IMAGES_PATH + "starOff.png");
		star5.setUrl(IMAGES_PATH + "starOff.png");
		
		btnPublish.setText(constants.btnPublish());
		btnPublish.removeStyleName("btn");
	}
}

package kornell.gui.client.presentation.course.library.generic;

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

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	
	private KornellClient client;
	private PlaceController placeCtrl;
	private EventBus bus;
	private KornellConstants constants = GWT.create(KornellConstants.class); 
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
			}
		});*/
		// TODO get info	
		display();
	}


	private void display() {
		//TODO i18n
		includeFileFormInfoTitle.setText("Incluir arquivo");
		includeFileFormInfoText.setText("Ao incluir um arquivo dê a ele uma descrição para facilitar o entendimento do mesmo. Informe a sua relevância com o tema deste curso relacionando com as estrelas da seguinte forma: 1 estrela é de pouca relevância, 5 estrelas é altamente relevante, praticamente fundamental.");
		
		fileDescription.setPlaceholder("Como este arquivo pode auxiliar as pessoas? Para o que ele serve?");
		
		starsLabel.setText("Relevância:");
		star1.setUrl(IMAGES_PATH + "starOff.png");
		star2.setUrl(IMAGES_PATH + "starOff.png");
		star3.setUrl(IMAGES_PATH + "starOff.png");
		star4.setUrl(IMAGES_PATH + "starOff.png");
		star5.setUrl(IMAGES_PATH + "starOff.png");
		
		btnPublish.setText("Publicar");
		btnPublish.removeStyleName("btn");
	}
}
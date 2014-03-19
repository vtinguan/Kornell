package kornell.gui.client.presentation.course.generic.details;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.to.CourseClassTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.ProgressEvent;
import kornell.gui.client.event.ProgressEventHandler;
import kornell.gui.client.event.ShowDetailsEvent;
import kornell.gui.client.event.ShowDetailsEventHandler;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.util.KornellNotification;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericCertificationItemView extends Composite implements ProgressEventHandler, ShowDetailsEventHandler{
	interface MyUiBinder extends UiBinder<Widget, GenericCertificationItemView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private EventBus bus;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private String IMAGES_PATH = "skins/first/icons/courseDetails/";
	private KornellSession session;
	private CourseClassTO currentCourseClass;
	private String type;
	private String name;
	private String description;
	private String status;
	private String grade;
	
	private HandlerRegistration actionHandler;
	
	public static String TEST = "test";
	public static String CERTIFICATION = "certification";
	
	@UiField
	Image certificationIcon;
	@UiField
	Label lblName;
	@UiField
	Label lblDescription;
	@UiField
	Label lblStatus;
	@UiField
	Label lblGrade;
	@UiField
	Anchor lblActions;
	
	private boolean courseClassComplete, approvedOnTest;


	public GenericCertificationItemView(EventBus eventBus, KornellSession session, CourseClassTO currentCourseClass,
			String type) {
		this.bus = eventBus;
		this.session = session;
		this.currentCourseClass = currentCourseClass;
		this.type = type;
		bus.addHandler(ProgressEvent.TYPE,this);
		bus.addHandler(ShowDetailsEvent.TYPE,this);
		initWidget(uiBinder.createAndBindUi(this));
		initData();
		display();
	}

	private void initData() {
		if(TEST.equals(type)){
			this.name = "Avaliação";
			this.description = "Esta avaliação final tem a intenção de identificar o seu conhecimento após a conclusão do curso.";
			this.status = "A fazer";
			this.grade = "-";
		} else if(CERTIFICATION.equals(type)){
			this.name = "Certificado";
			this.description = "Impressão do certificado. Uma vez que o curso for terminado, você poderá gerar o certificado aqui.";
			this.grade = "-";
			updateCertificationLinkAndLabel();
		}
	}

	private void display() {
		certificationIcon.setUrl(IMAGES_PATH + type + ".png");
		lblName.setText(name);
		lblDescription.setText(description);
		lblStatus.setText(status);
		lblGrade.setText(grade);

		displayActionCell(false);
	}

	private void displayActionCell(boolean show) {
		if (show) {
			lblActions.setText("Gerar");
			lblActions.addStyleName("cursorPointer");
			actionHandler = lblActions.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					KornellNotification.show("Aguarde um instante...", AlertType.INFO, 2000);
					Window.Location.assign(session.getApiUrl() + "/report/certificate/"
							+ session.getCurrentUser().getPerson().getUUID() + "/"
							+ currentCourseClass.getCourseClass().getUUID());
				}
			});
		} else {
			lblActions.setText("-");
			lblActions.removeStyleName("cursorPointer");
			if(actionHandler != null){
				actionHandler.removeHandler();
			}
		}
	}

	@Override
	public void onProgress(ProgressEvent event) {
		// TODO Auto-generated method stub
		if(CERTIFICATION.equals(type)){
			if(event.getProgressPercent() >= 100){
				courseClassComplete = true;
			}
			updateCertificationLinkAndLabel();
		}
	}

	private void updateCertificationLinkAndLabel(){
		boolean allowCertificateGeneration = (courseClassComplete && approvedOnTest)  || session.isPlatformAdmin();
		status = allowCertificateGeneration ? "Disponível" : "Não disponível";
		lblStatus.setText(status);
		displayActionCell(allowCertificateGeneration);
	}
	
	@Override
	public void onShowDetails(ShowDetailsEvent event) {
		if(CERTIFICATION.equals(type) && event.isShowDetails())
			checkCertificateAvailability();
	}
	
	private void checkCertificateAvailability() {
		if(Dean.getInstance().getCourseClassTO() != null && Dean.getInstance().getCourseClassTO().getEnrollment() != null){
		    session.enrollment(Dean.getInstance().getCourseClassTO().getEnrollment().getUUID())
		    .isApproved(new Callback<Boolean>() {
		    	@Override
		    	public void ok(Boolean approved) {
		    		approvedOnTest = approved;
		    		updateCertificationLinkAndLabel();
		    	}
			});
		}
	}
}

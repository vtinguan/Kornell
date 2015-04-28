package kornell.gui.client.presentation.course.generic.details;

import static kornell.core.util.StringUtils.mkurl;
import static kornell.core.util.StringUtils.noneEmpty;

import java.math.BigDecimal;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Assessment;
import kornell.core.entity.CourseClass;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentCategory;
import kornell.core.entity.EnrollmentProgressDescription;
import kornell.core.entity.Person;
import kornell.core.to.CourseClassTO;
import kornell.core.to.UserInfoTO;
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
import com.google.gwt.user.client.Timer;
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
	private String IMAGES_PATH = "skins/first/icons/courseDetails/";
	private KornellSession session;
	private CourseClassTO currentCourseClass;
	private String type;
	private String name;
	private String description;
	private String status;
	private String grade;
	
	private HandlerRegistration actionHandler;
	
	public static final String TEST = "test";
	public static final String CERTIFICATION = "certification";
	
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
		displayGenericCertificationItemView();
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
			
			Enrollment enrollment = currentCourseClass != null? currentCourseClass.getEnrollment() : null;
			Integer progress = enrollment != null ? enrollment.getProgress() : -1;
			courseClassComplete = progress >= 100;
			Assessment assessment = enrollment != null ? enrollment.getAssessment() : null; 
			approvedOnTest = Assessment.PASSED.equals(assessment);
			updateCertificationLinkAndLabel();
		}
	}

	private void displayGenericCertificationItemView() {
		String url = mkurl(IMAGES_PATH, type + ".png");
		certificationIcon.setUrl(url);
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
					CourseClass courseClass = currentCourseClass != null ? currentCourseClass.getCourseClass() : null;					
					UserInfoTO currentUser = session.getCurrentUser();
					Person person = currentUser != null? currentUser.getPerson() : null;
					String personUUID = person != null ? person.getUUID() : null;
					String courseClassUUID = courseClass != null ? courseClass.getUUID() : null;
					String apiURL = session != null ? session.getApiUrl() : null;
					
					if (noneEmpty(apiURL,personUUID,courseClassUUID)){
						String url = mkurl(apiURL,
								"/report/certificate/",
								personUUID,
								courseClassUUID);					
						Window.Location.assign(url);
					}
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
		if(CERTIFICATION.equals(type)){
			if(event.getProgressPercent() >= 100){
				courseClassComplete = true;
			}
			updateCertificationLinkAndLabel();
		}
	}

	private void updateCertificationLinkAndLabel(){
		boolean allowCertificateGeneration = (courseClassComplete && approvedOnTest);
		status = allowCertificateGeneration ? "Disponível" : "Não disponível";
		lblStatus.setText(status);

		if(currentCourseClass != null){
			Enrollment currEnrollment = currentCourseClass.getEnrollment();
			CourseClass courseClass = currentCourseClass.getCourseClass();
			BigDecimal requiredScore = courseClass != null ? courseClass.getRequiredScore() : null;
			if(currEnrollment != null &&
					EnrollmentProgressDescription.completed.equals(EnrollmentCategory.getEnrollmentProgressDescription(currEnrollment)) &&
					requiredScore != null && 
					requiredScore.intValue() != 0 &&
					currEnrollment.getAssessmentScore() != null){
				this.grade = ""+currEnrollment.getAssessmentScore().intValue();
			} else {
				this.grade = "-";
			}
			lblGrade.setText(grade);
		}
		
		displayActionCell(allowCertificateGeneration);
	}
	
	@Override
	public void onShowDetails(ShowDetailsEvent event) {
		if(CERTIFICATION.equals(type) && event.isShowDetails())
			checkCertificateAvailability();
	}
	
	private void checkCertificateAvailability() {
		if(!approvedOnTest && Dean.getInstance().getCourseClassTO() != null && Dean.getInstance().getCourseClassTO().getEnrollment() != null){
			Timer checkTimer = new Timer() {
				@Override
				public void run() {
			    session.enrollment(Dean.getInstance().getCourseClassTO().getEnrollment().getUUID())
			    .isApproved(new Callback<Boolean>() {
			    	@Override
			    	public void ok(Boolean approved) {
			    		approvedOnTest = approved;
			    		updateCertificationLinkAndLabel();
			    	}
				});
				}
			};
			checkTimer.schedule(3000);
			//TODO: MDA (to be resolved on the refactoring of the details)
		}
	}
}

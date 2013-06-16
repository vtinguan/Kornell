package kornell.gui.client.presentation.welcome.generic;


import java.math.BigDecimal;

import kornell.core.shared.data.Course;
import kornell.core.shared.data.CourseTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.atividade.AtividadePlace;

import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Image;

public class GenericCourseSummaryView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseSummaryView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	private KornellConstants constants = GWT.create(KornellConstants.class);

	@UiField
	Heading hTitle;
	
	@UiField
	Paragraph pDescription;
	
	@UiField
	Paragraph pProgress;
	
	@UiField
	Image imgThumb;
	
	@UiField
	Image imgIconCourse;

	@UiField
	HorizontalPanel pnlCourseSummaryBar;
	
	String iconCourseURL = "skins/first/icons/";
	
	public GenericCourseSummaryView(final PlaceController placeCtrl, final CourseTO courseTO) {
		initWidget(uiBinder.createAndBindUi(this));
		
		final Course course = courseTO.getCourse();
		hTitle.setText(course.getTitle());
		pDescription.setText(course.getDescription());
		
		BigDecimal progress = courseTO.getEnrollment().getProgress();
		if(progress != null){
			if(progress.compareTo(BigDecimal.ONE) == 0){
				Image iconCertificate = new Image();
				iconCertificate.setUrl(iconCourseURL+"iconPDF.png");
				iconCertificate.addStyleName("iconCertificate");
				pnlCourseSummaryBar.add(iconCertificate);
				
				Label certificate = new Label(constants.certificate());
				certificate.addStyleName("courseProgress");
				certificate.addStyleName("courseProgressCertificate");
				pnlCourseSummaryBar.add(certificate);
				
				pProgress.setText(constants.courseFinished());
				iconCourseURL+="iconFinished.png"; 
			}else if(progress.compareTo(BigDecimal.ZERO) == 0){
				pProgress.setText(constants.toStart());
				iconCourseURL+="iconToStart.png"; 
			}else{
				pProgress.setText(toPercentString(progress) + " " + constants.complete().toLowerCase());
				iconCourseURL+="iconCurrent.png"; 
			}
		}else{
			pProgress.setText(constants.toAcquire());
			iconCourseURL+="iconToAcquire.png"; 
		}
		
		String assetsURL = course.getAssetsURL();
		//TODO course.getImage()
		imgThumb.setUrl(assetsURL + "thumb.jpg");
		imgIconCourse.setUrl(iconCourseURL);
		
		sinkEvents(Event.ONCLICK);
		addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				placeCtrl.goTo(new AtividadePlace(course.getUUID(), 0));
			}
		}, ClickEvent.getType());
		
	}

	private String toPercentString(BigDecimal progress) {
		return progress.multiply(new BigDecimal("100")).setScale(0).toPlainString() + "%";		
	}



}

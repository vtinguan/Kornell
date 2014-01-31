package kornell.gui.client.presentation.welcome.generic;


import java.math.BigDecimal;

import kornell.core.entity.Course;
import kornell.core.to.CourseClassTO;
import kornell.gui.client.KornellConstants;

import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

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
	FlowPanel pnlCourseSummaryBar;
	
	String iconCourseURL = "skins/first/icons/";
	
	public GenericCourseSummaryView(final PlaceController placeCtrl, final CourseClassTO courseClassTO) {
		initWidget(uiBinder.createAndBindUi(this));
		
		final Course course = courseClassTO.getCourseVersionTO().getCourse();
		hTitle.setText(course.getTitle());
		pDescription.setText(course.getDescription());
		
		Integer progress = courseClassTO.getEnrollment().getProgress();
		if(progress != null){
			if(progress == 100){
				Label certificate = new Label(constants.certificate());
				certificate.addStyleName("courseProgress");
				certificate.addStyleName("courseProgressCertificate");
				pnlCourseSummaryBar.add(certificate);
				
				Image iconCertificate = new Image();
				iconCertificate.setUrl(iconCourseURL+"iconPDF.png");
				iconCertificate.addStyleName("iconCertificate");
				pnlCourseSummaryBar.add(iconCertificate);
				
				pProgress.setText(constants.courseFinished());
				iconCourseURL+="iconFinished.png"; 
			}else if(progress == 0){
				pProgress.setText(constants.toStart());
				iconCourseURL+="iconToStart.png"; 
			}else{
				pProgress.setText(progress + " " + constants.complete().toLowerCase());
				iconCourseURL+="iconCurrent.png"; 
			}
		}else{
			pProgress.setText(constants.toAcquire());
			iconCourseURL+="iconToAcquire.png"; 
		}
		
		String assetsURL = "";
		//TODO course.getImage()
		imgThumb.setUrl(assetsURL + "thumb.jpg");
		imgIconCourse.setUrl(iconCourseURL);
		
		sinkEvents(Event.ONCLICK);
		addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//placeCtrl.goTo(new CourseHomePlace(course.getUUID()));
			}
		}, ClickEvent.getType());
		
	}

	private String toPercentString(BigDecimal progress) {
		return progress.multiply(new BigDecimal("100")).setScale(0).toPlainString() + "%";		
	}



}

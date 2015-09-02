package kornell.gui.client.presentation.admin.course.course.generic;

import kornell.api.client.KornellSession;
import kornell.core.entity.Course;
import kornell.core.util.StringUtils;
import kornell.gui.client.presentation.util.KornellNotification;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericCourseReportItemView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseReportItemView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private String BASE_IMAGES_PATH = "skins/first/icons/";
	private String ADMIN_IMAGES_PATH = BASE_IMAGES_PATH + "admin/";
	private String LIBRARY_IMAGES_PATH = BASE_IMAGES_PATH + "courseLibrary/";
	private KornellSession session;
	private Course course;
	private String type;
	private String name;
	private String description;
	
	private CheckBox xlsCheckBox;
	
	public static final String COURSE_INFO = "courseClassInfo";
	
	@UiField
	Image certificationIcon;
	@UiField
	Label lblName;
	@UiField
	Label lblDescription;
	@UiField
	FlowPanel optionPanel;
	@UiField
	Anchor lblGenerate;
	@UiField
	Anchor lblDownload;


	public GenericCourseReportItemView(EventBus eventBus, KornellSession session, Course course,
			String type) {
		this.session = session;
		this.course = course;
		this.type = type;
		initWidget(uiBinder.createAndBindUi(this));
		display();
	}
	
	private void display() {
		if(COURSE_INFO.equals(this.type))
			displayCourseClassInfo();
	}

	private void displayCourseClassInfo() {
	  this.name = "Relatório de detalhes do curso";
		this.description = "Geração do relatório de detalhes do curso, contendo os dados das matrículas nas turmas.";
		
		certificationIcon.setUrl(ADMIN_IMAGES_PATH + type + ".png");
		lblName.setText(name);
		lblDescription.setText(description);
		lblGenerate.setText("Gerar");
		lblGenerate.addStyleName("cursorPointer");

		lblDownload.setText("-");
		lblDownload.removeStyleName("cursorPointer");
		lblDownload.addStyleName("anchorToLabel");
		lblDownload.setEnabled(false);
		
		Image img = new Image(LIBRARY_IMAGES_PATH + "xls.png");
		xlsCheckBox = new CheckBox("Gerar em formato Excel");
		
		optionPanel.add(img);
		optionPanel.add(xlsCheckBox);
		
		img.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				xlsCheckBox.setValue(!xlsCheckBox.getValue());				
			}
		});
		
		lblGenerate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				KornellNotification.show("Aguarde um instante...", AlertType.INFO, 2000);
				String url = session.getApiUrl() + "/report/courseClassInfo/?courseUUID="
						+ course.getUUID() + "&fileType=" + (xlsCheckBox.getValue() ? "xls" : "pdf");
				Window.Location.assign(url);
			}
		});
  }

}

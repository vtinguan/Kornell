package kornell.gui.client.presentation.admin.home.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClass;
import kornell.core.entity.EntityFactory;
import kornell.core.to.CourseClassTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.home.AdminHomeView.Presenter;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.uidget.formfield.KornellFormFieldWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericCourseClassReportsView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseClassReportsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	public static final EntityFactory entityFactory = GWT.create(EntityFactory.class);

	private EventBus bus;
	private KornellSession session;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private FormHelper formHelper;
	private boolean isInstitutionAdmin;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;

	private Presenter presenter;

	@UiField
	FlowPanel reportsPanel;

	private UserInfoTO user;
	private CourseClassTO courseClassTO;
	private CourseClass courseClass;
	private FileUpload fileUpload;
	private List<KornellFormFieldWrapper> fields;
	
	public GenericCourseClassReportsView(final KornellSession session, EventBus bus,
			Presenter presenter, CourseClassTO courseClassTO) {
		this.session = session;
		this.bus = bus;
		this.presenter = presenter;
		this.user = session.getCurrentUser();
		formHelper = new FormHelper();
		initWidget(uiBinder.createAndBindUi(this));		
		this.courseClassTO = courseClassTO;		
		initData();
	}

	public void initData() {
		reportsPanel.setVisible(false);
		this.fields = new ArrayList<KornellFormFieldWrapper>();
		courseClass =  courseClassTO.getCourseClass();
		isInstitutionAdmin = session.isInstitutionAdmin();


		reportsPanel.add(getReportPanel());
		reportsPanel.setVisible(true);

	}

	private FlowPanel getReportPanel() {
		FlowPanel reportPanel = new FlowPanel();
		reportPanel.addStyleName("reportPanel");
		// TODO: i18n
		reportPanel.add(getReportInfo());
		reportPanel.add(getReportTableHeader());
		reportPanel.add(getReportTableContent());

		return reportPanel;
	}

	private FlowPanel getReportInfo() {
		FlowPanel reportInfo = new FlowPanel();
		reportInfo.addStyleName("reportInfo");

		Label infoTitle = new Label("Relatórios");
		infoTitle.addStyleName("reportInfoTitle");
		reportInfo.add(infoTitle);

		Label infoText = new Label("Geração dos relatórios administrativos.");
		infoText.addStyleName("reportInfoText");
		reportInfo.add(infoText);

		return reportInfo;
	}

	private FlowPanel getReportTableContent() {
		FlowPanel reportContentPanel = new FlowPanel();
		reportContentPanel.addStyleName("reportContentPanel");
		reportContentPanel.add(new GenericReportItemView(bus, session, Dean.getInstance().getCourseClassTO(), GenericReportItemView.CERTIFICATION)); 

		return reportContentPanel;
	}

	private FlowPanel getReportTableHeader() {
		FlowPanel reportHeaderPanel = new FlowPanel();
		reportHeaderPanel.addStyleName("reportHeaderPanel");

		reportHeaderPanel.add(getHeaderButton("Relatório", "btnReport", "btnReportHeader"));
		reportHeaderPanel.add(getHeaderButton("Geração", "btnGenerate", "btnReportHeader"));
		reportHeaderPanel.add(getHeaderButton("Download", "btnDownload", "btnReportHeader"));
		
		return reportHeaderPanel;
	}

	private Button getHeaderButton(String label, String styleName,
			String styleNameGlobal) {
		Button btn = new Button(label);
		btn.removeStyleName("btn");
		btn.addStyleName(styleNameGlobal);
		btn.addStyleName(styleName);
		btn.addStyleName("btnNotSelected");
		return btn;
	}

}
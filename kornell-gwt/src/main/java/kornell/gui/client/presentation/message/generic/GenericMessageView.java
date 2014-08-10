package kornell.gui.client.presentation.message.generic;

import java.util.List;

import kornell.api.client.KornellSession;
import kornell.core.to.CourseClassTO;
import kornell.core.to.EnrollmentTO;
import kornell.gui.client.presentation.admin.home.generic.GenericCourseClassReportsView;
import kornell.gui.client.presentation.message.MessageView;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.uidget.KornellPagination;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Collapse;
import com.github.gwtbootstrap.client.ui.CollapseTrigger;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericMessageView extends Composite implements MessageView {

	interface MyUiBinder extends UiBinder<Widget, GenericMessageView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private KornellSession session;
	private EventBus bus;
	private MessageView.Presenter presenter;
	final CellTable<EnrollmentTO> table;
	private List<EnrollmentTO> enrollmentsCurrent;
	private List<EnrollmentTO> enrollments;
	private KornellPagination pagination;
	private TextBox txtSearch;
	private Button btnSearch;
	private List<CourseClassTO> courseClasses;
	private Boolean enrollWithCPF = false;
	private boolean isEnabled;
	private Integer maxEnrollments = 0;
	private Integer numEnrollments = 0;
	private GenericCourseClassReportsView reportsView;
	private FormHelper formHelper;

	@UiField
	FlowPanel adminHomePanel;
	@UiField
	FlowPanel courseClassesPanel;
	@UiField
	Button btnAddCourseClass;
	@UiField
	FlowPanel enrollmentsPanel;
	@UiField
	FlowPanel addEnrollmentsPanel;
	@UiField
	ListBox listBoxCourseClasses;
	@UiField
	Tab enrollmentsTab;
	@UiField
	Tab configTab;
	@UiField
	FlowPanel configPanel;
	@UiField
	Tab reportsTab;
	@UiField
	FlowPanel reportsPanel;

	@UiField
	Button btnAddEnrollment;
	@UiField
	Button btnAddEnrollmentBatch;
	@UiField
	Button btnAddEnrollmentBatchEnable;
	@UiField
	TextBox txtFullName;
	@UiField
	TextBox txtEmail;
	@UiField
	TextArea txtAddEnrollmentBatch;
	@UiField
	CollapseTrigger trigger;
	@UiField
	Collapse collapse;
	@UiField
	Label identifierLabel;
	@UiField
	FlowPanel infoPanelEmail;
	@UiField
	FlowPanel infoPanelCPF;

	@UiField
	Modal errorModal;
	@UiField
	Label txtModal1;
	@UiField
	Label txtModal2;
	@UiField
	TextArea txtModalError;
	@UiField
	Button btnModalOK;
	@UiField
	Button btnModalCancel;

	@UiField
	Label lblCourseClassName;
	@UiField
	Label lblCourseName;
	@UiField
	Label lblEnrollmentsCount;

	@UiField
	FlowPanel enrollmentsWrapper;

	@UiField
	TabPanel tabsPanel;

	Tab adminsTab;
	FlowPanel adminsPanel;
	private String viewType;

	// TODO i18n xml
	public GenericMessageView(final KornellSession session, EventBus bus) {
		this.session = session;
		this.bus = bus;
		initWidget(uiBinder.createAndBindUi(this));
		tabsPanel.setVisible(false);
		table = new CellTable<EnrollmentTO>();
		pagination = new KornellPagination(table, enrollmentsCurrent);
		formHelper = new FormHelper();

		trigger.setTarget("#toggle");
		collapse.setId("toggle");

		txtModalError.setReadOnly(true);

		btnModalOK.setText("OK".toUpperCase());
		btnModalCancel.setText("Cancelar".toUpperCase());
		btnAddCourseClass.setText("Criar Nova Turma");

		//btnAddEnrollmentBatchEnable.setHTML(btnAddEnrollmentBatchEnable.getText() + "&nbsp;&nbsp;&#x25BC;");


		enrollmentsTab.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//presenter.updateCourseClass(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID());
			}
		});

		configTab.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				buildConfigView(false);
			}
		});

		// new ExceptionalRequestBuilder(RequestBuilder.PUT, url)
	}
	
	@Override
	public String getViewType() {
		return viewType;
	}

	//
	public void prepareAddNewCourseClass(boolean addingNewCourseClass) {
		adminHomePanel.clear();
		if (!addingNewCourseClass) {
			adminHomePanel.add(courseClassesPanel);
			adminHomePanel.add(tabsPanel);
			configPanel.clear();
			configTab.setActive(false);
			reportsPanel.clear();
			reportsTab.setActive(false);
			reportsView = null;
			if (adminsTab != null)
				adminsTab.setActive(false);
			enrollmentsTab.setActive(true);
		}
	}

	//@Override
	public void buildConfigView(boolean isCreationMode) {
		prepareAddNewCourseClass(isCreationMode);
		if (isCreationMode) {
			//adminHomePanel.add(new GenericCourseClassConfigView(session, presenter, null));
		} else {
			//configPanel.add(new GenericCourseClassConfigView(session, presenter, Dean.getInstance().getCourseClassTO()));
		}
	}
	
	//@Override
	public void showTabsPanel(boolean visible) {
		tabsPanel.setVisible(visible);
	}

	@Override
	public void setPresenter(Presenter p) {
		presenter = p;
		// TODO Auto-generated method stub
	}
}
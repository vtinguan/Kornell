package kornell.gui.client.presentation.admin.home.generic;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kornell.api.client.KornellSession;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentCategory;
import kornell.core.entity.EnrollmentProgressState;
import kornell.core.entity.EnrollmentState;
import kornell.core.to.CourseClassTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.home.AdminHomeView;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.uidget.KornellPagination;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Collapse;
import com.github.gwtbootstrap.client.ui.CollapseTrigger;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.web.bindery.event.shared.EventBus;

public class GenericAdminHomeView extends Composite implements AdminHomeView {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminHomeView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private KornellSession session;
	private EventBus bus;
	private AdminHomeView.Presenter presenter;
	final CellTable<Enrollment> table;
	private List<Enrollment> enrollmentsCurrent;
	private List<Enrollment> enrollments;
	private KornellPagination pagination;
	private TextBox txtSearch;
	private Button btnSearch;
	private List<CourseClassTO> courseClasses;
	private Boolean enrollWithCPF = false;
	private Integer maxEnrollments = 0;
	private Integer numEnrollments = 0;
	private GenericCourseClassReportsView reportsView;
	private FormHelper formHelper;
		
	private boolean forbidProfileView;
	
	@UiField
	FlowPanel adminHomePanel;
	@UiField
	FlowPanel courseClassesPanel;
	@UiField
	Button btnAddCourseClass;
	@UiField
	FlowPanel enrollmentsPanel;
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
	Button btnGoToCourse;
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

	// TODO i18n xml
	public GenericAdminHomeView(final KornellSession session, EventBus bus) {
		this.session = session;
		this.bus = bus;
		initWidget(uiBinder.createAndBindUi(this));
		table = new CellTable<Enrollment>();
		pagination = new KornellPagination(table, enrollmentsCurrent);
		formHelper = new FormHelper();
		
		trigger.setTarget("#toggle");
		collapse.setId("toggle");
		
		txtModalError.setReadOnly(true);

		btnModalOK.setText("OK".toUpperCase());
		btnModalCancel.setText("Cancelar".toUpperCase());
		btnAddCourseClass.setText("Criar Nova Turma");
				
		btnAddEnrollmentBatchEnable.setHTML(btnAddEnrollmentBatchEnable.getText()+"&nbsp;&nbsp;&#x25BC;");
		
		listBoxCourseClasses.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String newCourseClassUUID = ((ListBox) event.getSource()).getValue();
				if(!newCourseClassUUID.equals(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID())){
					presenter.updateCourseClass(newCourseClassUUID);
				}
			}
		});
		btnAddCourseClass.setVisible(session.isInstitutionAdmin());
		btnAddCourseClass.addClickHandler(new ClickHandler() {	
			@Override
			public void onClick(ClickEvent event) {
				if(session.isInstitutionAdmin()){
					buildConfigView(true);
				}
			}
		});
		
		enrollmentsTab.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showEnrollmentsPanel(false);
				presenter.updateCourseClass(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID());
			}
		});
		
		configTab.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				buildConfigView(false);
			}
		});
		
		reportsTab.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				buildReportsView();
			}
		});
		
		if(session.isInstitutionAdmin()){
			adminsTab = new Tab();
			adminsTab.setIcon(IconType.GROUP);
			adminsTab.setHeading("Administradores");
			adminsTab.setActive(false);
			adminsPanel = new FlowPanel();
			adminsTab.add(adminsPanel);
			adminsTab.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					buildAdminsView();
				}
			});
			
			tabsPanel.add(adminsTab);
		}
		
		//new ExceptionalRequestBuilder(RequestBuilder.PUT, url)
	}
	
	@Override
	public void prepareAddNewCourseClass(boolean addingNewCourseClass){
		adminHomePanel.clear();
		if(!addingNewCourseClass){
			adminHomePanel.add(courseClassesPanel);
			adminHomePanel.add(tabsPanel);
			configPanel.clear();
			configTab.setActive(false);
			reportsPanel.clear();
			reportsTab.setActive(false);
			reportsView = null;
			if(adminsTab != null)
				adminsTab.setActive(false);
			enrollmentsTab.setActive(true);
		}
	}

	@Override
	public void buildConfigView(boolean isCreationMode) {
		prepareAddNewCourseClass(isCreationMode);
		if(isCreationMode){
			adminHomePanel.add(new GenericCourseClassConfigView(session, presenter, null));
		} else {
			configPanel.add(new GenericCourseClassConfigView(session, presenter, Dean.getInstance().getCourseClassTO()));
		}
	}

	@Override
	public void buildReportsView() {
		if(reportsView == null){
			reportsView = new GenericCourseClassReportsView(session, bus, presenter, Dean.getInstance().getCourseClassTO());
		}
		reportsPanel.clear();
		reportsPanel.add(reportsView);
	}
 
	@Override
	public void buildAdminsView() {
		adminsPanel.clear();
		if(!session.isInstitutionAdmin()) return;
		adminsPanel.add(new GenericCourseClassAdminsView(session, presenter, Dean.getInstance().getCourseClassTO()));
	}

	private void initSearch() {
		txtSearch = new TextBox();
		txtSearch.addStyleName("txtSearch");
		txtSearch.setTitle("nome, "+ (enrollWithCPF?"CPF":"email") +", matrícula ou progresso");
		txtSearch.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				filterEnrollments();
			}
		});
		txtSearch.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				filterEnrollments();
			}
		});
		txtSearch.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				filterEnrollments();
				
			}
		});
		btnSearch = new Button("Pesquisar");
		btnSearch.addStyleName("btnNotSelected btnSearch");
		btnSearch.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				filterEnrollments();
			}
		});
	}

	private void initTable() {
	    
		table.addStyleName("enrollmentsCellTable");
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		for (int i = 0; table.getColumnCount() > 0;) {
			table.removeColumn(i);
		}
				
		table.addColumn(new TextColumn<Enrollment>() {
			@Override
			public String getValue(Enrollment enrollment) {
				return enrollment.getPerson().getFullName();
			}
		}, "Nome");
		
		table.addColumn(new TextColumn<Enrollment>() {
			@Override
			public String getValue(Enrollment enrollment) {
				if(enrollment.getPerson().getEmail() != null && !"".equals(enrollment.getPerson().getEmail()))
					return enrollment.getPerson().getEmail();
				else
					return enrollment.getPerson().getCPF();
			}
		}, "Email/CPF");

		table.addColumn(new TextColumn<Enrollment>() {
			@Override	
			public String getValue(Enrollment enrollment) {
				return formHelper.getEnrollmentStateAsText(enrollment.getState());
			}
		}, "Matrícula");

		table.addColumn(new TextColumn<Enrollment>() {
			@Override	
			public String getValue(Enrollment enrollment) {
				return getEnrollmentProgressAsText(EnrollmentCategory.getEnrollmentProgressState(enrollment));
			}
		}, "Progresso");
	
	    List<HasCell<Enrollment, ?>> cells = new LinkedList<HasCell<Enrollment, ?>>();
	    cells.add(new EnrollmentActionsHasCell("Aceitar", getStateChangeDelegate(EnrollmentState.enrolled)));
	    cells.add(new EnrollmentActionsHasCell("Negar", getStateChangeDelegate(EnrollmentState.denied)));
	    cells.add(new EnrollmentActionsHasCell("Cancelar", getStateChangeDelegate(EnrollmentState.cancelled)));
	    cells.add(new EnrollmentActionsHasCell("Matricular", getStateChangeDelegate(EnrollmentState.enrolled)));
	    cells.add(new EnrollmentActionsHasCell("Excluir", getDeleteEnrollmentDelegate()));
	    
	    CompositeCell<Enrollment> cell = new CompositeCell<Enrollment>(cells);
	    table.addColumn(new Column<Enrollment, Enrollment>(cell) {
	        @Override
	        public Enrollment getValue(Enrollment enrollment) {
	            return enrollment;
	        }
	    }, "Ações");

		// Add a selection model to handle user selection.
		final SingleSelectionModel<Enrollment> selectionModel = new SingleSelectionModel<Enrollment>();
		table.setSelectionModel(selectionModel);
		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
			        	if(forbidProfileView){
				        	forbidProfileView = false;
			        	} else {
							Enrollment selected = selectionModel.getSelectedObject();
							if (selected != null) {
								presenter.onUserClicked(selected.getPerson().getUUID());
							}
			        	}
					}
				});
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}


	@UiHandler("btnModalOK")
	void onModalOkButtonClicked(ClickEvent e) {
		presenter.onModalOkButtonClicked();
    	errorModal.hide();
	}


	@UiHandler("btnModalCancel")
	void onModalCancelButtonClicked(ClickEvent e) {
    	errorModal.hide();
	}


	@UiHandler("btnGoToCourse")
	void onGoToCourseButtonClicked(ClickEvent e) {
		presenter.onGoToCourseButtonClicked();
	}


	@UiHandler("btnAddEnrollment")
	void onAddEnrollmentButtonClicked(ClickEvent e) {
		presenter.onAddEnrollmentButtonClicked(txtFullName.getText(), txtEmail.getText());
	}


	@UiHandler("btnAddEnrollmentBatch")
	void doLogin(ClickEvent e) {
		collapse.hide();
		presenter.onAddEnrollmentBatchButtonClicked(txtAddEnrollmentBatch.getText());
	}

	@Override
	public void setModalErrors(String errors) {
		txtModalError.setText(errors);
	}

	@Override
	public void setEnrollmentList(List<Enrollment> enrollmentsIn) {
		numEnrollments = enrollmentsIn.size();
		maxEnrollments = Dean.getInstance().getCourseClassTO().getCourseClass().getMaxEnrollments();
		lblEnrollmentsCount.setText(numEnrollments + " / " + maxEnrollments);
				
		enrollmentsCurrent = new ArrayList<Enrollment>(enrollmentsIn);
		enrollments = new ArrayList<Enrollment>(enrollmentsIn);
		enrollmentsWrapper.clear();
		
		VerticalPanel panel = new VerticalPanel();
		panel.setWidth("400");
		panel.add(table);
		
		Image separatorBar = new Image("skins/first/icons/profile/separatorBar.png");
		separatorBar.addStyleName("fillWidth");
		
		final ListBox pageSizeListBox = new ListBox();
		//pageSizeListBox.addItem("1");
		//pageSizeListBox.addItem("10");
		pageSizeListBox.addItem("20");
		pageSizeListBox.addItem("50");
		pageSizeListBox.addItem("100");
		pageSizeListBox.setSelectedValue(""+pagination.getPageSize());
		pageSizeListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if(pageSizeListBox.getValue().matches("[0-9]*"))
					pagination.setPageSize(Integer.parseInt(pageSizeListBox.getValue()));
			}
		});		
		pageSizeListBox.addStyleName("pageSizeListBox");
		
		
		enrollmentsWrapper.add(separatorBar);
		enrollmentsWrapper.add(txtSearch);
		enrollmentsWrapper.add(btnSearch);
		enrollmentsWrapper.add(pageSizeListBox);
		enrollmentsWrapper.add(panel);
		enrollmentsWrapper.add(pagination);
		
		pagination.setRowData(enrollmentsCurrent);
		pagination.displayTableData(1);
		
		filterEnrollments();
	}

	private void filterEnrollments() {
		enrollmentsCurrent = new ArrayList<Enrollment>(enrollments);
		Enrollment enrollment;
		GWT.log("size: "+enrollmentsCurrent.size());
		for (int i = 0; i < enrollmentsCurrent.size(); i++) {
	    	enrollment = enrollmentsCurrent.get(i);
			boolean fullNameMatch = enrollment.getPerson() != null && enrollment.getPerson().getFullName() != null &&
					enrollment.getPerson().getFullName().toLowerCase().indexOf(txtSearch.getText().toLowerCase()) >= 0;
			boolean emailMatch = false;
			if(enrollWithCPF){
				emailMatch = enrollment.getPerson() != null && enrollment.getPerson().getCPF() != null &&
						enrollment.getPerson().getCPF().toLowerCase().indexOf(txtSearch.getText().toLowerCase()) >= 0;
			} else {
				emailMatch = enrollment.getPerson() != null && enrollment.getPerson().getEmail() != null &&
					enrollment.getPerson().getEmail().toLowerCase().indexOf(txtSearch.getText().toLowerCase()) >= 0;
			}
			boolean enrollmentStateMatch = enrollment.getPerson() != null && enrollment.getState() != null &&
					formHelper.getEnrollmentStateAsText(enrollment.getState()).toLowerCase().indexOf(txtSearch.getText().toLowerCase()) >= 0;
			boolean enrollmentProgressMatch = enrollment.getPerson() != null && enrollment.getProgress() != null &&
					getEnrollmentProgressAsText(EnrollmentCategory.getEnrollmentProgressState(enrollment)).toLowerCase().indexOf(txtSearch.getText().toLowerCase()) >= 0;
			if(!fullNameMatch && !emailMatch && !enrollmentStateMatch && !enrollmentProgressMatch){
				enrollmentsCurrent.remove(i);
				i--;
			}
		}
		pagination.setRowData(enrollmentsCurrent);
		pagination.displayTableData(1);
	}

	private String getEnrollmentProgressAsText(EnrollmentProgressState progressState) {
		switch (progressState) {
		case toStart:
			return "A iniciar";
		case inProgress:
			return "Em andamento";
		case finished:
			return "Concluído";
		default:
			return "???";
		}
	}

	@Override
	public void showModal(){
    	errorModal.show();
	}

	private Delegate<Enrollment> getStateChangeDelegate(final EnrollmentState state) {
		return new Delegate<Enrollment>() {
	        @Override
	        public void execute(Enrollment object) {
	        	if(forbidProfileView){
		        	presenter.changeEnrollmentState(object, state);
	        	}
	        	forbidProfileView = true;
	        }
	    };
	}

	private Delegate<Enrollment> getDeleteEnrollmentDelegate() {
		return new Delegate<Enrollment>() {
	        @Override
	        public void execute(Enrollment object) {
	        	if(forbidProfileView){
	        		presenter.deleteEnrollment(object);
	        	}
	        	forbidProfileView = true;
	        }
	    };
	}
	
	private class EnrollmentActionsActionCell<Enrollment> extends ActionCell<Enrollment> {
		Delegate<Enrollment> delegate;
		public EnrollmentActionsActionCell(String message, Delegate<Enrollment> delegate) {
			super(message, delegate);
			this.delegate = delegate;
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, Enrollment value,
				NativeEvent event, ValueUpdater<Enrollment> valueUpdater) {
			event.stopPropagation();
			event.preventDefault();
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
			if (CLICK.equals(event.getType())) {
				EventTarget eventTarget = event.getEventTarget();
				if (!Element.is(eventTarget)) {
					return;
				}
				if (parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget))) {
					// Ignore clicks that occur outside of the main element.
					onEnterKeyDown(context, parent, value, event, valueUpdater);
				}
			}
		}
	}
	
	private class EnrollmentActionsHasCell implements HasCell<Enrollment, Enrollment> {
		private EnrollmentActionsActionCell<Enrollment> cell;

		public EnrollmentActionsHasCell(String text, Delegate<Enrollment> delegate) {
			final String actionName = text;
			cell = new EnrollmentActionsActionCell<Enrollment>(text, delegate){
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context, Enrollment object, SafeHtmlBuilder sb) {
			        if(presenter.showActionButton(actionName, object)){
			        	String buttonClass = "Excluir".equals(actionName) ? "btnNotSelected" : (("Cancelar".equals(actionName) || "Negar".equals(actionName)) ? "btnSelected" : "btnAction");
			        	//super.render(context, object, sb);
			        	SafeHtml html = SafeHtmlUtils.fromTrustedString("<button type=\"button\" class=\"gwt-Button btnEnrollmentsCellTable "+
			        			buttonClass + "\">" + actionName.toUpperCase() + "</button>");
			            sb.append(html);
			        }
			        else
			             sb.appendEscaped("");
				}
				
			};
		}

		@Override
		public Cell<Enrollment> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<Enrollment, Enrollment> getFieldUpdater() {
			return null;
		}

		@Override
		public Enrollment getValue(Enrollment object) {
			return object;
		}
	}

	@Override
	public void setCourseClassName(String courseClassName) {
		this.lblCourseClassName.setText(courseClassName);
	}

	@Override
	public void setCourseName(String courseName) {
		this.lblCourseName.setText(courseName);
	}

	@Override
	public void setCourseClasses(List<CourseClassTO> courseClasses) {
		this.courseClasses = courseClasses;
		listBoxCourseClasses.clear();
		String name, value;
		for (CourseClassTO courseClassTO : courseClasses) {
			name = courseClassTO.getCourseVersionTO().getCourse().getTitle() + " - " + courseClassTO.getCourseClass().getName();
			value = courseClassTO.getCourseClass().getUUID();
			listBoxCourseClasses.addItem(name, value);
		}		
	}

	@Override
	public void setUserEnrollmentIdentificationType(Boolean enrollWithCPF) {
		this.enrollWithCPF = enrollWithCPF;
		if(enrollWithCPF){
			identifierLabel.setText("CPF");
			infoPanelEmail.addStyleName("shy");
			infoPanelCPF.removeStyleName("shy");
			infoPanelEmail.removeStyleName("shy");
		} else {
			identifierLabel.setText("Email");
			infoPanelEmail.removeStyleName("shy");
			infoPanelCPF.addStyleName("shy");
		}
		initTable();
		initSearch();
	}

	@Override
	public void setSelectedCourseClass(String uuid) {
		listBoxCourseClasses.setSelectedValue(uuid);
	}
	
	@Override
	public void setHomeTabActive(){
		enrollmentsTab.setActive(true);
		configTab.setActive(false);
	}

	@Override
	public void showEnrollmentsPanel(boolean visible) {
		enrollmentsPanel.setVisible(visible);
	}
}
package kornell.gui.client.presentation.admin.home.generic;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentCategory;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.Person;
import kornell.core.to.CourseClassTO;
import kornell.core.to.EnrollmentTO;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.home.AdminHomeView;
import kornell.gui.client.presentation.util.AsciiUtils;
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
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
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
	private KornellSession session;
	private EventBus bus;
	private AdminHomeView.Presenter presenter;
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

		listBoxCourseClasses.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String newCourseClassUUID = ((ListBox) event.getSource()).getValue();
				if (!newCourseClassUUID.equals(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID())) {
					presenter.updateCourseClass(newCourseClassUUID);
				}
			}
		});
		btnAddCourseClass.setVisible(session.isInstitutionAdmin());
		btnAddCourseClass.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (session.isInstitutionAdmin()) {
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

		if (session.isInstitutionAdmin()) {
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

		// new ExceptionalRequestBuilder(RequestBuilder.PUT, url)
	}

	@Override
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

	@Override
	public void buildConfigView(boolean isCreationMode) {
		prepareAddNewCourseClass(isCreationMode);
		if (isCreationMode) {
			adminHomePanel.add(new GenericCourseClassConfigView(session, presenter, null));
		} else {
			configPanel.add(new GenericCourseClassConfigView(session, presenter, Dean.getInstance().getCourseClassTO()));
		}
	}

	@Override
	public void buildReportsView() {
		if (reportsView == null) {
			reportsView = new GenericCourseClassReportsView(session, bus, presenter, Dean.getInstance().getCourseClassTO());
		}
		reportsPanel.clear();
		reportsPanel.add(reportsView);
	}

	@Override
	public void buildAdminsView() {
		adminsPanel.clear();
		if (!session.isInstitutionAdmin())
			return;
		adminsPanel.add(new GenericCourseClassAdminsView(session, presenter, Dean.getInstance().getCourseClassTO()));
	}

	private void initSearch() {
		txtSearch = new TextBox();
		txtSearch.addStyleName("txtSearch");
		txtSearch.setTitle("nome, " + (enrollWithCPF ? "CPF" : "email") + ", matrícula ou progresso");
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
		btnSearch.setSize(ButtonSize.MINI);
		btnSearch.setIcon(IconType.SEARCH);
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

		table.addColumn(new TextColumn<EnrollmentTO>() {
			@Override
			public String getValue(EnrollmentTO enrollmentTO) {
				return enrollmentTO.getPerson().getFullName();
			}
		}, "Nome");

		table.addColumn(new TextColumn<EnrollmentTO>() {
			@Override
			public String getValue(EnrollmentTO enrollmentTO) {
				if (enrollmentTO.getPerson().getEmail() != null && !"".equals(enrollmentTO.getPerson().getEmail()))
					return enrollmentTO.getPerson().getEmail();
				else
					return formHelper.formatCPF(enrollmentTO.getPerson().getCPF());
			}
		}, "Email/CPF");

		table.addColumn(new TextColumn<EnrollmentTO>() {
			@Override
			public String getValue(EnrollmentTO enrollmentTO) {
				return formHelper.getEnrollmentStateAsText(enrollmentTO.getEnrollment().getState());
			}
		}, "Matrícula");

		table.addColumn(new TextColumn<EnrollmentTO>() {
			@Override
			public String getValue(EnrollmentTO enrollmentTO) {
				return formHelper.getEnrollmentProgressAsText(EnrollmentCategory.getEnrollmentProgressDescription(enrollmentTO.getEnrollment()));
			}
		}, "Progresso");

		List<HasCell<EnrollmentTO, ?>> cells = new LinkedList<HasCell<EnrollmentTO, ?>>();
		cells.add(new EnrollmentActionsHasCell("Perfil", getGoToProfileDelegate()));
		cells.add(new EnrollmentActionsHasCell("Certificado", getGenerateCertificateDelegate()));
		cells.add(new EnrollmentActionsHasCell("Excluir", getDeleteEnrollmentDelegate()));
		cells.add(new EnrollmentActionsHasCell("Matricular", getStateChangeDelegate(EnrollmentState.enrolled)));
		cells.add(new EnrollmentActionsHasCell("Cancelar", getStateChangeDelegate(EnrollmentState.cancelled)));
		cells.add(new EnrollmentActionsHasCell("Negar", getStateChangeDelegate(EnrollmentState.denied)));
		cells.add(new EnrollmentActionsHasCell("Aceitar", getStateChangeDelegate(EnrollmentState.enrolled)));

		CompositeCell<EnrollmentTO> cell = new CompositeCell<EnrollmentTO>(cells);
		table.addColumn(new Column<EnrollmentTO, EnrollmentTO>(cell) {
			@Override
			public EnrollmentTO getValue(EnrollmentTO enrollmentTO) {
				return enrollmentTO;
			}
		}, "Ações");

		// Add a selection model to handle user selection.
		final SingleSelectionModel<EnrollmentTO> selectionModel = new SingleSelectionModel<EnrollmentTO>();
		table.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				//
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
	public void setEnrollmentList(List<EnrollmentTO> enrollmentsIn) {
		this.isEnabled = CourseClassState.active.equals(Dean.getInstance().getCourseClassTO().getCourseClass().getState());
		addEnrollmentsPanel.setVisible(isEnabled);
		
		numEnrollments = enrollmentsIn.size();
		maxEnrollments = Dean.getInstance().getCourseClassTO().getCourseClass().getMaxEnrollments();
		lblEnrollmentsCount.setText(numEnrollments + " / " + maxEnrollments);

		enrollmentsCurrent = new ArrayList<EnrollmentTO>(enrollmentsIn);
		enrollments = new ArrayList<EnrollmentTO>(enrollmentsIn);
		enrollmentsWrapper.clear();

		VerticalPanel panel = new VerticalPanel();
		panel.setWidth("400");
		panel.add(table);

		Image separatorBar = new Image("skins/first/icons/profile/separatorBar.png");
		separatorBar.addStyleName("fillWidth");

		final ListBox pageSizeListBox = new ListBox();
		// pageSizeListBox.addItem("1");
		// pageSizeListBox.addItem("10");
		pageSizeListBox.addItem("20");
		pageSizeListBox.addItem("50");
		pageSizeListBox.addItem("100");
		pageSizeListBox.setSelectedValue("" + pagination.getPageSize());
		pageSizeListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (pageSizeListBox.getValue().matches("[0-9]*"))
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
		enrollmentsCurrent = new ArrayList<EnrollmentTO>(enrollments);
		for (int i = 0; i < enrollmentsCurrent.size(); i++) {
			if (!matchesWithSearch(enrollmentsCurrent.get(i))) {
				enrollmentsCurrent.remove(i);
				i--;
			}
		}
		pagination.setRowData(enrollmentsCurrent);
		pagination.displayTableData(1);
	}

	private boolean matchesWithSearch(EnrollmentTO one){
		Person p = one.getPerson();
		Enrollment e = one.getEnrollment();
		if(p == null) return false;
		
		boolean fullNameMatch = matchesWithSearch(p.getFullName());
		boolean emailMatch = enrollWithCPF ? matchesWithSearch(p.getCPF()) : matchesWithSearch(p.getEmail());
		boolean enrollmentStateMatch = matchesWithSearch(formHelper.getEnrollmentStateAsText(e.getState()));
		boolean enrollmentProgressMatch = e.getProgress() != null && 
				matchesWithSearch(formHelper.getEnrollmentProgressAsText(EnrollmentCategory.getEnrollmentProgressDescription(e)).toLowerCase());
		
		return fullNameMatch || emailMatch || enrollmentStateMatch || enrollmentProgressMatch;
	}

	private boolean matchesWithSearch(String one){
		if(one == null) return false;
		return prepareForSearch(one).indexOf(prepareForSearch(txtSearch.getText())) >= 0;
	}
	
	private String prepareForSearch(String str){
		str = AsciiUtils.convertNonAscii(str).toLowerCase();
		return str.replaceAll("-", "").replaceAll("\\.", "");
	}
	
	@Override
	public void showModal() {
		errorModal.show();
	}

	private Delegate<EnrollmentTO> getStateChangeDelegate(final EnrollmentState state) {
		return new Delegate<EnrollmentTO>() {
			@Override
			public void execute(EnrollmentTO object) {
				presenter.changeEnrollmentState(object, state);
			}
		};
	}

	private Delegate<EnrollmentTO> getDeleteEnrollmentDelegate() {
		return new Delegate<EnrollmentTO>() {
			@Override
			public void execute(EnrollmentTO object) {
				presenter.deleteEnrollment(object);
			}
		};
	}

	private Delegate<EnrollmentTO> getGoToProfileDelegate() {
		return new Delegate<EnrollmentTO>() {
			@Override
			public void execute(EnrollmentTO object) {
				presenter.onUserClicked(object);
			}
		};
	}

	private Delegate<EnrollmentTO> getGenerateCertificateDelegate() {
		return new Delegate<EnrollmentTO>() {
			@Override
			public void execute(EnrollmentTO object) {
				presenter.onGenerateCertificate(object);
			}
		};
	}

	@SuppressWarnings("hiding")
  private class EnrollmentActionsActionCell<EnrollmentTO> extends ActionCell<EnrollmentTO> {
		
		public EnrollmentActionsActionCell(String message, Delegate<EnrollmentTO> delegate) {
			super(message, delegate);
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, EnrollmentTO value, NativeEvent event, ValueUpdater<EnrollmentTO> valueUpdater) {
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

	private class EnrollmentActionsHasCell implements HasCell<EnrollmentTO, EnrollmentTO> {
		private EnrollmentActionsActionCell<EnrollmentTO> cell;

		public EnrollmentActionsHasCell(String text, Delegate<EnrollmentTO> delegate) {
			final String actionName = text;
			cell = new EnrollmentActionsActionCell<EnrollmentTO>(text, delegate) {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context, EnrollmentTO object, SafeHtmlBuilder sb) {
					if (presenter.showActionButton(actionName, object)) {
						SafeHtml html = SafeHtmlUtils.fromTrustedString(buildButtonHTML(actionName));
						sb.append(html);
					} else
						sb.appendEscaped("");
				}
				
				private String buildButtonHTML(String actionName){
					Button btn = new Button();
					btn.setSize(ButtonSize.SMALL);
					btn.setTitle(actionName);
					if("Excluir".equals(actionName)){
						btn.setIcon(IconType.TRASH);
						btn.addStyleName("btnNotSelected");
					} else if("Cancelar".equals(actionName)){
						btn.setIcon(IconType.REMOVE);
						btn.addStyleName("btnSelected");
					} else if("Negar".equals(actionName)){
						btn.setIcon(IconType.THUMBS_DOWN);
						btn.addStyleName("btnSelected");
					} else if("Matricular".equals(actionName)){
						btn.setIcon(IconType.BOOK);
						btn.addStyleName("btnAction");
					} else if("Aceitar".equals(actionName)){
						btn.setIcon(IconType.THUMBS_UP);
						btn.addStyleName("btnAction");
					} else if("Perfil".equals(actionName)){
						btn.setIcon(IconType.USER);
						btn.addStyleName("btnNotSelected");
					} else if("Certificado".equals(actionName)){
						btn.setIcon(IconType.DOWNLOAD_ALT);
						btn.addStyleName("btnNotSelected");
					} 
					btn.addStyleName("btnIconSolo");
					return btn.toString();
				}
			};
		}

		@Override
		public Cell<EnrollmentTO> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<EnrollmentTO, EnrollmentTO> getFieldUpdater() {
			return null;
		}

		@Override
		public EnrollmentTO getValue(EnrollmentTO object) {
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
			value = courseClassTO.getCourseClass().getUUID();
			name = courseClassTO.getCourseVersionTO().getCourse().getTitle() + " - " + courseClassTO.getCourseClass().getName();
			if(CourseClassState.inactive.equals(courseClassTO.getCourseClass().getState())){
				name += " (Desabilitada)";
			}
			listBoxCourseClasses.addItem(name, value);
		}
	}

	@Override
	public void setUserEnrollmentIdentificationType(Boolean enrollWithCPF) {
		this.enrollWithCPF = enrollWithCPF;
		if (enrollWithCPF) {
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
	public void setHomeTabActive() {
		enrollmentsTab.setActive(true);
		configTab.setActive(false);
	}

	@Override
	public void showEnrollmentsPanel(boolean visible) {
		enrollmentsPanel.setVisible(visible);
	}
}
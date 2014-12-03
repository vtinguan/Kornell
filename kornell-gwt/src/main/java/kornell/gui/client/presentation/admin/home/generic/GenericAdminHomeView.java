package kornell.gui.client.presentation.admin.home.generic;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentCategory;
import kornell.core.entity.EnrollmentProgressDescription;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.Person;
import kornell.core.entity.RegistrationEnrollmentType;
import kornell.core.to.CourseClassTO;
import kornell.core.to.EnrollmentTO;
import kornell.core.to.UnreadChatThreadTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.UnreadMessagesCountChangedEvent;
import kornell.gui.client.event.UnreadMessagesCountChangedEventHandler;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEvent;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEventHandler;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.home.AdminHomeView;
import kornell.gui.client.presentation.message.MessagePresenter;
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
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericAdminHomeView extends Composite implements AdminHomeView, UnreadMessagesPerThreadFetchedEventHandler, UnreadMessagesCountChangedEventHandler {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminHomeView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private KornellSession session;
	private EventBus bus;
	private PlaceController placeCtrl;
	private ViewFactory viewFactory;
	private AdminHomeView.Presenter presenter;
	final CellTable<EnrollmentTO> table;
	private List<EnrollmentTO> enrollmentsCurrent;
	private List<EnrollmentTO> enrollmentsOriginal;
	private KornellPagination pagination;
	private TextBox txtSearch;
	private Button btnSearch;
	private boolean isEnabled;
	private Integer maxEnrollments = 0;
	private Integer numEnrollments = 0;
	private GenericCourseClassReportsView reportsView;
	private GenericCourseClassMessagesView messagesView;
	private FormHelper formHelper;
	private Timer updateTimer;
	private boolean canPerformEnrollmentAction = true;
	private MessagePresenter messagePresenter;
	private int totalCount = 0;

	@UiField
	FlowPanel adminHomePanel;
	@UiField
	FlowPanel enrollPanel;
	@UiField
	FlowPanel enrollmentsPanel;
	@UiField
	FlowPanel addEnrollmentsPanel;
	@UiField
	Tab enrollmentsTab;
	@UiField
	Tab enrollTab;
	@UiField
	Tab configTab;
	@UiField
	FlowPanel configPanel;
	@UiField
	Tab reportsTab;
	@UiField
	FlowPanel reportsPanel;
	@UiField
	Tab messagesTab;
	@UiField
	FlowPanel messagesPanel;

	@UiField
	Button btnAddEnrollment;
	@UiField
	Button btnAddEnrollmentBatch;
	@UiField
	TextBox txtFullName;
	@UiField
	TextBox txtEmail;
	@UiField
	TextArea txtAddEnrollmentBatch;
	@UiField
	Label identifierLabel;
	@UiField
	FlowPanel infoPanel;

	@UiField
	Modal errorModal;
	@UiField
	Label txtModal1;
	@UiField
	Label txtModal2;
	@UiField
	TextArea txtModalError;
	@UiField
	com.google.gwt.user.client.ui.Button btnModalOK;
	@UiField
	com.google.gwt.user.client.ui.Button btnModalCancel;

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
	private List<UnreadChatThreadTO> unreadChatThreadTOs;

	public GenericAdminHomeView(final KornellSession session, EventBus bus, PlaceController placeCtrl, ViewFactory viewFactory) {
		this.session = session;
		this.bus = bus;
		this.placeCtrl = placeCtrl;
		this.viewFactory = viewFactory;
		this.messagePresenter = viewFactory.getMessagePresenterCourseClass();
		this.messagePresenter.enableMessagesUpdate(false);
		initWidget(uiBinder.createAndBindUi(this));
		tabsPanel.setVisible(false);
		table = new CellTable<EnrollmentTO>();
		pagination = new KornellPagination(table, enrollmentsCurrent);
		formHelper = new FormHelper();
		bus.addHandler(UnreadMessagesPerThreadFetchedEvent.TYPE, this);
		bus.addHandler(UnreadMessagesCountChangedEvent.TYPE, this);

		txtModalError.setReadOnly(true);

		btnModalOK.setText("OK".toUpperCase());
		btnModalCancel.setText("Cancelar".toUpperCase());

		//btnAddEnrollmentBatchEnable.setHTML(btnAddEnrollmentBatchEnable.getText() + "&nbsp;&nbsp;&#x25BC;");


		enrollmentsTab.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.updateCourseClass(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID());
				messagePresenter.enableMessagesUpdate(false);
			}
		});

		enrollTab.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				messagePresenter.enableMessagesUpdate(false);
			}
		});

		configTab.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				buildConfigView(false);
				messagePresenter.enableMessagesUpdate(false);
			}
		});

		reportsTab.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				buildReportsView();
				messagePresenter.enableMessagesUpdate(false);
			}
		});

		messagesTab.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				messagePresenter.enableMessagesUpdate(true);
				buildMessagesView();
			}
		});
		
		updateTimer = new Timer() {
			@Override
			public void run() {
				filterEnrollments();
			}
		};

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
					messagePresenter.enableMessagesUpdate(false);
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
			adminHomePanel.add(tabsPanel);
			configPanel.clear();
			configTab.setActive(false);
			reportsPanel.clear();
			reportsTab.setActive(false);
			reportsView = null;
			messagesTab.setActive(false);
			messagesView = null;
			if (adminsTab != null)
				adminsTab.setActive(false);
			enrollmentsTab.setActive(true);
			enrollTab.setActive(false);
		}
	}

	@Override
	public void buildConfigView(boolean isCreationMode) {
		prepareAddNewCourseClass(isCreationMode);
		if (!isCreationMode) {
			configPanel.add(new GenericCourseClassConfigView(session, bus, placeCtrl, presenter, Dean.getInstance().getCourseClassTO()));
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
	public void buildMessagesView() {
		if (messagesView == null) {
			messagesView = new GenericCourseClassMessagesView(session, bus, placeCtrl, viewFactory, messagePresenter, Dean.getInstance().getCourseClassTO());
		}
		messagePresenter.filterAndShowThreads();
		messagesPanel.clear();
		messagesPanel.add(messagesView);
	}

	@Override
	public void buildAdminsView() {
		adminsPanel.clear();
		if (!session.isInstitutionAdmin())
			return;
		adminsPanel.add(new GenericCourseClassAdminsView(session, presenter, Dean.getInstance().getCourseClassTO()));
	}

	private void initSearch() {
		if(txtSearch == null){
			txtSearch = new TextBox();
			txtSearch.addStyleName("txtSearch");
			txtSearch.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					scheduleEnrollmentFilter();
				}
			});
			txtSearch.addKeyUpHandler(new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent event) {
					scheduleEnrollmentFilter();
				}
			});
			txtSearch.addValueChangeHandler(new ValueChangeHandler<String>() {
	
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					scheduleEnrollmentFilter();
	
				}
			});
			btnSearch = new Button("Pesquisar");
			btnSearch.setSize(ButtonSize.MINI);
			btnSearch.setIcon(IconType.SEARCH);
			btnSearch.addStyleName("btnNotSelected btnSearch");
			btnSearch.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					scheduleEnrollmentFilter();
				}
			});
		}
		txtSearch.setValue("");
		txtSearch.setTitle("nome, " + formHelper.getRegistrationEnrollmentTypeAsText(Dean.getInstance()
				.getCourseClassTO().getCourseClass().getRegistrationEnrollmentType()) + ", matrícula ou progresso");
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
				return enrollmentTO.getFullName();
			}
		}, "Nome");

		table.addColumn(new TextColumn<EnrollmentTO>() {
			@Override
			public String getValue(EnrollmentTO enrollmentTO) {
				return enrollmentTO.getUsername();
			}
		}, "Usuário");

		table.addColumn(new TextColumn<EnrollmentTO>() {
			@Override
			public String getValue(EnrollmentTO enrollmentTO) {
				return formHelper.getEnrollmentStateAsText(enrollmentTO.getEnrollment().getState());
			}
		}, "Matrícula");

		table.addColumn(new TextColumn<EnrollmentTO>() {
			@Override
			public String getValue(EnrollmentTO enrollmentTO) {
				String progressTxt = formHelper.getEnrollmentProgressAsText(EnrollmentCategory.getEnrollmentProgressDescription(enrollmentTO.getEnrollment()));
				if(EnrollmentProgressDescription.inProgress.equals(EnrollmentCategory.getEnrollmentProgressDescription(enrollmentTO.getEnrollment())) &&
						new Integer(100).equals(enrollmentTO.getEnrollment().getProgress())){
					progressTxt = "Aguardando Avaliação";
				} else if(EnrollmentProgressDescription.inProgress.equals(EnrollmentCategory.getEnrollmentProgressDescription(enrollmentTO.getEnrollment()))){
					progressTxt += ": " + enrollmentTO.getEnrollment().getProgress() + "%";
				} else if(EnrollmentProgressDescription.completed.equals(EnrollmentCategory.getEnrollmentProgressDescription(enrollmentTO.getEnrollment())) &&
						Dean.getInstance().getCourseClassTO().getCourseClass().getRequiredScore() != null && 
						Dean.getInstance().getCourseClassTO().getCourseClass().getRequiredScore().intValue() != 0 &&
						enrollmentTO.getEnrollment().getAssessmentScore() != null){
					progressTxt += " - Nota: " + enrollmentTO.getEnrollment().getAssessmentScore().intValue();
				}
				return progressTxt;
			}
		}, "Progresso");

		table.addColumn(new TextColumn<EnrollmentTO>() {
			@Override
			public String getValue(EnrollmentTO enrollmentTO) {
				return formHelper.dateToString(enrollmentTO.getEnrollment().getEnrolledOn());
			}
		}, "Data da Matrícula");

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

		/*// Add a selection model to handle user selection.
		final SingleSelectionModel<EnrollmentTO> selectionModel = new SingleSelectionModel<EnrollmentTO>();
		table.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				//
			}
		});*/
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("btnModalOK")
	void onModalOkButtonClicked(ClickEvent e) {
		presenter.onModalOkButtonClicked();
	}

	@UiHandler("btnModalCancel")
	void onModalCancelButtonClicked(ClickEvent e) {
		errorModal.hide();
	}

	@UiHandler("btnAddEnrollment")
	void onAddEnrollmentButtonClicked(ClickEvent e) {
		presenter.onAddEnrollmentButtonClicked(txtFullName.getText(), txtEmail.getText());
	}

	@Override
	public void setModalErrors(String title, String lbl1, String errors, String lbl2) {
		errorModal.setTitle(title);
		txtModal1.setText(lbl1);
		txtModalError.setText(errors);
		txtModal2.setText(lbl2);
	}

	@Override
	public void setEnrollmentList(List<EnrollmentTO> enrollmentsIn, boolean refresh) {
		
		enrollmentsOriginal = enrollmentsIn;
		this.isEnabled = CourseClassState.active.equals(Dean.getInstance().getCourseClassTO().getCourseClass().getState());
		addEnrollmentsPanel.setVisible(isEnabled);
		
		numEnrollments = enrollmentsIn.size();
		maxEnrollments = Dean.getInstance().getCourseClassTO().getCourseClass().getMaxEnrollments();
		lblEnrollmentsCount.setText(numEnrollments + " / " + maxEnrollments);
		
		if(!refresh)
			return;
		
		if(enrollmentsCurrent == null){

			enrollmentsWrapper.clear();
	
			VerticalPanel panel = new VerticalPanel();
			panel.setWidth("400");
			panel.add(table);
			
	
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
			FlowPanel tableTools = new FlowPanel();
			tableTools.addStyleName("marginTop25");
			tableTools.add(txtSearch);
			tableTools.add(btnSearch);
			tableTools.add(pageSizeListBox);
			enrollmentsWrapper.add(tableTools);
			enrollmentsWrapper.add(panel);
			enrollmentsWrapper.add(pagination);
		}
		
		filterEnrollments();
	}

	private void scheduleEnrollmentFilter() {
		updateTimer.cancel();
		updateTimer.schedule(500);
	}
	
	private void filterEnrollments(){
		if(StringUtils.isSome(txtSearch.getText().trim())){
			enrollmentsCurrent = new ArrayList<EnrollmentTO>();
			for (EnrollmentTO enrollmentTO : enrollmentsOriginal) {
				if (matchesWithSearch(enrollmentTO)) {
					enrollmentsCurrent.add(enrollmentTO);
				}
			}
			pagination.setRowData(enrollmentsCurrent);
		} else {
			pagination.setRowData(enrollmentsOriginal);
		}
		pagination.displayTableData(1);
	}

	private boolean matchesWithSearch(EnrollmentTO one){
		Enrollment e = one.getEnrollment();
		
		boolean fullNameMatch = matchesWithSearch(one.getFullName());
		boolean usernameMatch = matchesWithSearch(one.getUsername());
		boolean enrollmentStateMatch = matchesWithSearch(formHelper.getEnrollmentStateAsText(e.getState()));
		boolean enrollmentProgressMatch = e.getProgress() != null && 
				matchesWithSearch(formHelper.getEnrollmentProgressAsText(EnrollmentCategory.getEnrollmentProgressDescription(e)).toLowerCase());
		
		return fullNameMatch || usernameMatch || enrollmentStateMatch || enrollmentProgressMatch;
	}

	private boolean matchesWithSearch(String one){
		if(one == null) return false;
		return prepareForSearch(one).indexOf(prepareForSearch(txtSearch.getText().trim())) >= 0;
	}
	
	private String prepareForSearch(String str){
		str = AsciiUtils.convertNonAscii(str).toLowerCase();
		return str.replaceAll("-", "").replaceAll("\\.", "");
	}
	
	@Override
	public void showModal(boolean show) {
		if(show)
			errorModal.show();
		else
			errorModal.hide();
	}
	
	@Override
	public void setCanPerformEnrollmentAction(boolean allow){
		this.canPerformEnrollmentAction = allow;
	}

	private Delegate<EnrollmentTO> getStateChangeDelegate(final EnrollmentState state) {
		return new Delegate<EnrollmentTO>() {
			@Override
			public void execute(EnrollmentTO object) {
				if(canPerformEnrollmentAction){
					canPerformEnrollmentAction = false;
					presenter.changeEnrollmentState(object, state);
				}
			}
		};
	}

	private Delegate<EnrollmentTO> getDeleteEnrollmentDelegate() {
		return new Delegate<EnrollmentTO>() {
			@Override
			public void execute(EnrollmentTO object) {
				if(canPerformEnrollmentAction){
					canPerformEnrollmentAction = false;
					presenter.deleteEnrollment(object);
				}
			}
		};
	}

	private Delegate<EnrollmentTO> getGoToProfileDelegate() {
		return new Delegate<EnrollmentTO>() {
			@Override
			public void execute(EnrollmentTO object) {
				if(canPerformEnrollmentAction){
					presenter.onUserClicked(object);
				}
			}
		};
	}

	private Delegate<EnrollmentTO> getGenerateCertificateDelegate() {
		return new Delegate<EnrollmentTO>() {
			@Override
			public void execute(EnrollmentTO object) {
				if(canPerformEnrollmentAction){
					presenter.onGenerateCertificate(object);
				}
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
	public void setUserEnrollmentIdentificationType(RegistrationEnrollmentType registrationEnrollmentType) {
		infoPanel.clear();
		switch (registrationEnrollmentType) {
		case email:
			infoPanel.add(getLabel("Formato:", false));
			infoPanel.add(getLabel("\"nome;email\" ou somente \"email\".", true));
			infoPanel.add(getLabel("* Um participante por linha", true));
			infoPanel.add(getLabel("Exemplo:", false));
			infoPanel.add(getLabel("Nome Sobrenome;email@example.com", true));
			infoPanel.add(getLabel("email1@example.com", true));
			infoPanel.add(getLabel("email2@example.com", true));
			break;
		case cpf:
			infoPanel.add(getLabel("Formato:", false));
			infoPanel.add(getLabel("\"nome;cpf\"", true));
			infoPanel.add(getLabel("* Um participante por linha", true));
			infoPanel.add(getLabel("Exemplo:", false));
			infoPanel.add(getLabel("Nome Sobrenome;123.456.789-13", true));
			infoPanel.add(getLabel("Nome2 Sobrenome2;12345687913", true));
			break;
		case username:
			infoPanel.add(getLabel("Formato:", false));
			infoPanel.add(getLabel("\"nome;usuário\"", true));
			infoPanel.add(getLabel("* Um participante por linha", true));
			infoPanel.add(getLabel("Exemplo:", false));
			infoPanel.add(getLabel("Nome Sobrenome;12345", true));
			infoPanel.add(getLabel("Nome2 Sobrenome2;12346", true));
			break;
		default:
			break;
		}
		identifierLabel.setText(formHelper.getRegistrationEnrollmentTypeAsText(Dean.getInstance()
				.getCourseClassTO().getCourseClass().getRegistrationEnrollmentType())+":");
		initTable();
		initSearch();
	}
	
	private Label getLabel(String labelTxt, boolean isHighlight){
		Label lbl = new Label(labelTxt);
		if(!isHighlight)
			lbl.addStyleName("textInfoColor");
		return lbl;
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
	
	@Override
	public void showTabsPanel(boolean visible) {
		tabsPanel.setVisible(visible);
	}

	private void updateMessagesTabHeading() {
	  messagesTab.setHeading("Mensagens" + (totalCount > 0 ? " ("+totalCount+")" : ""));
  }
	
	@Override
  public void onUnreadMessagesPerThreadFetched(UnreadMessagesPerThreadFetchedEvent event) {
		unreadChatThreadTOs = event.getUnreadChatThreadTOs();
		refreshMessagesCount();
  }

	private void refreshMessagesCount() {
		if(unreadChatThreadTOs != null){
		  int count = 0;
			for (UnreadChatThreadTO unreadChatThreadTO : unreadChatThreadTOs) {
				if(Dean.getInstance().getCourseClassTO() != null && 
						Dean.getInstance().getCourseClassTO().getCourseClass().getUUID().equals(unreadChatThreadTO.getCourseClassUUID()))
						count = count + Integer.parseInt(unreadChatThreadTO.getUnreadMessages());
	    }
			totalCount  = count;
		} else {
			totalCount = 0;
		}
		updateMessagesTabHeading();
  }

	@Override
	public void onUnreadMessagesCountChanged(UnreadMessagesCountChangedEvent event) {
	  totalCount = event.isIncrement() ? totalCount + event.getCountChange() : totalCount - event.getCountChange();
	  updateMessagesTabHeading();
	}

}
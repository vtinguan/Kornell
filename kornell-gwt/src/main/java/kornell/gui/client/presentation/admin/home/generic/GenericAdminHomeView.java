package kornell.gui.client.presentation.admin.home.generic;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.admin.home.AdminHomeView;
import kornell.gui.client.uidget.KornellPagination;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Collapse;
import com.github.gwtbootstrap.client.ui.CollapseTrigger;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell.Context;
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
import com.google.gwt.i18n.client.Constants.DefaultStringValue;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class GenericAdminHomeView extends Composite implements AdminHomeView {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminHomeView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private AdminHomeView.Presenter presenter;
	final CellTable<Enrollment> table;
	private List<Enrollment> enrollmentsCurrent;
	private List<Enrollment> enrollments;
	private KornellPagination pagination;

	private TextBox txtSearch;
	private Button btnSearch;
	private String currentSearch;
	
	private boolean forbidProfileView;
	
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
	Modal errorModal;
	@UiField
	TextArea txtModalError;
	@UiField
	Button btnModalOK;
	@UiField
	Button btnModalCancel;

	@UiField
	FlowPanel enrollmentsWrapper;

	// TODO i18n xml
	public GenericAdminHomeView() {
		initWidget(uiBinder.createAndBindUi(this));
		
		table = new CellTable<Enrollment>();
		initTable();
		pagination = new KornellPagination(table, enrollmentsCurrent);
		
		trigger.setTarget("#toggle");
		collapse.setId("toggle");
		

		btnModalOK.setText("OK".toUpperCase());
		btnModalCancel.setText("Cancelar".toUpperCase());
		txtModalError.setEnabled(false);
	}

	private void initTable() {
	    
		table.addStyleName("enrollmentsCellTable");
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
				
		table.addColumn(new TextColumn<Enrollment>() {
			@Override
			public String getValue(Enrollment enrollment) {
				return enrollment.getPerson().getFullName();
			}
		}, "Nome");
		
		table.addColumn(new TextColumn<Enrollment>() {
			@Override
			public String getValue(Enrollment enrollment) {
				return enrollment.getPerson().getEmail();
			}
		}, "Email");

		table.addColumn(new TextColumn<Enrollment>() {
			@Override	
			public String getValue(Enrollment enrollment) {
				return getEnrollmentStateAsText(enrollment.getState());
			}
		}, "Matrícula");

		table.addColumn(new TextColumn<Enrollment>() {
			@Override	
			public String getValue(Enrollment enrollment) {
				return getEnrollmentProgressAsText(enrollment.getProgress());
			}
		}, "Progresso");
	
	    List<HasCell<Enrollment, ?>> cells = new LinkedList<HasCell<Enrollment, ?>>();
	    cells.add(new EnrollmentActionsHasCell("Aceitar", getStateChangeDelegate(EnrollmentState.enrolled)));
	    cells.add(new EnrollmentActionsHasCell("Negar", getStateChangeDelegate(EnrollmentState.denied)));
	    cells.add(new EnrollmentActionsHasCell("Cancelar", getStateChangeDelegate(EnrollmentState.cancelled)));
	    cells.add(new EnrollmentActionsHasCell("Matricular", getStateChangeDelegate(EnrollmentState.enrolled)));
	    
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
		enrollmentsCurrent = new ArrayList<Enrollment>(enrollmentsIn);
		enrollments = new ArrayList<Enrollment>(enrollmentsIn);
		enrollmentsWrapper.clear();
		
		VerticalPanel panel = new VerticalPanel();
		panel.setWidth("400");
		panel.add(table);
		
		Image separatorBar = new Image("skins/first/icons/profile/separatorBar.png");
		separatorBar.addStyleName("fillWidth");
		
		final ListBox pageSizeListBox = new ListBox();
		pageSizeListBox.addItem("1");
		pageSizeListBox.addItem("10");
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
		
		txtSearch = new TextBox();
		txtSearch.addStyleName("txtSearch");
		txtSearch.setTitle("nome, email, matrícula ou progresso");
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
	
	
		
		enrollmentsWrapper.add(separatorBar);
		enrollmentsWrapper.add(txtSearch);
		enrollmentsWrapper.add(btnSearch);
		enrollmentsWrapper.add(pageSizeListBox);
		enrollmentsWrapper.add(panel);
		enrollmentsWrapper.add(pagination);
		
		pagination.setRowData(enrollmentsCurrent);
		pagination.displayTableData(1);
	}

	private void filterEnrollments() {
		if(!txtSearch.getText().equals(currentSearch)){
			enrollmentsCurrent = new ArrayList<Enrollment>(enrollments);
			Enrollment enrollment;
			GWT.log("size: "+enrollmentsCurrent.size());
			for (int i = 0; i < enrollmentsCurrent.size(); i++) {
		    	enrollment = enrollmentsCurrent.get(i);
				boolean fullNameMatch = enrollment.getPerson() != null && enrollment.getPerson().getFullName() != null &&
						enrollment.getPerson().getFullName().toLowerCase().indexOf(txtSearch.getText().toLowerCase()) >= 0;
				boolean emailMatch = enrollment.getPerson() != null && enrollment.getPerson().getEmail() != null &&
						enrollment.getPerson().getEmail().toLowerCase().indexOf(txtSearch.getText().toLowerCase()) >= 0;
				boolean enrollmentStateMatch = enrollment.getPerson() != null && enrollment.getState() != null &&
						getEnrollmentStateAsText(enrollment.getState()).toLowerCase().indexOf(txtSearch.getText().toLowerCase()) >= 0;
				boolean enrollmentProgressMatch = enrollment.getPerson() != null && enrollment.getProgress() != null &&
						getEnrollmentProgressAsText(enrollment.getProgress()).toLowerCase().indexOf(txtSearch.getText().toLowerCase()) >= 0;
				if(!fullNameMatch && !emailMatch && !enrollmentStateMatch && !enrollmentProgressMatch){
					enrollmentsCurrent.remove(i);
					i--;
				}
			}
			pagination.setRowData(enrollmentsCurrent);
			pagination.displayTableData(1);
		}
	}

	private String getEnrollmentProgressAsText(Integer progress) {
		if(progress == null || progress == 0){
			return "A Iniciar";
		} else if(progress < 100){
			return "Em andamento";
		} else {
			return "Concluído";
		}
	}
	
	private String getEnrollmentStateAsText(EnrollmentState state){
		switch (state) {
		case notEnrolled:
			return constants.notEnrolled();
		case preEnrolled:
			return constants.preEnrolled();
		case enrolled:
			return constants.enrolled();
		case requested:
			return constants.requested();
		case denied:
			return constants.denied();
		case cancelled:
			return constants.cancelled();
		default:
			return "";
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
	        	forbidProfileView = true;
	        	presenter.changeEnrollmentState(object, state);
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
			event.stopPropagation();
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
			        if(presenter.showActionButton(actionName, object.getState())){
			        	//super.render(context, object, sb);
			        	SafeHtml html = SafeHtmlUtils.fromTrustedString("<button type=\"button\" class=\"gwt-Button btnEnrollmentsCellTable "+
			        			(("Cancelar".equals(actionName) || "Negar".equals(actionName)) ? "btnSelected" : "btnAction")
			        			+"\">"+actionName.toUpperCase()+"</button>");
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
}
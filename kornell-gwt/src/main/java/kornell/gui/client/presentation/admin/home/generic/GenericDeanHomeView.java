package kornell.gui.client.presentation.admin.home.generic;

import java.util.LinkedList;
import java.util.List;

import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.gui.client.presentation.admin.home.DeanHomeView;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Collapse;
import com.github.gwtbootstrap.client.ui.CollapseTrigger;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class GenericDeanHomeView extends Composite implements DeanHomeView {

	interface MyUiBinder extends UiBinder<Widget, GenericDeanHomeView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private DeanHomeView.Presenter presenter;

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
	FlowPanel enrollmentsWrapper;

	// TODO i18n xml
	public GenericDeanHomeView() {
		initWidget(uiBinder.createAndBindUi(this));
		trigger.setTarget("#toggle");
		collapse.setId("toggle");

	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}


	@UiHandler("btnAddEnrollment")
	void onAddEnrollmentButtonClicked(ClickEvent e) {
		presenter.onAddEnrollmentButtonClicked(txtFullName.getText(), txtEmail.getText());
	}


	@UiHandler("btnAddEnrollmentBatch")
	void doLogin(ClickEvent e) {
		presenter.onAddEnrollmentBatchButtonClicked(txtAddEnrollmentBatch.getText());
	}

	@Override
	public void setEnrollmentList(List<Enrollment> enrollments) {
		enrollmentsWrapper.clear();
	    
		// Create a CellTable.
		final CellTable<Enrollment> table = new CellTable<Enrollment>();
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
				return enrollment.getState().toString();
			}
		}, "Estado da Matrícula");
	
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
						Enrollment selected = selectionModel
								.getSelectedObject();
						if (selected != null) {
							// Window.alert("Você selecionou o " +
							// selected.getPerson().getFullName());
						}
					}
				});

		// Set the total row count. This isn't strictly necessary,
		// but it affects paging calculations, so its good habit to
		// keep the row count up to date.
		table.setRowCount(enrollments.size(), true);

		// Push the data into the widget.
		table.setRowData(0, enrollments);

		VerticalPanel panel = new VerticalPanel();
		panel.setBorderWidth(1);
		panel.setWidth("400");
		panel.add(table);

		enrollmentsWrapper.add(panel);

	}

	private Delegate<Enrollment> getStateChangeDelegate(final EnrollmentState state) {
		return new Delegate<Enrollment>() {
	        @Override
	        public void execute(Enrollment object) {
	        	presenter.changeEnrollmentState(object, state);
	        }
	    };
	}
	
	private class EnrollmentActionsHasCell implements HasCell<Enrollment, Enrollment> {
		private ActionCell<Enrollment> cell;

		public EnrollmentActionsHasCell(String text, Delegate<Enrollment> delegate) {
			final String actionName = text;
			cell = new ActionCell<Enrollment>(text, delegate){
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context, Enrollment object, SafeHtmlBuilder sb) {
			        if(presenter.showActionButton(actionName, object.getState())){
			        	//super.render(context, object, sb);
			        	SafeHtml html = SafeHtmlUtils.fromTrustedString("<button type=\"button\" class=\"gwt-Button btnEnrollmentsCellTable "+
			        			(("Cancelar".equals(actionName) || "Negar".equals(actionName)) ? "btnSelected" : "btnAction")
			        			+"\">"+actionName+"</button>");
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
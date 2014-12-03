package kornell.gui.client.presentation.admin.courseClasses.generic;

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
import kornell.core.entity.RegistrationEnrollmentType;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassTO;
import kornell.core.to.UnreadChatThreadTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.UnreadMessagesCountChangedEvent;
import kornell.gui.client.event.UnreadMessagesCountChangedEventHandler;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEvent;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEventHandler;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.courseClasses.AdminCourseClassesPresenter;
import kornell.gui.client.presentation.admin.courseClasses.AdminCourseClassesView;
import kornell.gui.client.presentation.admin.home.AdminHomePlace;
import kornell.gui.client.presentation.admin.home.generic.GenericCourseClassAdminsView;
import kornell.gui.client.presentation.admin.home.generic.GenericCourseClassConfigView;
import kornell.gui.client.presentation.admin.home.generic.GenericCourseClassMessagesView;
import kornell.gui.client.presentation.admin.home.generic.GenericCourseClassReportsView;
import kornell.gui.client.presentation.message.MessagePresenter;
import kornell.gui.client.presentation.util.AsciiUtils;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.uidget.KornellPagination;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Tab;
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

public class GenericAdminCourseClassesView extends Composite implements AdminCourseClassesView {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminCourseClassesView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private KornellSession session;
	private EventBus bus;
	private PlaceController placeCtrl;
	private ViewFactory viewFactory;
	private AdminCourseClassesView.Presenter presenter;
	final CellTable<CourseClassTO> table;
	private List<CourseClassTO> courseClassTOs;
	private KornellPagination pagination;
	private FormHelper formHelper = GWT.create(FormHelper.class);

	@UiField
	FlowPanel adminHomePanel;
	@UiField
	FlowPanel courseClassesPanel;
	@UiField
	Button btnAddCourseClass;

	Tab adminsTab;
	FlowPanel adminsPanel;
	private List<UnreadChatThreadTO> unreadChatThreadTOs;

	// TODO i18n xml
	public GenericAdminCourseClassesView(final KornellSession session, EventBus bus, PlaceController placeCtrl, ViewFactory viewFactory) {
		this.session = session;
		this.bus = bus;
		this.placeCtrl = placeCtrl;
		this.viewFactory = viewFactory;
		initWidget(uiBinder.createAndBindUi(this));
		table = new CellTable<CourseClassTO>();
		pagination = new KornellPagination(table, courseClassTOs, 15);
		btnAddCourseClass.setText("Criar Nova Turma");

		btnAddCourseClass.setVisible(session.isInstitutionAdmin());
		btnAddCourseClass.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (session.isInstitutionAdmin()) {
				}
			}
		});
	}
	
	private void initTable() {

		table.addStyleName("enrollmentsCellTable");
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		for (int i = 0; table.getColumnCount() > 0;) {
			table.removeColumn(i);
		}

		table.addColumn(new TextColumn<CourseClassTO>() {
			@Override
			public String getValue(CourseClassTO courseClassTO) {
				return courseClassTO.getCourseVersionTO().getCourse().getTitle();
			}
		}, "Curso");

		table.addColumn(new TextColumn<CourseClassTO>() {
			@Override
			public String getValue(CourseClassTO courseClassTO) {
				return courseClassTO.getCourseVersionTO().getCourseVersion().getName();
			}
		}, "Versão");
		
		table.addColumn(new TextColumn<CourseClassTO>() {
			@Override
			public String getValue(CourseClassTO courseClassTO) {
				return courseClassTO.getCourseClass().getName();
			}
		}, "Turma");
		
		table.addColumn(new TextColumn<CourseClassTO>() {
			@Override
			public String getValue(CourseClassTO courseClassTO) {
				return formHelper.getCourseClassStateAsText(courseClassTO.getCourseClass().getState());
			}
		}, "Status");
		
		table.addColumn(new TextColumn<CourseClassTO>() {
			@Override
			public String getValue(CourseClassTO courseClassTO) {
				return formHelper.dateToString(courseClassTO.getCourseClass().getCreatedAt());
			}
		}, "Data de Criação");

		List<HasCell<CourseClassTO, ?>> cells = new LinkedList<HasCell<CourseClassTO, ?>>();
		cells.add(new EnrollmentActionsHasCell("Editar", getStateChangeDelegate(EnrollmentState.enrolled)));

		CompositeCell<CourseClassTO> cell = new CompositeCell<CourseClassTO>(cells);
		table.addColumn(new Column<CourseClassTO, CourseClassTO>(cell) {
			@Override
			public CourseClassTO getValue(CourseClassTO courseClassTO) {
				return courseClassTO;
			}
		}, "Ações");
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	private Delegate<CourseClassTO> getStateChangeDelegate(final EnrollmentState state) {
		return new Delegate<CourseClassTO>() {
			@Override
			public void execute(CourseClassTO courseClassTO) {
				placeCtrl.goTo(new AdminHomePlace(courseClassTO.getCourseClass().getUUID()));
			}
		};
	}

	@SuppressWarnings("hiding")
  private class EnrollmentActionsActionCell<CourseClassTO> extends ActionCell<CourseClassTO> {
		
		public EnrollmentActionsActionCell(String message, Delegate<CourseClassTO> delegate) {
			super(message, delegate);
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, CourseClassTO value, NativeEvent event, ValueUpdater<CourseClassTO> valueUpdater) {
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

	private class EnrollmentActionsHasCell implements HasCell<CourseClassTO, CourseClassTO> {
		private EnrollmentActionsActionCell<CourseClassTO> cell;

		public EnrollmentActionsHasCell(String text, Delegate<CourseClassTO> delegate) {
			final String actionName = text;
			cell = new EnrollmentActionsActionCell<CourseClassTO>(text, delegate) {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context, CourseClassTO object, SafeHtmlBuilder sb) {
						SafeHtml html = SafeHtmlUtils.fromTrustedString(buildButtonHTML(actionName));
						sb.append(html);
				}
				
				private String buildButtonHTML(String actionName){
					Button btn = new Button();
					btn.setSize(ButtonSize.SMALL);
					btn.setTitle(actionName);
					if("Editar".equals(actionName)){
						btn.setIcon(IconType.EDIT);
						btn.addStyleName("btnAction");
					}
					btn.addStyleName("btnIconSolo");
					return btn.toString();
				}
			};
		}

		@Override
		public Cell<CourseClassTO> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<CourseClassTO, CourseClassTO> getFieldUpdater() {
			return null;
		}

		@Override
		public CourseClassTO getValue(CourseClassTO object) {
			return object;
		}
	}


	@Override
	public void setCourseClasses(List<CourseClassTO> courseClasses) {
		String name, value;
		for (CourseClassTO courseClassTO : courseClasses) {
			value = courseClassTO.getCourseClass().getUUID();
			name = courseClassTO.getCourseVersionTO().getCourse().getTitle() + " - " + courseClassTO.getCourseClass().getName();
			if(CourseClassState.inactive.equals(courseClassTO.getCourseClass().getState())){
				name += " (Desabilitada)";
			}
		}
		this.courseClassTOs = courseClasses;
		VerticalPanel panel = new VerticalPanel();
		panel.setWidth("400");
		panel.add(table);
		courseClassesPanel.add(panel);
		courseClassesPanel.add(pagination);
		pagination.setRowData(courseClasses);
		pagination.displayTableData(1);
		initTable();
	}
	
	@Override
  public void setPresenter(AdminCourseClassesPresenter adminCourseClassesPresenter) {
	  this.presenter = adminCourseClassesPresenter;
	  
  }


}
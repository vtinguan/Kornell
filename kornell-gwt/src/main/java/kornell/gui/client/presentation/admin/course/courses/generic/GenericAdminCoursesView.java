package kornell.gui.client.presentation.admin.course.courses.generic;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import java.util.LinkedList;
import java.util.List;

import kornell.api.client.KornellSession;
import kornell.core.entity.Course;
import kornell.core.entity.EnrollmentState;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.presentation.admin.course.course.AdminCoursePlace;
import kornell.gui.client.presentation.admin.course.course.AdminCourseView;
import kornell.gui.client.presentation.admin.course.course.generic.GenericAdminCourseView;
import kornell.gui.client.presentation.admin.course.courses.AdminCoursesView;
import kornell.gui.client.uidget.KornellPagination;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Tab;
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericAdminCoursesView extends Composite implements AdminCoursesView {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminCoursesView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private PlaceController placeCtrl;
	final CellTable<Course> table;
	private List<Course> courses;
	private KornellPagination pagination;

	@UiField
	FlowPanel adminHomePanel;
	@UiField
	FlowPanel coursesPanel;
	@UiField
	FlowPanel createCoursePanel;
	@UiField
	Button btnAddCourse;

	Tab adminsTab;
	FlowPanel adminsPanel;
	private AdminCourseView view;

	public GenericAdminCoursesView(final KornellSession session, final EventBus bus, final PlaceController placeCtrl, final ViewFactory viewFactory) {
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		table = new CellTable<Course>();
		pagination = new KornellPagination(table, courses, 15);
		btnAddCourse.setText("Criar Novo Curso");


		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						if(createCoursePanel.getWidgetCount() > 0){
							createCoursePanel.clear();
						}
						coursesPanel.setVisible(true);
						view = null;
					}
				});

		btnAddCourse.setVisible(session.isInstitutionAdmin());
		btnAddCourse.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (session.isPlatformAdmin()) {
					coursesPanel.setVisible(false);
					view = viewFactory.getAdminCourseView();
					view.setPresenter(viewFactory.getAdminCoursePresenter());
					view.init();
					createCoursePanel.add(view);
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

		table.addColumn(new TextColumn<Course>() {
			@Override
			public String getValue(Course course) {
				return course.getCode();
			}
		}, "Código");

		table.addColumn(new TextColumn<Course>() {
			@Override
			public String getValue(Course course) {
				return course.getTitle();
			}
		}, "Nome");

		table.addColumn(new TextColumn<Course>() {
			@Override
			public String getValue(Course course) {
				return course.getDescription();
			}
		}, "Descrição");

		List<HasCell<Course, ?>> cells = new LinkedList<HasCell<Course, ?>>();
		cells.add(new EnrollmentActionsHasCell("Editar", getStateChangeDelegate(EnrollmentState.enrolled)));

		CompositeCell<Course> cell = new CompositeCell<Course>(cells);
		table.addColumn(new Column<Course, Course>(cell) {
			@Override
			public Course getValue(Course course) {
				return course;
			}
		}, "Ações");
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

	private Delegate<Course> getStateChangeDelegate(final EnrollmentState state) {
		return new Delegate<Course>() {
			@Override
			public void execute(Course course) {
				placeCtrl.goTo(new AdminCoursePlace(course.getUUID()));
			}
		};
	}

	@SuppressWarnings("hiding")
  private class EnrollmentActionsActionCell<Course> extends ActionCell<Course> {
		
		public EnrollmentActionsActionCell(String message, Delegate<Course> delegate) {
			super(message, delegate);
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, Course value, NativeEvent event, ValueUpdater<Course> valueUpdater) {
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

	private class EnrollmentActionsHasCell implements HasCell<Course, Course> {
		private EnrollmentActionsActionCell<Course> cell;

		public EnrollmentActionsHasCell(String text, Delegate<Course> delegate) {
			final String actionName = text;
			cell = new EnrollmentActionsActionCell<Course>(text, delegate) {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context, Course object, SafeHtmlBuilder sb) {
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
		public Cell<Course> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<Course, Course> getFieldUpdater() {
			return null;
		}

		@Override
		public Course getValue(Course object) {
			return object;
		}
	}


	@Override
	public void setCourses(List<Course> courses) {
		this.courses = courses;
		VerticalPanel panel = new VerticalPanel();
		panel.setWidth("400");
		panel.add(table);
		coursesPanel.add(panel);
		coursesPanel.add(pagination);
		pagination.setRowData(courses);
		pagination.displayTableData(1);
		initTable();
	}


}
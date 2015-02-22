package kornell.gui.client.presentation.admin.courseversion.courseversions.generic;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import java.util.LinkedList;
import java.util.List;

import kornell.api.client.KornellSession;
import kornell.core.entity.CourseVersion;
import kornell.core.entity.EnrollmentState;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionPlace;
import kornell.gui.client.presentation.admin.courseversion.courseversion.generic.GenericAdminCourseVersionView;
import kornell.gui.client.presentation.admin.courseversion.courseversions.AdminCourseVersionsView;
import kornell.gui.client.presentation.util.FormHelper;
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

public class GenericAdminCourseVersionsView extends Composite implements AdminCourseVersionsView {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminCourseVersionsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private PlaceController placeCtrl;
	final CellTable<CourseVersion> table;
	private List<CourseVersion> courseVersions;
	private KornellPagination pagination;
	private FormHelper formHelper = GWT.create(FormHelper.class);

	@UiField
	FlowPanel adminHomePanel;
	@UiField
	FlowPanel courseVersionsPanel;
	@UiField
	FlowPanel createVersionPanel;
	@UiField
	Button btnAddCourseVersion;

	Tab adminsTab;
	FlowPanel adminsPanel;

	public GenericAdminCourseVersionsView(final KornellSession session, final EventBus bus, final PlaceController placeCtrl, final ViewFactory viewFactory) {
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		table = new CellTable<CourseVersion>();
		pagination = new KornellPagination(table, courseVersions, 15);
		btnAddCourseVersion.setText("Criar Nova Versão");


		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						if(createVersionPanel.getWidgetCount() > 0){
							createVersionPanel.clear();
						}
						courseVersionsPanel.setVisible(true);
					}
				});

		btnAddCourseVersion.setVisible(session.isInstitutionAdmin());
		btnAddCourseVersion.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (session.isPlatformAdmin()) {
					courseVersionsPanel.setVisible(false);
					GenericAdminCourseVersionView view = new GenericAdminCourseVersionView(session, bus, placeCtrl);
					view.setPresenter(viewFactory.getAdminCourseVersionPresenter());
					view.init();
					createVersionPanel.add(view);
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

		table.addColumn(new TextColumn<CourseVersion>() {
			@Override
			public String getValue(CourseVersion courseVersion) {
				return courseVersion.getName();
			}
		}, "Nome");

		table.addColumn(new TextColumn<CourseVersion>() {
			@Override
			public String getValue(CourseVersion courseVersion) {
				return courseVersion.getContentSpec().toString();
			}
		}, "Tipo");

		table.addColumn(new TextColumn<CourseVersion>() {
			@Override
			public String getValue(CourseVersion courseVersion) {
				return courseVersion.getDistributionPrefix();
			}
		}, "Prefixo de Distribuição");
		
		table.addColumn(new TextColumn<CourseVersion>() {
			@Override
			public String getValue(CourseVersion courseVersion) {
				return courseVersion.isDisabled() ? "Desativada" : "Ativa";
			}
		}, "Status");
		
		table.addColumn(new TextColumn<CourseVersion>() {
			@Override
			public String getValue(CourseVersion courseVersion) {
				return formHelper.dateToString(courseVersion.getVersionCreatedAt());
			}
		}, "Data de Criação");

		List<HasCell<CourseVersion, ?>> cells = new LinkedList<HasCell<CourseVersion, ?>>();
		cells.add(new EnrollmentActionsHasCell("Editar", getStateChangeDelegate(EnrollmentState.enrolled)));

		CompositeCell<CourseVersion> cell = new CompositeCell<CourseVersion>(cells);
		table.addColumn(new Column<CourseVersion, CourseVersion>(cell) {
			@Override
			public CourseVersion getValue(CourseVersion courseVersion) {
				return courseVersion;
			}
		}, "Ações");
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

	private Delegate<CourseVersion> getStateChangeDelegate(final EnrollmentState state) {
		return new Delegate<CourseVersion>() {
			@Override
			public void execute(CourseVersion courseVersion) {
				placeCtrl.goTo(new AdminCourseVersionPlace(courseVersion.getUUID()));
			}
		};
	}

	@SuppressWarnings("hiding")
  private class EnrollmentActionsActionCell<CourseVersion> extends ActionCell<CourseVersion> {
		
		public EnrollmentActionsActionCell(String message, Delegate<CourseVersion> delegate) {
			super(message, delegate);
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, CourseVersion value, NativeEvent event, ValueUpdater<CourseVersion> valueUpdater) {
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

	private class EnrollmentActionsHasCell implements HasCell<CourseVersion, CourseVersion> {
		private EnrollmentActionsActionCell<CourseVersion> cell;

		public EnrollmentActionsHasCell(String text, Delegate<CourseVersion> delegate) {
			final String actionName = text;
			cell = new EnrollmentActionsActionCell<CourseVersion>(text, delegate) {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context, CourseVersion object, SafeHtmlBuilder sb) {
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
		public Cell<CourseVersion> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<CourseVersion, CourseVersion> getFieldUpdater() {
			return null;
		}

		@Override
		public CourseVersion getValue(CourseVersion object) {
			return object;
		}
	}


	@Override
	public void setCourseVersions(List<CourseVersion> courseVersions) {
		this.courseVersions = courseVersions;
		VerticalPanel panel = new VerticalPanel();
		panel.setWidth("400");
		panel.add(table);
		courseVersionsPanel.add(panel);
		courseVersionsPanel.add(pagination);
		pagination.setRowData(courseVersions);
		pagination.displayTableData(1);
		initTable();
	}


}
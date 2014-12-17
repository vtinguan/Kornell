package kornell.gui.client.presentation.course.generic;

import java.util.logging.Logger;

import kornell.api.client.KornellSession;
import kornell.core.entity.EnrollmentCategory;
import kornell.gui.client.event.ShowDetailsEvent;
import kornell.gui.client.event.ShowDetailsEventHandler;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionPresenter;
import kornell.gui.client.presentation.course.ClassroomView;
import kornell.gui.client.presentation.course.generic.details.GenericCourseDetailsView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericClassroomView extends Composite implements ClassroomView, ShowDetailsEventHandler {
	interface MyUiBinder extends UiBinder<Widget, GenericClassroomView> {
	}
	private PlaceController placeCtrl;
	private KornellSession session;
	private EventBus bus;

	Logger logger = Logger.getLogger(AdminInstitutionPresenter.class.getName());
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	FlowPanel contentPanel;
	@UiField
	FlowPanel detailsPanel;
	
	private boolean showCourseClassContent;
	
	private GenericCourseDetailsView detailsView;

	private Presenter presenter;

	public GenericClassroomView(PlaceController placeCtrl, KornellSession session, EventBus bus) {
		this.placeCtrl = placeCtrl;
		this.session = session;
		this.bus = bus;
		bus.addHandler(ShowDetailsEvent.TYPE,this);
		initWidget(uiBinder.createAndBindUi(this));
		detailsPanel.setVisible(true);
		contentPanel.setVisible(false);
	}

	@Override
	public void display(boolean isEnrolled) {
		presenter.stopSequencer();
		this.showCourseClassContent = isEnrolled;
		if(this.showCourseClassContent){
			presenter.startSequencer();
		}
		detailsView = new GenericCourseDetailsView(bus, session, placeCtrl);
		detailsView.setPresenter(presenter);
		detailsView.initData();
		detailsPanel.clear();
		detailsPanel.add(detailsView);
		boolean showDetails = !isEnrolled || EnrollmentCategory.isFinished(Dean.getInstance().getCourseClassTO().getEnrollment());
		bus.fireEvent(new ShowDetailsEvent(showDetails));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public FlowPanel getContentPanel() {
		return contentPanel;
	}

	@Override
	public void onShowDetails(ShowDetailsEvent event) {
		boolean showDetails = event.isShowDetails();
		contentPanel.setVisible(!showDetails);
		detailsPanel.setVisible(showDetails);
		if(showDetails)
			presenter.fireProgressEvent();
	}
}

package kornell.gui.client.presentation.admin.courseversion.courseversion;

import java.util.logging.Logger;

import javax.swing.text.View;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.entity.CourseVersion;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.Wizard;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardElement;

public class AdminCourseVersionContentPresenter implements AdminCourseVersionContentView.Presenter {
	Logger logger = Logger.getLogger(AdminCourseVersionContentPresenter.class.getName());
	private AdminCourseVersionContentView view;
	private KornellSession session;
	private PlaceController placeController;
	private EventBus bus;
	Place defaultPlace;
	private ViewFactory viewFactory;
	private CourseVersion courseVersion;

	public AdminCourseVersionContentPresenter(KornellSession session, PlaceController placeController, EventBus bus,
			Place defaultPlace, ViewFactory viewFactory) {
		this.session = session;
		this.placeController = placeController;
		this.bus = bus;
		this.defaultPlace = defaultPlace;
		this.viewFactory = viewFactory;
	}

	@Override
	public void init(CourseVersion courseVersion) {
		if (session.isInstitutionAdmin()) {
			view = viewFactory.getAdminCourseVersionContentView();
			view.init(courseVersion);
			if (view.getPresenter() == null) {
				view.setPresenter(this);
			}
		} else {
			logger.warning("Hey, only admins are allowed to see this! " + this.getClass().getName());
			placeController.goTo(defaultPlace);
		}
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	@Override
	public void wizardElementClicked(Wizard wizard, WizardElement wizardElement) {
		view.displaySlidePanel(false);
		view.updateSidePanel(wizard, wizardElement);
		view.updateSlidePanel(wizard, wizardElement);
		view.displaySlidePanel(true);
	}

	public AdminCourseVersionContentView getView() {
		return view;
	}

}
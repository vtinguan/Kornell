package kornell.gui.client.presentation.admin.courseversion.courseversion;

import java.util.logging.Logger;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseVersion;
import kornell.core.entity.EntityFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.mvp.PlaceUtils;
import kornell.gui.client.presentation.admin.courseclass.courseclasses.AdminCourseClassesPresenter;
import kornell.gui.client.presentation.admin.courseversion.courseversions.AdminCourseVersionsPlace;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class AdminCourseVersionPresenter implements AdminCourseVersionView.Presenter {
	Logger logger = Logger.getLogger(AdminCourseVersionPresenter.class.getName());
	private AdminCourseVersionView view;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private KornellSession session;
	private PlaceController placeController;
	private EventBus bus;
	Place defaultPlace;
	EntityFactory entityFactory;
	private ViewFactory viewFactory;

	public AdminCourseVersionPresenter(KornellSession session,
			PlaceController placeController, EventBus bus, Place defaultPlace,
			EntityFactory entityFactory, ViewFactory viewFactory) {
		this.session = session;
		this.placeController = placeController;
		this.bus = bus;
		this.defaultPlace = defaultPlace;
		this.entityFactory = entityFactory;
		this.viewFactory = viewFactory;

		init();
	}

	private void init() {
		if (session.isPlatformAdmin()) {
			view = getView();
			view.setPresenter(this);      
			view.init();
		} else {
			logger.warning("Hey, only admins are allowed to see this! "
					+ this.getClass().getName());
			placeController.goTo(defaultPlace);
		}
	}
	
	@Override
	public CourseVersion getNewCourseVersion() {
		return entityFactory.newCourseVersion().as();
	}

	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	private AdminCourseVersionView getView() {
		return viewFactory.getAdminCourseVersionView();
	}

	@Override
  public void upsertCourseVersion(CourseVersion courseVersion) {
		if(courseVersion.getUUID() == null){
			session.courseVersions().create(courseVersion, new Callback<CourseVersion>() {
				@Override
				public void ok(CourseVersion courseVersion) {
						LoadingPopup.hide();
						KornellNotification.show("Versão de curso criada com sucesso!");
						PlaceUtils.reloadCurrentPlace(bus, placeController);
				}		
				
				@Override
				public void conflict(String errorMessage){
					LoadingPopup.hide();
					KornellNotification.show(errorMessage, AlertType.ERROR, 2500);
				}
			});
		} else {
			session.courseVersion(courseVersion.getUUID()).update(courseVersion, new Callback<CourseVersion>() {
				@Override
				public void ok(CourseVersion courseVersion) {
						LoadingPopup.hide();
						KornellNotification.show("Alterações salvas com sucesso!");
						placeController.goTo(new AdminCourseVersionsPlace());
				}		
				
				@Override
				public void conflict(String errorMessage){
					LoadingPopup.hide();
					KornellNotification.show(errorMessage, AlertType.ERROR, 2500);
				}
			});
		}
  }
}
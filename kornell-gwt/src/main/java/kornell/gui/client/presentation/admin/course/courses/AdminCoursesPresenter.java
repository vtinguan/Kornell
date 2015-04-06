package kornell.gui.client.presentation.admin.course.courses;

import java.util.logging.Logger;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.to.CoursesTO;
import kornell.core.to.TOFactory;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.LoadingPopup;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;

public class AdminCoursesPresenter implements AdminCoursesView.Presenter {
	Logger logger = Logger.getLogger(AdminCoursesPresenter.class.getName());
	private AdminCoursesView view;
	FormHelper formHelper;
	private KornellSession session;
	private PlaceController placeController;
	private Place defaultPlace;
	TOFactory toFactory;
	private ViewFactory viewFactory;
	private CoursesTO coursesTO;

	public AdminCoursesPresenter(KornellSession session,
			PlaceController placeController,
			TOFactory toFactory, ViewFactory viewFactory) {
		this.session = session;
		this.placeController = placeController;
		this.toFactory = toFactory;
		this.viewFactory = viewFactory;
		formHelper = new FormHelper();
		init();
	}

	private void init() {
		if (session.isPlatformAdmin()) {
			view = getView();
			view.setPresenter(this);
			LoadingPopup.show();
  		session.courses().get(new Callback<CoursesTO>() {
  			@Override
  			public void ok(CoursesTO to) {
  				coursesTO = to;
  				view.setCourses(coursesTO.getCourses());
  				LoadingPopup.hide();
  			}
  		});
      
		} else {
			logger.warning("Hey, only admins are allowed to see this! "
					+ this.getClass().getName());
			placeController.goTo(defaultPlace);
		}
	}
	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	private AdminCoursesView getView() {
		return viewFactory.getAdminCoursesView();
	}
	
}
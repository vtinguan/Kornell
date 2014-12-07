package kornell.gui.client.presentation.admin.courseversion.courseversions;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseVersion;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.to.CourseVersionsTO;
import kornell.core.to.TOFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.presentation.util.FormHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;

public class AdminCourseVersionsPresenter implements AdminCourseVersionsView.Presenter {
	private AdminCourseVersionsView view;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	FormHelper formHelper;
	private KornellSession session;
	private PlaceController placeController;
	private Place defaultPlace;
	TOFactory toFactory;
	private ViewFactory viewFactory;
	private CourseVersionsTO courseVersionsTO;

	public AdminCourseVersionsPresenter(KornellSession session,
			PlaceController placeController, Place defaultPlace,
			TOFactory toFactory, ViewFactory viewFactory) {
		this.session = session;
		this.placeController = placeController;
		this.defaultPlace = defaultPlace;
		this.toFactory = toFactory;
		this.viewFactory = viewFactory;
		formHelper = new FormHelper();
		init();
	}

	private void init() {
		if (session.isPlatformAdmin()) {
			view = getView();
			view.setPresenter(this);
  		session.courseVersions().get(new Callback<CourseVersionsTO>() {
  			@Override
  			public void ok(CourseVersionsTO to) {
  				courseVersionsTO = to;
  				view.setCourseVersions(courseVersionsTO.getCourseVersions());
  			}
  		});
      
		} else {
			GWT.log("Hey, only admins are allowed to see this! "
					+ this.getClass().getName());
			placeController.goTo(defaultPlace);
		}
	}
	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	private AdminCourseVersionsView getView() {
		return viewFactory.getAdminCourseVersionsView();
	}

	@Override
  public void upsertCourseVersion(CourseVersion courseVersion) {
		if(courseVersion.getUUID() == null){
			/*session.courseClasses().create(courseVersion, new Callback<CourseClass>() {
				@Override
				public void ok(CourseClass courseClass) {
						LoadingPopup.hide();
						KornellNotification.show("Turma criada com sucesso!");
						CourseClassTO courseClassTO2 = Dean.getInstance().getCourseClassTO();
						if(courseClassTO2 != null)
							courseClassTO2.setCourseClass(courseClass);
						updateCourseClass(courseClass.getUUID());
				}
				
				@Override
				public void conflict(String errorMessage){
					LoadingPopup.hide();
					KornellNotification.show(errorMessage, AlertType.ERROR, 2500);
				}
			});*/
		} 
  }
}
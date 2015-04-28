package kornell.gui.client.presentation.admin.courseclass.courseclasses;

import java.util.Map;
import java.util.logging.Logger;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClass;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.error.KornellErrorTO;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.EnrollmentsTO;
import kornell.core.to.TOFactory;
import kornell.gui.client.KornellConstantsHelper;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;

public class AdminCourseClassesPresenter implements AdminCourseClassesView.Presenter {
	Logger logger = Logger.getLogger(AdminCourseClassesPresenter.class.getName());
	private AdminCourseClassesView view;
	FormHelper formHelper;
	private KornellSession session;
	private PlaceController placeController;
	private Place defaultPlace;
	TOFactory toFactory;
	private ViewFactory viewFactory;
	private CourseClassesTO courseClassesTO;
	private Map<String, EnrollmentsTO> enrollmentsCacheMap;
	private String pageSize = "20";
	private String pageNumber = "1";
	private String searchTerm = "";

	public AdminCourseClassesPresenter(KornellSession session,
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
		if (RoleCategory.hasRole(session.getCurrentUser().getRoles(),
				RoleType.courseClassAdmin) || session.isInstitutionAdmin()) {
			view = getView();
			view.setPresenter(this);
			
			String selectedCourseClass = "";
			updateCourseClass(selectedCourseClass);
      
		} else {
			logger.warning("Hey, only admins are allowed to see this! "
					+ this.getClass().getName());
			placeController.goTo(defaultPlace);
		}
	}

	@Override
	public void updateCourseClass(final String courseClassUUID) {
		LoadingPopup.show();
		view.setCourseClasses(null, 0, 0);
		session.courseClasses().getAdministratedCourseClassesTOByInstitution(Dean.getInstance().getInstitution().getUUID(), pageSize, pageNumber, searchTerm, 
				new Callback<CourseClassesTO>() {
			@Override
			public void ok(CourseClassesTO to) {
				courseClassesTO = to;
				view.setCourseClasses(courseClassesTO.getCourseClasses(), to.getCount(), to.getSearchCount());
				LoadingPopup.hide();
				if(courseClassesTO.getCourseClasses().size() == 0){
				} else {
					for (CourseClassTO courseClassTO : courseClassesTO.getCourseClasses()) {
						if (courseClassUUID == null || courseClassTO.getCourseClass().getUUID().equals(courseClassUUID)) {
							return;
						}
					}
				}
			}
		});
	}
	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	private AdminCourseClassesView getView() {
		return viewFactory.getAdminCourseClassesView();
	}
	
	@Override
	public void upsertCourseClass(CourseClass courseClass) {
		if(courseClass.getUUID() == null){
			courseClass.setCreatedBy(session.getCurrentUser().getPerson().getUUID());
			session.courseClasses().create(courseClass, new Callback<CourseClass>() {
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
				public void conflict(KornellErrorTO kornellErrorTO){
					LoadingPopup.hide();
					KornellNotification.show(KornellConstantsHelper.getConflictMessage(kornellErrorTO), AlertType.ERROR, 2500);
				}
			});
		} else {
	  	enrollmentsCacheMap.remove(courseClass.getUUID());
			session.courseClass(courseClass.getUUID()).update(courseClass, new Callback<CourseClass>() {
				@Override
				public void ok(CourseClass courseClass) {
						LoadingPopup.hide();
						KornellNotification.show("Alterações salvas com sucesso!");
						Dean.getInstance().getCourseClassTO().setCourseClass(courseClass);
						updateCourseClass(courseClass.getUUID());
				}		
				
				@Override
				public void conflict(KornellErrorTO kornellErrorTO){
					LoadingPopup.hide();
					KornellNotification.show(KornellConstantsHelper.getConflictMessage(kornellErrorTO), AlertType.ERROR, 2500);
				}
			});
		}
  }

	@Override
	public String getPageSize() {
		return pageSize;
	}

	@Override
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String getPageNumber() {
		return pageNumber;
	}

	@Override
	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

	@Override
	public String getSearchTerm() {
		return searchTerm;
	}

	@Override
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;	
	}

	@Override
	public void updateData() {
    	updateCourseClass("");
	}
}
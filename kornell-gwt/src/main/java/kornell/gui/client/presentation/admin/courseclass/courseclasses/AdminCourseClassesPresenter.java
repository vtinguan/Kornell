package kornell.gui.client.presentation.admin.courseclass.courseclasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentCategory;
import kornell.core.entity.EnrollmentProgressDescription;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.Enrollments;
import kornell.core.entity.RegistrationEnrollmentType;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.EnrollmentRequestTO;
import kornell.core.to.EnrollmentRequestsTO;
import kornell.core.to.EnrollmentTO;
import kornell.core.to.EnrollmentsTO;
import kornell.core.to.TOFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;
import kornell.gui.client.util.ClientProperties;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class AdminCourseClassesPresenter implements AdminCourseClassesView.Presenter {
	private AdminCourseClassesView view;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private List<EnrollmentTO> enrollmentTOs;
	private String batchEnrollmentErrors;
	private List<EnrollmentRequestTO> batchEnrollments;
	FormHelper formHelper;
	private KornellSession session;
	private PlaceController placeController;
	private Place defaultPlace;
	TOFactory toFactory;
	private ViewFactory viewFactory;
	private Boolean enrollWithCPFx = false;
	private Integer maxEnrollments = 0;
	private Integer numEnrollments = 0;
	private CourseClassesTO courseClassesTO;
	private boolean hasOverriddenEnrollments = false, overriddenEnrollmentsModalShown = false, confirmedEnrollmentsModal = false;
  private EnrollmentRequestsTO enrollmentRequestsTO;
  private List<EnrollmentTO> enrollmentsToOverride;
  private Map<String, EnrollmentsTO> enrollmentsCacheMap;
  
	private static final String PREFIX = ClientProperties.PREFIX + "AdminCourseClasses";

	public AdminCourseClassesPresenter(KornellSession session,
			PlaceController placeController, Place defaultPlace,
			TOFactory toFactory, ViewFactory viewFactory) {
		this.session = session;
		this.placeController = placeController;
		this.defaultPlace = defaultPlace;
		this.toFactory = toFactory;
		this.viewFactory = viewFactory;
		formHelper = new FormHelper();
		enrollmentRequestsTO = toFactory.newEnrollmentRequestsTO().as();
		enrollmentsCacheMap = new HashMap<String, EnrollmentsTO>();
		// TODO refactor permissions per session/activity

		init();
	}

	private void init() {
		if (RoleCategory.hasRole(session.getCurrentUser().getRoles(),
				RoleType.courseClassAdmin) || session.isInstitutionAdmin()) {
			view = getView();
			view.setPresenter(this);
			
			String selectedCourseClass = "-----------------------------";
      updateCourseClass(selectedCourseClass);
      
		} else {
			GWT.log("Hey, only admins are allowed to see this! "
					+ this.getClass().getName());
			placeController.goTo(defaultPlace);
		}
	}

	@Override
	public void updateCourseClass(final String courseClassUUID) {
		LoadingPopup.show();
		session.courseClasses().getAdministratedCourseClassesTOByInstitution(Dean.getInstance().getInstitution().getUUID(), 
				new Callback<CourseClassesTO>() {
			@Override
			public void ok(CourseClassesTO to) {
				courseClassesTO = to;
				view.setCourseClasses(courseClassesTO.getCourseClasses());
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
				public void conflict(String errorMessage){
					LoadingPopup.hide();
					KornellNotification.show(errorMessage, AlertType.ERROR, 2500);
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
				public void conflict(String errorMessage){
					LoadingPopup.hide();
					KornellNotification.show(errorMessage, AlertType.ERROR, 2500);
				}
			});
		}
  }
}
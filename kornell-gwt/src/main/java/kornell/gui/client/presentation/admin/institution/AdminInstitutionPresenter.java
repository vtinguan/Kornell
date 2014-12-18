package kornell.gui.client.presentation.admin.institution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClass;
import kornell.core.entity.Institution;
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
import kornell.gui.client.presentation.admin.courseversion.courseversions.AdminCourseVersionsPresenter;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;
import kornell.gui.client.util.ClientProperties;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;

public class AdminInstitutionPresenter implements AdminInstitutionView.Presenter {
	Logger logger = Logger.getLogger(AdminInstitutionPresenter.class.getName());
	private AdminInstitutionView view;
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
  
	private static final String PREFIX = ClientProperties.PREFIX + "AdminInstitution";

	public AdminInstitutionPresenter(KornellSession session,
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
		if (session.isInstitutionAdmin()) {
			view = getView();
			view.setPresenter(this);      
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

	private AdminInstitutionView getView() {
		return viewFactory.getAdminInstitutionView();
	}

	@Override
  public void updateInstitution(Institution institution) {
			session.institution(institution.getUUID()).update(institution, new Callback<Institution>() {
				@Override
				public void ok(Institution institution) {
						LoadingPopup.hide();
						KornellNotification.show("Alterações salvas com sucesso!");
						Dean.getInstance().setInstitution(institution);
				}		
				
				@Override
				public void conflict(String errorMessage){
					LoadingPopup.hide();
					KornellNotification.show(errorMessage, AlertType.ERROR, 2500);
				}
			});
  }
}
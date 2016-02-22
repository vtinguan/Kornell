package kornell.gui.client.personnel;

import java.util.logging.Logger;

import kornell.api.client.KornellSession;
import kornell.core.entity.ContentSpec;
import kornell.core.to.UserInfoTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.event.LoginEventHandler;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceChangeRequestEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Manages navigation
 * 
 * @author faermanj
 */
public class Captain implements LogoutEventHandler, LoginEventHandler {
	Logger logger = Logger.getLogger(Captain.class.getName());
	private PlaceController placeCtrl;
	private KornellSession session;
	
	private static KornellConstants constants = GWT.create(KornellConstants.class);

	public Captain(EventBus bus, KornellSession session, final PlaceController placeCtrl) {
		this.placeCtrl = placeCtrl;
		this.session = session;
		bus.addHandler(LogoutEvent.TYPE, this);
		bus.addHandler(LoginEvent.TYPE, this);
		bus.addHandler(PlaceChangeRequestEvent.TYPE, new PlaceChangeRequestEvent.Handler() {
			@Override
			public void onPlaceChangeRequest(PlaceChangeRequestEvent event) {
				// if the user tries to go from one classroom to another, don't show this message
				if (!(placeCtrl.getWhere() instanceof ClassroomPlace && event.getNewPlace() instanceof ClassroomPlace)) {
					// if the user is inside the classroom and doesn't try to go to
					// the vitrine (by logging out, for example)
					if (placeCtrl.getWhere() instanceof ClassroomPlace && !(event.getNewPlace() instanceof VitrinePlace)) {
						// if the user hasn't passed the class and the type of the
						// version isn't KNL (small htmls, user won't lose progress)
						if (Dean.getInstance().getCourseClassTO() != null
								&& Dean.getInstance().getCourseClassTO().getCourseClass() != null
								&& ContentSpec.SCORM12.equals(Dean.getInstance().getCourseClassTO().getCourseVersionTO()
										.getCourseVersion().getContentSpec())
								&& Dean.getInstance().getCourseClassTO().getEnrollment() != null
								&& Dean.getInstance().getCourseClassTO().getEnrollment().getCertifiedAt() == null) {
							//If there is a token cookie, a certificate is being generated. Bypass confirm on this case.
							if(StringUtils.isNone(ClientProperties.getCookie(ClientProperties.X_KNL_TOKEN)) && Dean.getInstance().getCourseClassTO() != null){
								event.setWarning(constants.leavingTheClassroom());	
							}
						}
					}
				}
			}
		});
	}

	@Override
	public void onLogout() {
		session.logout();
		placeCtrl.goTo(VitrinePlace.instance);
		Window.Location.reload();
	}

	@Override
	public void onLogin(UserInfoTO user) {
		logger.info("User logged in as " + user.getUsername());
	}

}

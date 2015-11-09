package kornell.gui.client.personnel;

import java.util.logging.Logger;

import kornell.api.client.KornellSession;
import kornell.core.entity.ContentSpec;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.event.LoginEventHandler;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.vitrine.VitrinePlace;

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

	public Captain(EventBus bus, KornellSession session, final PlaceController placeCtrl) {
		this.placeCtrl = placeCtrl;
		this.session = session;
		bus.addHandler(LogoutEvent.TYPE, this);
		bus.addHandler(LoginEvent.TYPE, this);
		bus.addHandler(PlaceChangeRequestEvent.TYPE, new PlaceChangeRequestEvent.Handler() {
			@Override
			public void onPlaceChangeRequest(PlaceChangeRequestEvent event) {
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
						event.setWarning("Tem certeza que deseja sair do curso? Seu progresso desde o último salvamento poderá ser perdido.");
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

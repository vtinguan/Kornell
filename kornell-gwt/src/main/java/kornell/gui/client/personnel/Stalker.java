package kornell.gui.client.personnel;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.GenericClientFactoryImpl;
import kornell.gui.client.event.ActomEnteredEvent;
import kornell.gui.client.event.ActomEnteredEventHandler;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.event.LoginEventHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Tracks user
 * 
 * @author faermanj
 */
public class Stalker implements ActomEnteredEventHandler, LoginEventHandler {
	EventBus bus;
	KornellSession session;
	PlaceHistoryMapper mapper;

	private Timer seuInacioTimer;

	public Stalker(EventBus bus, KornellSession session) {
		this.bus = bus;
		this.session = session;

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
		      @Override
		      public void execute() {
		    		signAttendanceSheet();
		      }
		});

		seuInacioTimer = new Timer() {
			public void run() {
				signAttendanceSheet();
			}
		};

		// Schedule the timer to run daily
		seuInacioTimer.scheduleRepeating(24 * 60 * 60 * 1000);
		
		bus.addHandler(ActomEnteredEvent.TYPE, this);
		bus.addHandler(LoginEvent.TYPE, this);
	}

	private void signAttendanceSheet() {
		if (session.isAnonymous())
			return;
		session.events()
				.attendanceSheetSigned(
						GenericClientFactoryImpl.DEAN.getInstitution().getUUID(),
						session.getCurrentUser().getPerson().getUUID())
				.fire(new Callback<Void>() {
					@Override
					public void ok(Void to) {
						//
					}
				});
	}

	@Override
	public void onActomEntered(ActomEnteredEvent event) {
		session.events()
				.actomEntered(event.getEnrollmentUUID(), event.getActomKey())
				.fire();
	}

	@Override
	public void onLogin(UserInfoTO user) {
		signAttendanceSheet();
	}
}

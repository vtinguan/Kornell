package kornell.gui.client.personnel;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.event.ActomEnteredEvent;
import kornell.gui.client.event.ActomEnteredEventHandler;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.event.LoginEventHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;

public class Stalker implements ActomEnteredEventHandler, LoginEventHandler {
	private KornellSession session;
	private Timer seuInacioTimer;
	
	public Stalker(EventBus bus, KornellSession session) {
		this.session = session;
		
		scheduleAttendanceSheetSigning();
		
		seuInacioTimer = new Timer() {
			public void run() {
				scheduleAttendanceSheetSigning();
			}
		};

		// Schedule the timer to run daily
		seuInacioTimer.scheduleRepeating(24 * 60 * 60 * 1000);

		bus.addHandler(ActomEnteredEvent.TYPE, this);
		bus.addHandler(LoginEvent.TYPE, this);
	}
	
	private void scheduleAttendanceSheetSigning(){
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				signAttendanceSheet();
			}
		});
	}

	private void signAttendanceSheet() {
		if (session.isAnonymous()) return;
		String institutionUUID = session.getInstitution().getUUID();
		String personUUID = session.getCurrentUser().getPerson().getUUID();
		session.events().attendanceSheetSigned(institutionUUID, personUUID).fire(new Callback<Void>() {
			@Override
			public void ok(Void to) { 
				/* nothing to do */
			}
		});
	}

	@Override
	public void onActomEntered(ActomEnteredEvent event) {
		session.events().actomEntered(event.getEnrollmentUUID(), event.getActomKey()).fire();
	}

	@Override
	public void onLogin(UserInfoTO user) {
		scheduleAttendanceSheetSigning();
	}
}

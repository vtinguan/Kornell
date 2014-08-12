package kornell.gui.client.sequence;

import java.util.logging.Logger;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.lom.Contents;
import kornell.core.to.ActionTO;
import kornell.core.to.LaunchEnrollmentTO;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.uidget.ExternalPageView;
import kornell.gui.client.uidget.OpenURLView;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Follow server-side evaluation and actions.
 */
public class ThinSequencer implements Sequencer{
	public static final Logger log = Logger.getLogger(ThinSequencer.class.getName());
	private FlowPanel contentPanel;
	private ClassroomPlace place;
	private EventBus bus;
	private KornellSession session;

	public ThinSequencer(EventBus bus, KornellSession session) {
		this.bus = bus;
		this.session = session;
	}

	@Override
	public void onContinue(NavigationRequest event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPrevious(NavigationRequest event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDirect(NavigationRequest event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Sequencer withPanel(FlowPanel contentPanel) {
		this.contentPanel = contentPanel;
		return this;
	}

	@Override
	public Sequencer withPlace(ClassroomPlace place) {
		this.place = place;
		return this;
	}

	@Override
	public void go(Contents contents) {
		final String enrollmentUUID = place.getEnrollmentUUID();
		log.info("Launching Enrollment ["+enrollmentUUID+"]");
		session.enrollment(enrollmentUUID).launch(new Callback<LaunchEnrollmentTO>() {

			@Override
			public void ok(LaunchEnrollmentTO to) {
				log.info("LAUNCH");
				log.info(to.toString());
				ActionTO actionTO = to.getActionTO();
			
				String url = actionTO.getProperties().get("href");
				OpenURLView view = new OpenURLView(url);
				
				contentPanel.clear();
				contentPanel.add(view);
			}
			
		});
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fireProgressEvent() {
		// TODO Auto-generated method stub
		
	}

}
